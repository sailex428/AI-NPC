package io.sailex.aiNpc.client.model.db;

public record Skill(int id, String name, String description, String example, float[] embedding) {}
