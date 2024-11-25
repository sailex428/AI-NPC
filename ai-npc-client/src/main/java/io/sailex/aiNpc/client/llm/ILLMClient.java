package io.sailex.aiNpc.client.llm;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for the LLM client
 */
public interface ILLMClient {

	/**
	 * Generate a response to a user and system prompt
	 *
	 * @param userPrompt the prompt from the user
	 * @param systemPrompt the prompt from the system/game
	 * @return a CompletableFuture with the response
	 */
	CompletableFuture<String> generateResponse(String userPrompt, String systemPrompt);

	/**
	 * Stop the execution of the service
	 */
	void stopService();
}
