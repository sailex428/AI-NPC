package io.sailex.ai.npc.client.database.repositories

import io.sailex.ai.npc.client.model.database.Resource

interface IRepository {

    fun init()
    fun createTable()
    fun getMostRelevantResources(prompt: String): List<Resource>
    fun selectAll(): List<Resource>

}