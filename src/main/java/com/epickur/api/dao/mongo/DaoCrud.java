package com.epickur.api.dao.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.epickur.api.dao.IDaoCrud;
import com.epickur.api.dao.MongoDb;
import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

/**
 * @author cph
 * @version 1.0
 * @param <T>
 *            Must be an AbstractEntity
 */
public abstract class DaoCrud<T extends AbstractEntity> implements IDaoCrud<T> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(DaoCrud.class.getSimpleName());
	/** Database **/
	private MongoDatabase db;
	/** Database collection **/
	private MongoCollection<Document> coll;

	/** Init function **/
	protected final void initDB() {
		this.db = MongoDb.getInstance();
	}

	@Override
	public abstract T create(T obj) throws EpickurException;

	@Override
	public abstract T read(String id) throws EpickurException;

	@Override
	public abstract T update(T obj) throws EpickurException;

	@Override
	public abstract boolean delete(String id) throws EpickurException;

	/**
	 * Check if the query is a succes
	 * 
	 * @param wr
	 *            The result of the query
	 * @param type
	 *            The type of the query
	 * @return True if the query is a succes
	 */
	protected final boolean isDeleted(final DeleteResult deleteResult, final String type) {
		boolean res = true;
		if (deleteResult.getDeletedCount() != 1) {
			res = false;
			LOG.error("Request type: " + type + " failed");
		}
		return res;
	}

	/**
	 * Getter
	 * 
	 * @return The DB object
	 */
	public final MongoDatabase getDb() {
		return db;
	}

	/**
	 * Getter
	 * 
	 * @return The DBCollection
	 */
	public final MongoCollection<Document> getColl() {
		return coll;
	}

	/**
	 * Setter
	 * 
	 * @param coll
	 *            The DBCollection to set
	 */
	public final void setColl(final MongoCollection<Document> coll) {
		this.coll = coll;
	}
}
