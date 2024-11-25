package io.sailex.aiNpcLauncher.commands;

import static net.minecraft.server.command.CommandManager.literal;

import io.sailex.aiNpcLauncher.launcher.ClientLauncher;
import io.sailex.aiNpcLauncher.launcher.ClientProcessManager;
import lombok.AllArgsConstructor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

@AllArgsConstructor
public class CommandManager {

	private final ClientLauncher clientLauncher;
	private final ClientProcessManager clientProcessManager;

	public void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			new SetConfigCommand().register(dispatcher);
			dispatcher.register(literal("npc")
					.requires(source -> source.hasPermissionLevel(2))
					.then(new NPCCreateCommand(clientLauncher).getCommand())
					.then(new NPCRemoveCommand(clientProcessManager).getCommand()));
		});
	}
}
