package io.sailex.ai.npc.launcher.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.sailex.ai.npc.launcher.config.LauncherConfig;
import io.sailex.ai.npc.launcher.constants.ConfigConstants;
import io.sailex.ai.npc.launcher.launcher.ClientLauncher;
import io.sailex.ai.npc.launcher.util.LogUtil;
import java.util.Set;
import net.minecraft.server.command.ServerCommandSource;

public class NPCCreateCommand {

	private static final String LLM_TYPE = "llm-type";
	private static final String LLM_MODEL = "llm-model";
	private static final String IS_ONLINE_COMMAND = "isOnline";

	private static final Set<String> allowedLLMTypes = Set.of("ollama", "openai");

	private final ClientLauncher clientLauncher;
	private final LauncherConfig config;

	public NPCCreateCommand(ClientLauncher clientLauncher, LauncherConfig config) {
		this.clientLauncher = clientLauncher;
		this.config = config;
	}

	public LiteralArgumentBuilder<ServerCommandSource> getCommand() {
		return literal("add")
				.requires(source -> source.hasPermissionLevel(2))
				.then(argument("name", StringArgumentType.string())
						.then(argument(IS_ONLINE_COMMAND, BoolArgumentType.bool())
								.executes(this::createNPC)
								.then(argument(LLM_TYPE, StringArgumentType.string())
										.suggests((context, builder) -> {
											for (String llmType : allowedLLMTypes) {
												builder.suggest(llmType);
											}
											return builder.buildFuture();
										})
										.then(argument(LLM_MODEL, StringArgumentType.string())
												.executes(this::createNPCWithLLM)))));
	}

	private int createNPC(CommandContext<ServerCommandSource> context) {
		String name = StringArgumentType.getString(context, "name");
		boolean isOnline = BoolArgumentType.getBool(context, IS_ONLINE_COMMAND);

		LogUtil.info("Creating NPC with name: " + name);

		String type = config.getProperty(ConfigConstants.NPC_LLM_TYPE);

		clientLauncher.launchAsync(name, type, getLlmModel(type), isOnline);
		return 1;
	}

	private int createNPCWithLLM(CommandContext<ServerCommandSource> context) {
		String name = StringArgumentType.getString(context, "name");
		boolean isOnline = BoolArgumentType.getBool(context, IS_ONLINE_COMMAND);
		String llmType = StringArgumentType.getString(context, LLM_TYPE);
		String llmModel = StringArgumentType.getString(context, LLM_MODEL);

		LogUtil.info(("Creating NPC with name: " + name + ", LLM Type: " + llmType + ", LLM Model: " + llmModel));

		clientLauncher.launchAsync(name, llmType, llmModel, isOnline);
		return 1;
	}

	private String getLlmModel(String type) {
		if (type.equals("ollama")) {
			return config.getProperty(ConfigConstants.NPC_LLM_OLLAMA_MODEL);
		} else if (type.equals("openai")) {
			return config.getProperty(ConfigConstants.NPC_LLM_OPENAI_MODEL);
		} else {
			throw new IllegalArgumentException("Invalid LLM type: " + type);
		}
	}
}
