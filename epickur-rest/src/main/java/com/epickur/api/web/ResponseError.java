package com.epickur.api.web;

import com.epickur.api.entity.message.ErrorMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Class that handles all the error responses
 */
public class ResponseError {

	private static HttpHeaders headers;

	static {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
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
	public static ResponseEntity error(final HttpStatus status, final String message) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(status.value());
		error.setMessage(message);
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
	public static ResponseEntity notFound(final String message, final String id) {
		final ErrorMessage error = new ErrorMessage();
		error.setError(HttpStatus.NOT_FOUND.value());
		error.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
		error.addDescription(message + ": " + id);
		return new ResponseEntity<>(error, headers, HttpStatus.NOT_FOUND);
	}

	/**
	 * @return The response
	 */
	public static ResponseEntity noResult() {
		final ErrorMessage error = new ErrorMessage();
		error.setError(HttpStatus.BAD_REQUEST.value());
		error.setMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
		return new ResponseEntity<>(error, headers, HttpStatus.BAD_REQUEST);
	}
}
