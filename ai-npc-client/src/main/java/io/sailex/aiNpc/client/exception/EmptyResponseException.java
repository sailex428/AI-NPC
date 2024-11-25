package io.sailex.aiNpc.client.exception;

/**
 * Exception thrown when the response from the LLM service is empty
 */
public class EmptyResponseException extends RuntimeException {
	public EmptyResponseException(String message) {
		super(message);
	}
}
