package io.sailex.aiNpc.client.config;

import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to read properties from the system
 */
public class Config {

	private static final Logger LOGGER = LogManager.getLogger(Config.class);
	private static final Properties properties = System.getProperties();

	/**
	 * Get the property value for the given key
	 *
	 * @param key the key
	 * @return the property value
	 */
	public static String getProperty(String key) {
		return validateProperty(properties.getProperty(key));
	}

	private static String validateProperty(String property) {
		LOGGER.info("Property: {}", property);
		if (property == null) {
			LOGGER.error("Property is null");
			return null;
		}
		return property;
	}
}
