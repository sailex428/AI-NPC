package io.sailex.aiNpcLauncher.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.sailex.aiNpcLauncher.config.ModConfig;
import io.sailex.aiNpcLauncher.constants.ConfigConstants;
import io.sailex.aiNpcLauncher.util.LogUtil;
import net.minecraft.server.command.ServerCommandSource;

public class SetConfigCommand {

	public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("setconfig")
				.requires(source -> source.hasPermissionLevel(2))
				.then(argument("key", StringArgumentType.word())
						.suggests((context, builder) -> {
							for (String key : ConfigConstants.ALLOWED_KEYS) {
								builder.suggest(key);
							}
							return builder.buildFuture();
						})
						.then(argument("value", StringArgumentType.string()).executes(this::setConfig))));
	}

	private int setConfig(CommandContext<ServerCommandSource> context) {
		String propertyKey = StringArgumentType.getString(context, "key");
		String propertyValue = StringArgumentType.getString(context, "value");

		if (!ConfigConstants.ALLOWED_KEYS.contains(propertyKey)) {
			LogUtil.error("Invalid property key!");
			return 0;
		}

		if (ModConfig.setProperty(propertyKey, propertyValue)) {
			LogUtil.info("Saved property successfully!");
			return 1;
		}

		LogUtil.error("Failed to save property!");
		return 0;
	}
}
