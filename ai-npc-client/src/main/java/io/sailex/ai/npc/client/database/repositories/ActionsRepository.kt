package io.sailex.ai.npc.client.database.repositories

import io.sailex.ai.npc.client.database.SqliteClient
import io.sailex.ai.npc.client.model.database.ActionResource
import io.sailex.ai.npc.client.model.database.Resource
import io.sailex.ai.npc.client.util.VectorUtil

class ActionsRepository(
    val sqliteClient: SqliteClient,
) : ARepository() {
    override fun createTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS actions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    description_embedding BLOB,
                    example TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        """
        sqliteClient.create(sql)
    }

    fun insert(
        name: String,
        description: String,
        descriptionEmbedding: DoubleArray,
        example: String,
    ) {
        val statement =
            sqliteClient.buildPreparedStatement(
                "INSERT INTO actions (name, description, description_embedding, example) VALUES (?, ?, ?, )",
            )
        statement.setString(1, name)
        statement.setString(2, description)
        statement.setBytes(3, VectorUtil.convertToBytes(descriptionEmbedding))
        statement.setString(4, example)
        sqliteClient.insert(statement)
    }

    override fun selectAll(): List<Resource> {
        val sql = "SELECT * FROM actions"
        val result = sqliteClient.select(sql)
        val actionResources = arrayListOf<ActionResource>()

        while (result.next()) {
            val actionResource =
                ActionResource(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("description"),
                    VectorUtil.convertToDoubles(result.getBytes("description_embedding")),
                    result.getString("example"),
                    result.getString("created_at"),
                )
            actionResources.add(actionResource)
        }
        return actionResources
    }
}
