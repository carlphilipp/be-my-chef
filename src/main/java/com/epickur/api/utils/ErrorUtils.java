package com.epickur.api.utils;

import javax.ws.rs.core.Response;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Error Service
 * 
 * @author cph
 * @version 1.0
 */
public final class ErrorUtils {

	/** Dish not found **/
	public static final String DISH_NOT_FOUND = "Dish not found";
	/** User not found **/
	public static final String USER_NOT_FOUND = "User not found";
	/** Order not found **/
	public static final String ORDER_NOT_FOUND = "Order not found";
	/** Caterer not found **/
	public static final String CATERER_NOT_FOUND = "Caterer not found";
	/** Invalid key **/
	public static final String INVALID_KEY = "Invalid API key";
	/** Missing key **/
	public static final String MISSING_KEY = "Missing API key";
	/** User not allowed to login **/
	public static final String USER_NOT_ALLOWED_TO_LOGIN = "User not allowed to login";
	/** Internal server error **/
	public static final String INTERNAL_SERVER_ERROR = Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
	/** Internal server error **/
	public static final String FORBIDDEN = Response.Status.FORBIDDEN.getReasonPhrase();
	/** Internal server error **/
	public static final String IDS_DO_NOT_MATCH = "IDs provided do not match";
	/** Basic error response **/
	public static final String LOGIN_ERROR = "An error occurred while logging in";
	/** Not implemented **/
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
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", status.getStatusCode());
		bdb.put("message", message);
		return Response.status(status.getStatusCode()).entity(bdb).build();
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
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.NOT_FOUND.getStatusCode());
		bdb.put("message", Response.Status.NOT_FOUND.getReasonPhrase());
		bdb.put("description", message + ": " + id);
		return Response.status(Response.Status.NOT_FOUND).entity(bdb).build();
	}

	/**
	 * @return The response
	 */
	public static Response noResult() {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("error", Response.Status.NO_CONTENT);
		bdb.put("message", Response.Status.NO_CONTENT.getReasonPhrase());
		return Response.status(Response.Status.NO_CONTENT).entity(bdb).build();
	}
}
