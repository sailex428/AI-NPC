package io.sailex.aiNpcLauncher.constants;

import java.util.Set;

public class ConfigConstants {

	public static final String NPC_LLM_OLLAMA_URL = "npc.llm.ollama.url";
	public static final String NPC_LLM_OLLAMA_MODEL = "npc.llm.ollama.model";
	public static final String NPC_LLM_OPENAI_MODEL = "npc.llm.openai.model";
	public static final String NPC_LLM_OPENAI_API_KEY = "npc.llm.openai.api_key";
	public static final String NPC_LLM_TYPE = "npc.llm.type";
	public static final String NPC_SERVER_IP = "npc.server.ip";
	public static final String NPC_SERVER_PORT = "npc.server.port";
	public static final String NPC_IS_HEADLESS = "npc.is_headless";

	public static final Set<String> ALLOWED_KEYS = Set.of(
			NPC_LLM_OLLAMA_URL,
			NPC_LLM_OLLAMA_MODEL,
			NPC_LLM_OPENAI_MODEL,
			NPC_LLM_OPENAI_API_KEY,
			NPC_LLM_TYPE,
			NPC_SERVER_PORT,
			NPC_IS_HEADLESS);
}
