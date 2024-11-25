package io.sailex.aiNpc.client.listener;

import io.sailex.aiNpc.client.database.SqliteClient;
import io.sailex.aiNpc.client.model.NPC;
import io.sailex.aiNpc.client.model.NPCEvent;
import io.sailex.aiNpc.client.model.interaction.ActionType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manager for handling event listeners.
 * Registers listeners for block interactions, chat messages, and stopping the client.
 */
public class EventListenerManager {

	private final NPC npc;
	private static final Logger LOGGER = LogManager.getLogger(EventListenerManager.class);

	/**
	 * Constructor for EventListenerManager.
	 *
	 * @param npc the NPC model
	 */
	public EventListenerManager(NPC npc) {
		this.npc = npc;
	}

	/**
	 * Register the event listeners.
	 */
	public void register(SqliteClient sqliteClient) {
		registerBlockInteractionListener();
		registerChatMessageListener();
		registerStoppingListener(sqliteClient);
		//		registerEntityLoadListener();
	}

	private void registerChatMessageListener() {
		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (message.getString().contains(npc.getNpcEntity().getName().getString())) {
				return;
			}
			String chatMessage = String.format(
					"%s : %s has written the message: %s",
					receptionTimestamp.toString(), sender != null ? sender.getName() : "Admin", message.getString());

			handleMessage(ActionType.CHAT, chatMessage);
		});
	}

	private void registerBlockInteractionListener() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			LOGGER.info("Player {} broke a block at {}", player.getName().getString(), pos.toString());

			String blockBreakMessage = String.format(
					"Player %s broke %s block at %s",
					player.getName().getString(), state.getBlock().getName().getString(), pos.toShortString());
			handleMessage(ActionType.MINE, blockBreakMessage);
		});

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			LOGGER.info(
					"Player {} interacted with block at {}",
					player.getName().getString(),
					hitResult.getBlockPos().toString());
			String blockInteractionMessage = String.format(
					"Player %s interacted with block at %s",
					player.getName().getString(), hitResult.getBlockPos().toShortString());
			handleMessage(ActionType.INTERACT, blockInteractionMessage);
			return ActionResult.PASS;
		});
	}

	private void registerEntityLoadListener() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (npc.getId().equals(entity.getUuid())) {
				return;
			}
			LOGGER.info(
					"Entity {} loaded in world {}",
					entity.getName().getString(),
					world.getRegistryKey().getValue());
			String entityLoadMessage = String.format(
					"Entity %s loaded in world %s",
					entity.getName().getString(), world.getRegistryKey().getValue());
			handleMessage(ActionType.INTERACT, entityLoadMessage);
		});
	}

	private void handleMessage(ActionType type, String message) {
		npc.getNpcController().handleEvent(new NPCEvent(type, message));
	}

	private void registerStoppingListener(SqliteClient sqliteClient) {
		ServerLifecycleEvents.SERVER_STOPPING.register(
				server -> MinecraftClient.getInstance().close());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> stopServices(sqliteClient));
	}

	private void stopServices(SqliteClient sqliteClient) {
		npc.getLlmService().stopService();
		npc.getNpcContextGenerator().stopService();
		sqliteClient.closeConnection();
	}
}
