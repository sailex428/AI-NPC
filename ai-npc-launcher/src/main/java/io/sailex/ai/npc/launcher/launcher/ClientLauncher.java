package io.sailex.ai.npc.launcher.launcher;

import io.sailex.ai.npc.launcher.config.ModConfig;
import io.sailex.ai.npc.launcher.constants.ConfigConstants;
import io.sailex.ai.npc.launcher.constants.ModRepositories;
import io.sailex.ai.npc.launcher.util.LogUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import me.earth.headlessmc.api.command.CommandException;
import me.earth.headlessmc.api.exit.ExitManager;
import me.earth.headlessmc.launcher.Launcher;
import me.earth.headlessmc.launcher.LauncherBuilder;
import me.earth.headlessmc.launcher.auth.AuthException;
import me.earth.headlessmc.launcher.auth.LaunchAccount;
import me.earth.headlessmc.launcher.auth.ValidatedAccount;
import me.earth.headlessmc.launcher.command.FabricCommand;
import me.earth.headlessmc.launcher.command.download.DownloadCommand;
import me.earth.headlessmc.launcher.command.download.VersionInfo;
import me.earth.headlessmc.launcher.files.FileManager;
import me.earth.headlessmc.launcher.launch.LaunchOptions;
import me.earth.headlessmc.launcher.specifics.VersionSpecificException;
import me.earth.headlessmc.launcher.version.Version;
import me.earth.headlessmc.launcher.version.VersionImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

/**
 * Launcher for the AI-NPC client.
 */
public class ClientLauncher {

	@Getter
	private Launcher launcher;

	private final ClientProcessManager npcClientProcesses;
	private FileManager files;
	private Version version;

	public ClientLauncher(ClientProcessManager npcClientProcesses) {
		this.npcClientProcesses = npcClientProcesses;
	}

	/**
	 * Launches the AI-NPC client asynchronously.
	 *
	 * @param npcName   the name of the NPC
	 * @param llmType   the type of the LLM
	 * @param llmModel  the model of the LLM
	 * @param isOnline whether to login online or offline
	 */
	public void launchAsync(String npcName, String llmType, String llmModel, boolean isOnline) {
		LaunchAccount account = getAccount(npcName, isOnline);
		if (account == null) {
			LogUtil.error("Account could not be found. Please login first using the /login command.");
			return;
		}
		CompletableFuture.runAsync(() -> launch(account, npcName, llmType, llmModel))
				.exceptionally(e -> {
					LogUtil.error(e.getMessage());
					return null;
				});
	}

	private void launch(LaunchAccount account, String npcName, String llmType, String llmModel) {
		try {
			LaunchOptions options = LaunchOptions.builder()
					.account(account)
					.additionalJvmArgs(getJvmArgs(llmType, llmModel))
					.version(version)
					.launcher(launcher)
					.files(files)
					.parseFlags(launcher, false)
					.lwjgl(Boolean.parseBoolean(ModConfig.getProperty(ConfigConstants.NPC_IS_HEADLESS)))
					.prepare(false)
					.build();

			Process process = launcher.getProcessFactory().run(options);

			if (process == null) {
				launcher.getExitManager().exit(0);
				LogUtil.error("Failed to launch the game.");
			}

			npcClientProcesses.addProcess(npcName, process);
			LogUtil.info("Launching AI-NPC client.");
		} catch (Exception e) {
			LogUtil.error("Failed to setup or launch the game.");
		}
	}

	/**
	 * Initializes the launcher asynchronously.
	 */
	public void initLauncherAsync() {
		CompletableFuture.runAsync(this::initLauncher).exceptionally(e -> {
			LogUtil.error(e.getMessage());
			return null;
		});
	}

	/**
	 * Initializes the launcher.
	 * Downloads and installs the Fabric client and the AI-NPC mod.
	 */
	private void initLauncher() {
		try {
			LauncherBuilder builder = new LauncherBuilder();
			builder.exitManager(new ExitManager());
			launcher = builder.buildDefault();

			if (launcher == null) {
				LogUtil.error("Failed to initialize the FileManager.");
			}

			setMcDir();
			this.files =
					launcher.getFileManager().createRelative(UUID.randomUUID().toString());

			String versionName = SharedConstants.getGameVersion().getName();
			version = findOrDownloadFabric(versionName);

			installAiNpcClientMod(version);
		} catch (AuthException e) {
			LogUtil.error("Failed to authenticate.");
		} catch (CommandException e) {
			LogUtil.error("Failed to download/install the game.");
		}
	}

	private void setMcDir() {
		String gameDir =
				FabricLoader.getInstance().getGameDir().toAbsolutePath().toString();
		FileManager fileManager = new FileManager(Path.of(gameDir, "ai-npcs").toString());
		LogUtil.info("Setting game directory to: " + fileManager.getPath(), true);

		launcher.getLauncherConfig().setMcFiles(fileManager);
		launcher.getLauncherConfig().setGameDir(fileManager);
	}

	private Version findOrDownloadFabric(String versionName) throws CommandException {
		Version version = getVersion(versionName);
		if (version != null) {
			return version;
		}
		LogUtil.info("No Fabric client found for version '" + versionName
				+ "'. Initiating download of the Fabric client...");
		Version neededVersion = VersionImpl.builder().name(versionName).build();

		DownloadCommand downloadCommand = new DownloadCommand(launcher);
		// cache version infos
		downloadCommand.execute(versionName);
		// get the needed version info
		VersionInfo versionInfo = null;
		for (VersionInfo info : downloadCommand.getIterable()) {
			if (info.getName().equals(versionName)) {
				versionInfo = info;
				break;
			}
		}
		// download vanilla
		downloadCommand.execute(versionInfo);

		// download fabric
		FabricCommand fabricCommand = new FabricCommand(launcher);
		fabricCommand.execute(neededVersion);

		return getVersion(versionName);
	}

	private Version getVersion(String versionName) {
		for (Version fabricVersion : launcher.getVersionService().getContents()) {
			if (fabricVersion.getName().contains(versionName) && fabricVersion.getName().contains("fabric")) {
				return fabricVersion;
			}
		}
		return null;
	}

	private void installAiNpcClientMod(Version version) {
		try {
			LogUtil.info("Downloading latest AI-NPC mod.");
			launcher.getVersionSpecificModManager().download(version, ModRepositories.AI_NPC);

			LogUtil.info("Installing AI-NPC mod.");
			launcher.getVersionSpecificModManager()
					.install(
							version,
							ModRepositories.AI_NPC,
							Path.of(launcher.getLauncherConfig().getMcFiles().getPath(), "mods"));
		} catch (VersionSpecificException | IOException e) {
			LogUtil.error("Failed to download/install AI-NPC mod.");
		}
	}

	private LaunchAccount getAccount(String npcName, boolean isOnline) {
		if (!isOnline) {
			LogUtil.info("Logging in offline.");
			return new LaunchAccount("msa", npcName, UUID.randomUUID().toString(), "", "");
		}
		LogUtil.info("Looking for logged in account with name: " + npcName);
		return launcher.getAccountManager().getAccounts().stream()
				.map(ValidatedAccount::toLaunchAccount)
				.filter(account -> account.getName().equals(npcName))
				.findFirst()
				.orElse(null);
	}

	private List<String> getJvmArgs(String llmType, String llmModel) {
		List<String> jvmArgs = new ArrayList<>();
		addServerAddressJvmArg(jvmArgs);
		jvmArgs.add(buildJvmArg(ConfigConstants.NPC_LLM_TYPE, llmType));

		if (llmType.equals("ollama")) {
			jvmArgs.addAll(List.of(
					buildJvmArg(
							ConfigConstants.NPC_LLM_OLLAMA_URL,
							ModConfig.getProperty(ConfigConstants.NPC_LLM_OLLAMA_URL)),
					buildJvmArg(ConfigConstants.NPC_LLM_OLLAMA_MODEL, llmModel)));
		}

		if (llmType.equals("openai")) {
			String apiKey = ModConfig.getProperty(ConfigConstants.NPC_LLM_OPENAI_API_KEY);
			if (apiKey == null || apiKey.isEmpty()) {
				LogUtil.error("OpenAI API key is missing.");
				return Collections.emptyList();
			}
			jvmArgs.addAll(List.of(
					buildJvmArg(ConfigConstants.NPC_LLM_OPENAI_MODEL, llmModel),
					buildJvmArg(ConfigConstants.NPC_LLM_OPENAI_API_KEY, apiKey),
					buildJvmArg(
							ConfigConstants.NPC_LLM_OPENAI_EMBEDDING_MODEL,
							ModConfig.getProperty(ConfigConstants.NPC_LLM_OPENAI_EMBEDDING_MODEL)),
					buildJvmArg(
							ConfigConstants.NPC_LLM_OPENAI_BASE_URL,
							ModConfig.getProperty(ConfigConstants.NPC_LLM_OPENAI_BASE_URL))));
		}
		return jvmArgs;
	}

	private void addServerAddressJvmArg(List<String> jvmArgs) {
		String serverPort = ModConfig.getProperty(ConfigConstants.NPC_SERVER_PORT);
		jvmArgs.add(buildJvmArg(ConfigConstants.NPC_SERVER_IP, "localhost"));
		jvmArgs.add(buildJvmArg(ConfigConstants.NPC_SERVER_PORT, serverPort));
	}

	private String buildJvmArg(String key, String value) {
		String argPrefix = "-D";
		return argPrefix + key + "=" + value;
	}
}
