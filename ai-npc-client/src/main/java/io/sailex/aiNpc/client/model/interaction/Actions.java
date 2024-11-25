package io.sailex.aiNpc.client.model.interaction;

import java.util.List;
import lombok.Getter;

/**
 * Represents a list of actions that can be taken by the NPC
 * @see Action
 * @see ActionType
 */
@Getter
public class Actions {

	private List<Action> actions;
}
