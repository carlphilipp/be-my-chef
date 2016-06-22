package com.epickur.api.dao.mongo;

import com.epickur.api.dao.ICrudDAO;
import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * Abstract class that helps the manipulation of Documents.
 *
 * @param <T> Must be an AbstractEntity
 * @author cph
 * @version 1.0
 */
@Slf4j
public abstract class CrudDAO<T extends AbstractEntity> implements ICrudDAO<T> {

	/**
	 * Database
	 */
	@Autowired
	private MongoDatabase db;
	/**
	 * Database collection
	 */
	private MongoCollection<Document> coll;

	@Override
	public abstract T create(final T obj) throws EpickurException;

	@Override
	public abstract Optional<T> read(final String id) throws EpickurException;

	@Override
	public abstract T update(final T obj) throws EpickurException;

	@Override
	public boolean delete(final String id) throws EpickurException {
		log.debug("Delete with id: " + id);
		final Document filter = convertAttributeToDocument("_id", new ObjectId(id));
		return deleteDocument(filter);
	}

	/**
	 * @param document The document
	 * @throws EpickurDBException If an EpickurDBException occurred.
	 */
	protected final void insertDocument(final Document document) throws EpickurDBException {
		try {
			getColl().insertOne(document);
		} catch (final MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), document, e);
		}
	}

	/**
	 * @param query The document query.
	 * @return The document.
	 * @throws EpickurDBException If an EpickurDBException occurred.
	 */
	protected final Document findDocument(final Document query) throws EpickurDBException {
		try {
			return getColl().find(query).first();
		} catch (final MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), query, e);
		}
	}

	/**
	 * @param filter The filter document.
	 * @param update The update document.
	 * @return The document.
	 * @throws EpickurDBException If an EpickurDBException occurred.
	 */
	protected final Document updateDocument(final Document filter, final Document update) throws EpickurDBException {
		try {
			return getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
		} catch (final MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	/**
	 * @param filter The document filter
	 * @return A boolean
	 * @throws EpickurDBException If an EpickurException occurred.
	 */
	protected final boolean deleteDocument(final Document filter) throws EpickurDBException {
		try {
			return isDeleted(getColl().deleteOne(filter));
		} catch (final MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), filter, e);
		}
	}

	/**
	 * Check if the query is a success
	 *
	 * @param deleteResult The result of the query
	 * @return True if the query is a success
	 */
	private boolean isDeleted(final DeleteResult deleteResult) {
		return deleteResult.getDeletedCount() == 1;
	}

	/**
	 * @param attributeName  The attribute name
	 * @param attributeValue The attribute value
	 * @return A document
	 */
	protected final Document convertAttributeToDocument(final String attributeName, final Object attributeValue) {
		return new Document().append(attributeName, attributeValue);
	}

	/**
	 * Getter
	 *
	 * @return The DB object
	 */
	protected final MongoDatabase getDb() {
		return db;
	}

	/**
	 * Getter
	 *
	 * @return The DBCollection
	 */
	protected final MongoCollection<Document> getColl() {
		return coll;
	}

	protected final void setColl(final MongoCollection<Document> coll) {
		this.coll = coll;
	}
}
