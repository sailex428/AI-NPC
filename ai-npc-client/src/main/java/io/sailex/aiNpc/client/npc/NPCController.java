package io.sailex.aiNpc.client.npc;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import io.sailex.aiNpc.client.constant.Instructions;
import io.sailex.aiNpc.client.llm.ILLMClient;
import io.sailex.aiNpc.client.model.NPCEvent;
import io.sailex.aiNpc.client.model.context.WorldContext;
import io.sailex.aiNpc.client.model.interaction.Action;
import io.sailex.aiNpc.client.model.interaction.ActionType;
import io.sailex.aiNpc.client.model.interaction.Actions;
import io.sailex.aiNpc.client.util.LogUtil;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for managing NPC actions and events.
 * Handles the NPC events (actions in-game) and executes the actions generated from the llm accordingly.
 */
public class NPCController {

	private static final Logger LOGGER = LogManager.getLogger(NPCController.class);
	private final ExecutorService executorService;
	private final Queue<Action> actionQueue = new LinkedBlockingQueue<>();

	private final ClientPlayerEntity npc;
	private final ILLMClient llmService;
	private final NPCContextGenerator npcContextGenerator;
	private final IBaritone baritone;

	/**
	 * Constructor for NPCController.
	 *
	 * @param npc                 the NPC entity
	 * @param llmService          the LLM client
	 * @param npcContextGenerator the NPC context generator
	 */
	public NPCController(ClientPlayerEntity npc, ILLMClient llmService, NPCContextGenerator npcContextGenerator) {
		this.npc = npc;
		this.llmService = llmService;
		this.npcContextGenerator = npcContextGenerator;
		this.executorService = Executors.newFixedThreadPool(3);
		this.baritone = setupPathFinding();
		handleInitMessage();
		registerActionFinishedListener();
	}

	private IBaritone setupPathFinding() {
		BaritoneAPI.getSettings().allowSprint.value = true;
		BaritoneAPI.getSettings().primaryTimeoutMS.value = 2000L;
		BaritoneAPI.getSettings().allowInventory.value = true;
		return BaritoneAPI.getProvider().getPrimaryBaritone();
	}

	/**
	 * Handles the NPC events (actions in-game).
	 *
	 * @param prompt the NPC event
	 */
	public void handleEvent(NPCEvent prompt) {
		executorService.submit(() -> {
			ActionType actionType = prompt.type();

			boolean isValidRequestType = Arrays.asList(ActionType.values()).contains(actionType);
			if (!isValidRequestType) {
				LOGGER.error("Action type not recognized: {}", actionType);
				return;
			}
			String context = NPCInteraction.formatContext(npcContextGenerator.getContext());

			String userPrompt = NPCInteraction.buildUserPrompt(prompt);
			String systemPrompt = NPCInteraction.buildSystemPrompt(context);
			llmService
					.generateResponse(userPrompt, systemPrompt)
					.thenAccept(this::offerAction)
					.exceptionally(throwable -> {
						LogUtil.error(throwable.getMessage());
						return null;
					});
		});
	}

	private void offerAction(String response) {
		Actions actions = NPCInteraction.parseResponse(response);
		actions.getActions().forEach(actionQueue::offer);
	}

	private void pollAction() {
		Action nextAction = actionQueue.poll();
		if (nextAction == null) {
			return;
		}
		executeAction(nextAction);
	}

	private void executeAction(Action action) {
		ActionType actionType = action.getAction();
		switch (actionType) {
			case CHAT -> chat(action.getMessage());
			case MOVE -> move(action.getTargetPosition());
			case MINE -> mine(action.getTargetPosition());
			default -> LOGGER.error("Action type not recognized in: {}", actionType);
		}
	}

	private void cancelAction() {
		baritone.getCommandManager().execute("cancel");
	}

	private void handleInitMessage() {
		handleEvent(new NPCEvent(
				ActionType.CHAT,
				Instructions.getDefaultInstruction(npc.getName().getString())));
		baritone.getExploreProcess().explore(20, 40);
	}

	private void chat(String message) {
		npc.networkHandler.sendChatMessage(message);
	}

	private void move(WorldContext.Position targetPosition) {
		baritone.getCustomGoalProcess()
				.setGoalAndPath(new GoalBlock(targetPosition.x(), targetPosition.y(), targetPosition.z()));
	}

	private void mine(WorldContext.Position targetPosition) {
		BetterBlockPos blockPos = new BetterBlockPos(targetPosition.x(), targetPosition.y(), targetPosition.z());
		baritone.getSelectionManager().addSelection(blockPos, blockPos);
		baritone.getBuilderProcess().clearArea(blockPos, blockPos);
	}

	private void registerActionFinishedListener() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (baritone.getPathingBehavior().isPathing()
					|| baritone.getCustomGoalProcess().isActive()
					|| baritone.getMineProcess().isActive()
					|| baritone.getExploreProcess().isActive()
					|| baritone.getFollowProcess().isActive()
					|| baritone.getFarmProcess().isActive()) {
				return;
			}
			pollAction();
		});
	}
}
