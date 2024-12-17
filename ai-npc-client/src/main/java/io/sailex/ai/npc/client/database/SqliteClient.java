package io.sailex.ai.npc.client.database;

import io.sailex.ai.npc.client.AiNPCClient;

import java.io.File;
import java.sql.*;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SQLite client for managing the database.
 */
public class SqliteClient {

	private static final Logger LOGGER = LogManager.getLogger(SqliteClient.class);
	private Connection connection;

	/**
	 * Create the database and tables.
	 */
	public void initDatabase(String databaseName) {
		String databasePath = initDataBaseDir();
		try {
			String jdbcUrl = String.format("jdbc:sqlite:%s/%s.db", databasePath, databaseName);
			connection = DriverManager.getConnection(jdbcUrl);
			if (connection.isValid(3)) {
				LOGGER.info("Database created or opened at: {}", databasePath);
			}
		} catch (SQLException e) {
			LOGGER.error("Error creating/connecting to database: {}", e.getMessage());
		}
	}

	private String initDataBaseDir() {
		File configDir = FabricLoader.getInstance().getConfigDir().toFile();
		File sqlDbDir = new File(configDir, AiNPCClient.MOD_ID + "_db");
		if (sqlDbDir.mkdirs()) {
			LOGGER.info("Database directory created at: {}", sqlDbDir.getAbsolutePath());
		}
		return sqlDbDir.getAbsolutePath();
	}

	/**
	 * Select data from the database.
	 */
	public ResultSet query(String sql) {
		try (Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(sql)) {
			LOGGER.info("Selected successfully: {} : {}", sql, resultSet);
			return resultSet;
		} catch (SQLException e) {
			LOGGER.error("Error selecting rule: {}", e.getMessage());
			return null;
		}
	}

	public void executeQuery(String sql) {
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
			LOGGER.info("Executed {} successfully", sql);
		} catch (SQLException e) {
			LOGGER.error("Error executing query {} : {}", sql, e.getMessage());
		}
	}

	/**
	 * Close the database connection.
	 */
	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				LOGGER.info("Database connection closed.");
			}
		} catch (SQLException e) {
			LOGGER.error("Error closing database connection: {}", e.getMessage());
		}
	}
}