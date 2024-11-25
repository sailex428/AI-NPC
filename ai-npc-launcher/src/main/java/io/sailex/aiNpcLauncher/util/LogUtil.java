package io.sailex.aiNpcLauncher.util;

import io.sailex.aiNpcLauncher.AiNPCLauncher;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

	private static final Logger LOGGER = LogManager.getLogger(LogUtil.class);
	private static final String PREFIX = "[§5AI-NPC§f] ";

	public static void info(String message) {
		info(message, false);
	}

	public static void info(String message, boolean onlyInConsole) {
		String formattedMessage = PREFIX + message;
		if (onlyInConsole) {
			LOGGER.info(formattedMessage);
		} else {
			log(formattedMessage);
		}
	}

	public static void error(String message) {
		String formattedMessage = PREFIX + "§c" + message;
		log(formattedMessage);
	}

	private static void log(String formattedMessage) {
		AiNPCLauncher.server.getPlayerManager().broadcast(Text.of(formattedMessage), false);
	}
}
