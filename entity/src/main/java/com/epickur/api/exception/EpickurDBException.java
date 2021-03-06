package com.epickur.api.exception;

import org.bson.Document;

import com.mongodb.MongoException;

/**
 * Called whenever a DB Exception occurs.
 * 
 * @author cph
 * @version 1.0
 */
public class EpickurDBException extends EpickurException {

	/** Serializer */
	private static final long serialVersionUID = 1L;
	/** Dbo failed */
	private Document doc;
	/** Id */
	private String id;
	/** Update */
	private Document update;
	/** Operation type */
	private String operation;

	/**
	 * Constructor
	 */
	public EpickurDBException() {
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            The message
	 */
	public EpickurDBException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param operation
	 *            The operation
	 * @param message
	 *            The message
	 * @param exception
	 *            If a MongoException occured
	 */
	public EpickurDBException(final String operation, final String message, final MongoException exception) {
		super(message, exception);
		this.operation = operation;
	}

	/**
	 * @param operation
	 *            The operation
	 * @param message
	 *            The message
	 * @param doc
	 *            The document
	 * @param exception
	 *            The MongoException
	 */
	public EpickurDBException(final String operation, final String message, final Document doc, final MongoException exception) {
		super(message, exception);
		this.doc = doc;
		this.operation = operation;
	}

	/**
	 * @param operation
	 *            The operation type
	 * @param message
	 *            The message
	 * @param doc
	 *            The Document
	 * @param update
	 *            The update Document
	 * @param exception
	 *            The Exception
	 */
	public EpickurDBException(final String operation, final String message, final Document doc, final Document update, final MongoException exception) {
		super(message, exception);
		this.doc = doc;
		this.update = update;
		this.operation = operation;
	}

	/**
	 * @param operation
	 *            The operation
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
	 * @return A Document
	 */
	public final Document getDocument() {
		return this.doc;
	}

	/**
	 * @return The id of the product
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return The update query
	 */
	public final Document getUpdate() {
		return update;
	}

	/**
	 * @return The operation type
	 */
	public final String getOperation() {
		return operation;
	}
}
