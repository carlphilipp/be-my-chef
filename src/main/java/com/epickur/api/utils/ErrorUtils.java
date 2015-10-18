package com.epickur.api.utils;

import javax.ws.rs.core.Response;

import com.epickur.api.entity.message.ErrorMessage;

/**
 * Error Service
 * 
 * @author cph
 * @version 1.0
 */
public final class ErrorUtils {

	/** Dish not found */
	public static final String DISH_NOT_FOUND = "Dish not found";
	/** User not found */
	public static final String USER_NOT_FOUND = "User not found";
	/** Order not found */
	public static final String ORDER_NOT_FOUND = "Order not found";
	/** Caterer not found */
	public static final String CATERER_NOT_FOUND = "Caterer not found";
	/** Voucher not found */
	public static final String VOUCHER_NOT_FOUND = "Voucher not found";
	/** Invalid key */
	public static final String INVALID_KEY = "Invalid API key";
	/** Missing key */
	public static final String MISSING_KEY = "Missing API key";
	/** User not allowed to login */
	public static final String USER_NOT_ALLOWED_TO_LOGIN = "User not allowed to login";
	/** Internal server error */
	public static final String INTERNAL_SERVER_ERROR = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
	/** Internal server error */
	public static final String FORBIDDEN = Response.Status.FORBIDDEN.getReasonPhrase();
	/** Internal server error */
	public static final String IDS_DO_NOT_MATCH = "IDs provided do not match";
	/** Basic error response */
	public static final String LOGIN_ERROR = "An error occurred while logging in";
	/** Not implemented */
	public static final String NOT_IMPLEMENTED = "Not implemented";

	/**
	 * Private Constructor
	 */
	private ErrorUtils() {
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
	public static Response error(final Response.Status status, final String message) {
		ErrorMessage error = new ErrorMessage();
		error.setError(status.getStatusCode());
		error.setMessage(message);
		return Response.status(status.getStatusCode()).entity(error).build();
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
	public static Response notFound(final String message, final String id) {		
		ErrorMessage error = new ErrorMessage();
		error.setError(Response.Status.NOT_FOUND.getStatusCode());
		error.setMessage(Response.Status.NOT_FOUND.getReasonPhrase());
		error.setDescription(message + ": " + id);
		return Response.status(Response.Status.NOT_FOUND).entity(error).build();
	}

	/**
	 * @return The response
	 */
	public static Response noResult() {
		ErrorMessage error = new ErrorMessage();
		error.setError(Response.Status.BAD_REQUEST.getStatusCode());
		error.setMessage(Response.Status.BAD_REQUEST.getReasonPhrase());
		return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
	}
}
