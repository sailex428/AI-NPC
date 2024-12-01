package io.sailex.aiNpc.client;

import io.sailex.aiNpc.client.config.Config;
import io.sailex.aiNpc.client.constant.ConfigConstants;
import io.sailex.aiNpc.client.database.SqliteClient;
import io.sailex.aiNpc.client.listener.EventListenerManager;
import io.sailex.aiNpc.client.llm.ILLMClient;
import io.sailex.aiNpc.client.llm.OllamaClient;
import io.sailex.aiNpc.client.llm.OpenAiClient;
import io.sailex.aiNpc.client.model.NPC;
import io.sailex.aiNpc.client.npc.NPCContextGenerator;
import io.sailex.aiNpc.client.npc.NPCController;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class for the AI NPC client mod.
 * Initializes the NPC, its components, and connects to the server.
 */
@Getter
public class AiNPCClient implements ClientModInitializer {

	private static final Logger LOGGER = LogManager.getLogger(AiNPCClient.class);
	public static final String MOD_ID = "ai_npc";
	public static final MinecraftClient client = MinecraftClient.getInstance();

	private boolean npcInitialized = false;
	private boolean connected = false;

	/**
	 * Initialize the client mod.
	 * Waits for the client to load and then init the NPC and connects to the server.
	 */
	@Override
	public void onInitializeClient() {
		waitForClientToLoad();
	}

	private void waitForClientToLoad() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.isFinishedLoading() && !connected) {
				connectToServer();
				connected = true;
			}
		});
		ClientTickEvents.END_WORLD_TICK.register(client -> {
			if (!npcInitialized) {
				LOGGER.info("Initialize NPC");
				initializeNpc();
				npcInitialized = true;
			}
		});
	}

	private void connectToServer() {
		String serverName = Config.getProperty(ConfigConstants.NPC_SERVER_IP);
		String port = Config.getProperty(ConfigConstants.NPC_SERVER_PORT);

		ConnectScreen.connect(
				client.currentScreen,
				client,
				new ServerAddress(serverName, Integer.parseInt(port)),
				new ServerInfo("server", serverName, ServerInfo.ServerType.OTHER),
				false
				/*? if >=1.21.1 {*//*, null*//*?}*/
		);
	}

	private void initializeNpc() {
		ClientPlayerEntity npcEntity = client.player;
		if (npcEntity == null) {
			LOGGER.error("NPC entity is null");
			return;
		}

		ILLMClient llmService;
		String npcType = Config.getProperty(ConfigConstants.NPC_LLM_TYPE);
		if ("ollama".equals(npcType)) {
			String ollamaModel = Config.getProperty(ConfigConstants.NPC_LLM_OLLAMA_MODEL);
			String ollamaUrl = Config.getProperty(ConfigConstants.NPC_LLM_OLLAMA_URL);
			llmService = new OllamaClient(ollamaModel, ollamaUrl);
		} else if ("openai".equals(npcType)) {
			String apiKey = Config.getProperty(ConfigConstants.NPC_LLM_OPENAI_API_KEY);
			String openAiModel = Config.getProperty(ConfigConstants.NPC_LLM_OPENAI_MODEL);
			llmService = new OpenAiClient(openAiModel, apiKey);
		} else {
			LOGGER.error("Invalid LLM type: {}", npcType);
			return;
		}

		NPCContextGenerator npcContextGenerator = new NPCContextGenerator(npcEntity);
		NPCController controller = new NPCController(npcEntity, llmService, npcContextGenerator);
		NPC npc = new NPC(npcEntity.getUuid(), npcEntity, controller, npcContextGenerator, llmService);

		SqliteClient sqliteClient = new SqliteClient();
		sqliteClient.initDatabase();

		EventListenerManager eventListenerManager = new EventListenerManager(npc);
		eventListenerManager.register(sqliteClient);
	}
}
