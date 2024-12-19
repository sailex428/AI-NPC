package io.sailex.ai.npc.client.llm;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.ResponseFormat;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.embedding.EmbeddingFloat;
import io.github.sashirestela.openai.domain.embedding.EmbeddingRequest;
import io.sailex.ai.npc.client.exception.EmptyResponseException;
import io.sailex.ai.npc.client.model.interaction.Actions;
import lombok.Setter;

import java.util.List;

/**
 * OpenAI client for generating responses.
 */
public class OpenAiClient extends ALLMClient implements ILLMClient {

	@Setter
	private SimpleOpenAI openAiService;
	private final String openAiModel;

	/**
	 * Constructor for OpenAiClient.
	 *
	 * @param openAiModel the openai model (e.g. "gpt-3.5-turbo")
	 * @param apiKey      the api key
	 */
	public OpenAiClient(String openAiModel, String apiKey) {
		super();
		this.openAiModel = openAiModel;
		this.openAiService = SimpleOpenAI.builder().apiKey(apiKey).build();
	}
	
	/**
	 * Generate response from OpenAI.
	 *
	 * @param userPrompt   the user prompt
	 * @param systemPrompt the system prompt
	 * @return the llm response
	 */
	@Override
	public String generateResponse(String userPrompt, String systemPrompt) {
		try {
			ChatRequest chatRequest = ChatRequest.builder()
					.model(openAiModel)
					.message(ChatMessage.SystemMessage.of(systemPrompt))
					.message(ChatMessage.UserMessage.of(userPrompt))
					.responseFormat(ResponseFormat.jsonSchema(ResponseFormat.JsonSchema.builder()
							.name("Actions")
							.schemaClass(Actions.class)
							.build()))
					.build();

			String chatResponse = openAiService
					.chatCompletions()
					.create(chatRequest)
					.join()
					.firstContent();

			LOGGER.info("Generated response from openai: {}", chatResponse);
			if (!chatResponse.isBlank()) {
				return chatResponse;
			}
			throw new EmptyResponseException("Empty response from openai");
		} catch (Exception e) {
			LOGGER.error("Could not generate response for prompt: {}", userPrompt, e);
			return null;
		}
	}

	@Override
	public double[] generateEmbedding(List<String> prompt) {
		try {
			return convertEmbedding(openAiService
					.embeddings()
					.create(EmbeddingRequest.builder().model("text-embedding-3-small").input(prompt).build())
					.join()
					.getData()
					.stream()
					.map(EmbeddingFloat::getEmbedding)
					.toList());
		} catch (Exception e) {
			LOGGER.error("Could not generate embedding for prompt: {}", prompt.getFirst(), e);
			return new double[]{};
		}
	}
}
