package io.sailex.ai.npc.client.database.indexer

import io.sailex.ai.npc.client.AiNPCClient.client
import io.sailex.ai.npc.client.database.repository.RequirementsRepository
import io.sailex.ai.npc.client.llm.ILLMClient
import io.sailex.ai.npc.client.util.LogUtil
import net.minecraft.inventory.CraftingInventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeEntry
import kotlin.collections.forEach

class RequirementsIndexer(
    val requirementsRepository: RequirementsRepository,
    val llmClient: ILLMClient
) {

    /**
     * Indexes the requirements in db for all recipes in the game
     */
    //? if <1.21.2 {
    fun index() {
        val world = client.world
        if (world == null) {
            LogUtil.error("Could not get 'recipes', cause the client world is null")
            return
        }
        val recipes: Collection<RecipeEntry<*>> = world.recipeManager.values();
        recipes.forEach { recipe ->
            {
                val recipeValue = recipe.value
                if (recipeValue is CraftingInventory) {
                    val recipeName = recipe.id.namespace
                    requirementsRepository.insert(
                        recipeName,
                        llmClient.generateEmbedding(listOf(recipeName)),
                        recipeValue.fits(2, 2),
                        getBlocksNeeded(recipeValue)
                    )
                }
            }
        }
    }

    private fun getBlocksNeeded(recipe: Recipe<*>): String {
        return recipe.ingredients.joinToString(",") { "${it.matchingStacks[0].name}=${it.matchingStacks.size}" }
    }
    //?}

}