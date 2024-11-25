package io.sailex.aiNpc.client.database;

import io.sailex.aiNpc.client.AiNPCClient;
import io.sailex.aiNpc.client.model.db.Skill;
import java.io.File;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SQLite client for managing the database of minecraft skills.
 */
public class SqliteClient {
	private static final Logger LOGGER = LogManager.getLogger(SqliteClient.class);
	private Connection connection;

	/**
	 * Create the database and tables.
	 */
	public void initDatabase() {
		String databasePath = initDataBaseDir();
		try {
			String jdbcUrl = "jdbc:sqlite:" + databasePath + "/skills.db";
			connection = DriverManager.getConnection(jdbcUrl);
			if (connection == null) {
				return;
			}
			if (connection.isValid(15)) {
				createRulesTable();
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

	private void createRulesTable() {
		try (Statement statement = connection.createStatement()) {
			statement.execute(
					"""
				CREATE TABLE IF NOT EXISTS skills (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					name TEXT NOT NULL,
					description TEXT,
					example TEXT,
					embedding BLOB,
					created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
				)
			""");
			LOGGER.info("Database tables created successfully");
		} catch (SQLException e) {
			LOGGER.error("Error creating tables: {}", e.getMessage());
		}
	}

	/**
	 * Add a minecraft skill to the database.
	 *
	 * @param name        the name of the rule
	 * @param description the description of the rule
	 * @param embedding   the embedding of the rule
	 */
	public void addSkill(String name, String description, String example, float[] embedding) {
		String sql = "INSERT INTO skills (name, description, example, embedding) VALUES (?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, name);
			statement.setString(2, description);
			statement.setString(3, example);
			statement.setBytes(3, convertToBytes(embedding));
			statement.executeUpdate();
			LOGGER.info("Rule added successfully: {}", name);
		} catch (SQLException e) {
			LOGGER.error("Error adding rule: {}", e.getMessage());
		}
	}

	/**
	 * Select all minecraft skills from the database.
	 *
	 * @return a list of skills
	 */
	public List<Skill> selectSkills() {
		List<Skill> skills = new ArrayList<>();
		String sql = "SELECT * FROM skills";
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				Skill skill = new Skill(
						resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getString("description"),
						resultSet.getString("example"),
						convertToFloats(resultSet.getBytes("embedding")));
				skills.add(skill);
				LOGGER.info(skill);
			}
		} catch (SQLException e) {
			LOGGER.error("Error selecting rule: {}", e.getMessage());
		}
		return skills;
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

	private float[] convertToFloats(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		float[] embedding = new float[bytes.length / 4];
		buffer.asFloatBuffer().get(embedding);
		return embedding;
	}

	private byte[] convertToBytes(float[] embedding) {
		ByteBuffer buffer = ByteBuffer.allocate(embedding.length * 4);
		buffer.asFloatBuffer().put(embedding);
		return buffer.array();
	}
}
