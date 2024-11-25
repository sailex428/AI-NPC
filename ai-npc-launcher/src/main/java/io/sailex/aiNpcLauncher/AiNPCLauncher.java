package io.sailex.aiNpcLauncher;

import io.sailex.aiNpcLauncher.commands.CommandManager;
import io.sailex.aiNpcLauncher.config.ModConfig;
import io.sailex.aiNpcLauncher.launcher.ClientLauncher;
import io.sailex.aiNpcLauncher.launcher.ClientProcessManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class AiNPCLauncher implements ModInitializer {

	public static MinecraftServer server;
	public static final String MOD_ID = "ai-npc-launcher";

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> server = minecraftServer);

		ModConfig.init();

		ClientProcessManager npcClientProcessManager = new ClientProcessManager();

		ClientLauncher clientLauncher = new ClientLauncher(npcClientProcessManager);

		CommandManager commandManager = new CommandManager(clientLauncher, npcClientProcessManager);
		commandManager.register();
	}
}
