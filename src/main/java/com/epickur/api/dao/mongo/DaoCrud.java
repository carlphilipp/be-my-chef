package com.epickur.api.dao.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.dao.IDaoCrud;
import com.epickur.api.dao.MongoDb;
import com.epickur.api.entity.AbstractEntity;
import com.epickur.api.exception.EpickurException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

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
	private DB db;
	/** Database collection **/
	private DBCollection coll;

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
	protected final boolean succes(final WriteResult wr, final String type) {
		boolean res = true;
		if (wr.getN() == 0) {
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
	public final DB getDb() {
		return db;
	}

	/**
	 * Getter
	 * 
	 * @return The DBCollection
	 */
	public final DBCollection getColl() {
		return coll;
	}

	/**
	 * Setter
	 * 
	 * @param coll
	 *            The DBCollection to set
	 */
	public final void setColl(final DBCollection coll) {
		this.coll = coll;
	}
}
