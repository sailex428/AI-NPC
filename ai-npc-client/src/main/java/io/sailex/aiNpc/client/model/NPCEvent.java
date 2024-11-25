package io.sailex.aiNpc.client.model;

import io.sailex.aiNpc.client.model.interaction.ActionType;

/**
 * Represents an event that is triggered in the game world
 */
public record NPCEvent(ActionType type, String message) {}
