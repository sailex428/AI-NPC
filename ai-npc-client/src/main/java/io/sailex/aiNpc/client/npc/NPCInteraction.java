package io.sailex.aiNpc.client.npc;

import com.google.gson.*;
import io.sailex.aiNpc.client.model.NPCEvent;
import io.sailex.aiNpc.client.model.context.WorldContext;
import io.sailex.aiNpc.client.model.interaction.Actions;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generates prompts and parses responses for communication between the NPC and the LLM.
 */
public class NPCInteraction {

	private static final Gson GSON = new Gson();
	private static final Logger LOGGER = LogManager.getLogger(NPCInteraction.class);

	/**
	 * Builds a JSON user prompt from an NPC event.
	 *
	 * @param message the NPC event
	 * @return the user prompt
	 */
	public static String buildUserPrompt(NPCEvent message) {
		return GSON.toJson(message);
	}

	/**
	 * Builds a JSON system prompt from the context of the Minecraft world.
	 *
	 * @param context the context of the Minecraft world
	 * @return the system prompt
	 */
	public static String buildSystemPrompt(String context) {
		return String.format("""
			context from the minecraft world: %s,
			""", context);
	}

	/**
	 * Parses the response from the LLM.
	 * Casts the response to Actions.
	 *
	 * @param response the response from the LLM
	 * @return the actions generated from the LLM
	 */
	public static Actions parseResponse(String response) {
		try {
			return GSON.fromJson(response, Actions.class);
		} catch (JsonSyntaxException e) {
			LOGGER.error("Error parsing response: {}", e.getMessage());
			throw new JsonSyntaxException("Error parsing response: " + e.getMessage());
		}
	}

	public static String formatContext(WorldContext context) {
		return String.format(
				"""
			Make decisions based on:

			Available Resources:
			%s

			Current State:
			- NPC state: %s
			- Inventory: %s

			You should:
			1. Check if the action is possible (correct tools, resources in range)
			2. Move to nearest appropriate resource if needed
			3. Inform player of your actions/intentions
			""",
				formatResources(context.nearbyBlocks()),
				formatNPCState(context.npcState()),
				formatInventory(context.inventory()));
	}

	public static String formatInventory(WorldContext.InventoryState inventory) {
		return String.format(
				"""
				- main hand: %s
				- armour: %s
				- main inventory: %s
				- hotbar: %s
				""",
				inventory.mainHandItem(), inventory.armor(), inventory.mainInventory(), inventory.hotbar());
	}

	public static String formatNPCState(WorldContext.NPCState state) {
		WorldContext.Position position = state.position();
		return String.format(
				"""
			- your position: %s
			- health: %s
			- hunger: %s
			- on Ground: %s
			- touching water: %s
			""",
				String.format("x: %s y: %s, z: %s", position.x(), position.y(), position.z()),
				state.health(),
				state.food(),
				state.onGround(),
				state.inWater());
	}

	public static String formatResources(List<WorldContext.BlockData> blocks) {
		return blocks.stream()
				.map(block -> String.format("- Block %s is at %s", block.type(), block.position()))
				.collect(Collectors.joining("\n"));
	}
}
