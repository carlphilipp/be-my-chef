package com.epickur.api.web;

import com.epickur.api.entity.message.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Class that handles all the error responses
 */
public class ResponseError {

	private static final HttpHeaders headers;

	static {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}

	/**
	 * Set error
	 *
	 * @param status
	 *            The status
	 * @return A response
	 */
	public static ResponseEntity<ErrorMessage> error(final HttpStatus status) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(status.getReasonPhrase());
		return new ResponseEntity<>(error, headers, status);
	}

	/**
	 * Set error
	 *
	 * @param status
	 *            The status
	 * @param message
	 *            The message
	 * @return A response
	 */
	public static ResponseEntity<ErrorMessage> error(final HttpStatus status, final String message) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(message);
		return new ResponseEntity<>(error, headers, status);
	}

	public static ResponseEntity<ErrorMessage> error(final HttpStatus status, final String message, final String description) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(message);
		error.addDescription(description);
		return new ResponseEntity<>(error, headers, status);
	}

	public static ResponseEntity<ErrorMessage> error(final HttpStatus status, final String message, final List<String> descriptions) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(message);
		error.setDescriptions(descriptions);
		return new ResponseEntity<>(error, headers, status);
	}

	/**
	 * Not found
	 *
	 * @param message
	 *            The message
	 * @param id
	 *            The Id
	 * @return The Response
	 */
	public static ResponseEntity<ErrorMessage> notFound(final String message, final String id) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(HttpStatus.NOT_FOUND.value());
		error.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
		error.addDescription(message + ": " + id);
		return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
	}
}
