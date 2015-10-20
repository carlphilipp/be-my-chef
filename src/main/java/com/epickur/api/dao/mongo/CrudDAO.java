package com.epickur.api.dao.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.epickur.api.dao.ICrudDAO;
import com.epickur.api.dao.MongoDb;
import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;

/**
 * Abstract class that helps the manipulation of Documents.
 * 
 * @author cph
 * @version 1.0
 * @param <T>
 *            Must be an AbstractEntity
 */
public abstract class CrudDAO<T extends AbstractEntity> implements ICrudDAO<T> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(CrudDAO.class.getSimpleName());
	/** Database */
	private MongoDatabase db;
	/** Database collection */
	private MongoCollection<Document> coll;

	/** Init function */
	protected final void initDB() {
		this.db = MongoDb.getInstance();
	}

	@Override
	public abstract T create(final T obj) throws EpickurException;

	@Override
	public abstract T read(final String id) throws EpickurException;

	@Override
	public abstract T update(final T obj) throws EpickurException;

	@Override
	public boolean delete(final String id) throws EpickurException {
		LOG.debug("Delete with id: " + id);
		Document filter = convertAttributeToDocument("_id", new ObjectId(id));
		return deleteDocument(filter);
	}
	
	protected final void insertDocument(final Document document) throws EpickurDBException {
		try {
			getColl().insertOne(document);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), document, e);
		}
	}
	
	protected final Document findDocument(final Document query) throws EpickurDBException {
		try {
			return getColl().find(query).first();
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), query, e);
		}
	}
	
	protected final Document updateDocument(final Document filter, final Document update) throws EpickurDBException {
		try {
			return getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}
	
	protected final boolean deleteDocument(final Document filter) throws EpickurDBException {
		try {
			return this.isDeleted(getColl().deleteOne(filter));
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), filter, e);
		}
	}
	
	/**
	 * Check if the query is a success
	 * 
	 * @param deleteResult
	 *            The result of the query
	 * @param type
	 *            The type of the query
	 * @return True if the query is a success
	 */
	private final boolean isDeleted(final DeleteResult deleteResult) {
		return deleteResult.getDeletedCount() == 1;
	}
	
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

	/**
	 * Setter
	 * 
	 * @param coll
	 *            The DBCollection to set
	 */
	protected final void setColl(final MongoCollection<Document> coll) {
		this.coll = coll;
	}
}
