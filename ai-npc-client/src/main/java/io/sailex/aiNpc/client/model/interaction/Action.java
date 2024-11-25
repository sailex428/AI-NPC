package io.sailex.aiNpc.client.model.interaction;

import io.sailex.aiNpc.client.model.context.WorldContext;
import java.util.Map;
import lombok.Data;

/**
 * Represents an action that can be taken by the NPC
 */
@Data
public class Action {

	private final ActionType action;
	private final String message;
	private final String targetType;
	private final String targetId;
	private final WorldContext.Position targetPosition;
	private final Map<String, Object> parameters;

	public Action(
			ActionType action,
			String message,
			String targetType,
			String targetId,
			WorldContext.Position targetPosition,
			Map<String, Object> parameters) {
		this.action = action;
		this.message = message;
		this.targetType = targetType;
		this.targetId = targetId;
		this.targetPosition = targetPosition;
		this.parameters = parameters;
	}
}
