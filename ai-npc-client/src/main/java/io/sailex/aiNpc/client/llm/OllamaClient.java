package io.sailex.aiNpc.client.llm;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.sailex.aiNpc.client.constant.Instructions;
import io.sailex.aiNpc.client.util.LogUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ollama client for generating responses.
 */
public class OllamaClient implements ILLMClient {

	private static final Logger LOGGER = LogManager.getLogger(OllamaClient.class);

	private final ExecutorService service;
	private final OllamaAPI ollamaAPI;
	private final OllamaChatRequestBuilder builder;

	/**
	 * Constructor for OllamaClient.
	 *
	 * @param ollamaModel the ollama model (e.g. "gemma2")
	 * @param ollamaUrl   the ollama url
	 */
	public OllamaClient(String ollamaModel, String ollamaUrl) {
		LogUtil.info("Connecting to ollama at " + ollamaUrl);
		this.ollamaAPI = new OllamaAPI(ollamaUrl);
		checkOllamaIsReachable();
		this.builder = OllamaChatRequestBuilder.getInstance(ollamaModel);
		this.service = Executors.newFixedThreadPool(2);
	}

	private void checkOllamaIsReachable() {
		boolean isOllamaServerReachable = ollamaAPI.ping();
		if (!isOllamaServerReachable) {
			LogUtil.error("Ollama server is not reachable");
			throw new CompletionException(new ConnectException("Ollama server is not reachable"));
		}
	}

	/**
	 * Generate response from Ollama.
	 *
	 * @param userPrompt   the user prompt
	 * @param systemPrompt the system prompt
	 * @return the llm response
	 */
	@Override
	public CompletableFuture<String> generateResponse(String userPrompt, String systemPrompt) {
		return CompletableFuture.supplyAsync(
				() -> {
					try {
						OllamaChatRequest requestModel = builder.withMessage(
										OllamaChatMessageRole.SYSTEM,
										systemPrompt + Instructions.STRUCTURE_INSTRUCTIONS)
								.withMessage(OllamaChatMessageRole.USER, userPrompt)
								.build();
						requestModel.setReturnFormatJson(true);
						ollamaAPI.setRequestTimeoutSeconds(30);

						return ollamaAPI.chat(requestModel).getResponse();
					} catch (IOException | InterruptedException | OllamaBaseException e) {
						LOGGER.error("Error generating response from ollama", e);
						throw new CompletionException(
								new ConnectException("Failed to connect to ollama: " + e.getMessage()));
					}
				},
				service);
	}

	@Override
	public void stopService() {
		service.shutdown();
	}
}
