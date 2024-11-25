package io.sailex.aiNpc.client.model.interaction;

/**
 * Represents the type of action that can be taken by the NPC
 */
public enum ActionType {
	CHAT, // Talking to other entities
	MOVE, // Moving to a location
	MINE, // Gathering resources
	CRAFT, // Creating items
	USE, // Using items
	STORE, // Putting items away
	RETRIEVE, // Getting items e.g. kill entities
	EQUIP, // Equipping items
	UNEQUIP, // Removing equipped items
	ATTACK, // Combat actions
	TRADE, // Trading with Villagers
	BUILD, // Construction actions
	REPAIR, // Fixing items
	INTERACT // General interactions
}
