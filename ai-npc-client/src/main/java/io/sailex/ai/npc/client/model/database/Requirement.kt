package io.sailex.ai.npc.client.model.database

data class Requirement(
    val type: String,
    val name: String,
    override val embedding: DoubleArray,
    val craftingTableNeeded: Boolean,
    val blocksNeeded: Map<String, Int>
) : Resource
