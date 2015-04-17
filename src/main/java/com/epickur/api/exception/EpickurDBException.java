package com.epickur.api.exception;

import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Called whenever a DB Exception occurs.
 * 
 * @author cph
 * @version 1.0
 */
public final class EpickurDBException extends EpickurException {

	/** Serializer **/
	private static final long serialVersionUID = 1L;
	/** Unexepcted exception **/
	public static final String UNEXPECTED_EXCEPTION = "Unexpected error";
	/** Dbo failed **/
	private DBObject dbo;
	/** Id **/
	private String id;
	/** Update **/
	private DBObject update;
	/** Operation type **/
	private String operation;

	/**
	 * Constructor
	 */
	public EpickurDBException() {
	}

	/**
	 * @param message
	 *            The message
	 * @param exception
	 *            The MongoException
	 */
	public EpickurDBException(final String operation, final String message, final MongoException exception) {
		super(message, exception);
		this.operation = operation;
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurDBException(final String operation, final String message, final DBObject dbo, final MongoException exception) {
		super(message, exception);
		this.dbo = dbo;
		this.operation = operation;
	}

	/**
	 * @param operation
	 *            The operation type
	 * @param message
	 *            The message
	 * @param dbo
	 *            The DBObject
	 * @param update
	 *            The update DBObject
	 * @param exception
	 *            The Exception
	 */
	public EpickurDBException(final String operation, final String message, final DBObject dbo, final DBObject update, final MongoException exception) {
		super(message, exception);
		this.dbo = dbo;
		this.update = update;
		this.operation = operation;
	}

	/**
	 * @param message
	 *            The message
	 * @param id
	 *            The id of the product
	 * @param exception
	 *            The MongoException
	 */
	public EpickurDBException(final String operation, final String message, final String id, final MongoException exception) {
		super(message, exception);
		this.id = id;
		this.operation = operation;
	}

	/**
	 * @return A DBObject
	 */
	public DBObject getDbo() {
		return this.dbo;
	}

	/**
	 * @return The id of the product
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return The update query
	 */
	public DBObject getUpdate() {
		return update;
	}

	/**
	 * @return The operation type
	 */
	public String getOperation() {
		return operation;
	}
}
