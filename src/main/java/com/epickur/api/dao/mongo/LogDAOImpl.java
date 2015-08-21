package com.epickur.api.dao.mongo;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class LogDAOImpl extends DAOCrud<Log> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Log.class.getSimpleName());

	/** Constructor */
	public LogDAOImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("logs"));
	}

	@Override
	public List<Log> readAll() throws EpickurException {
		throw new NotImplementedException();
	}

	@Override
	public Log create(final Log obj) throws EpickurException {
		Document doc = null;
		try {
			doc = obj.getDBView();
			LOG.trace("Create log: " + obj);
			getColl().insertOne(doc);
			return null;
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public Log read(final String id) throws EpickurException {
		throw new NotImplementedException();
	}

	@Override
	public Log update(final Log obj) throws EpickurException {
		throw new NotImplementedException();
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		throw new NotImplementedException();
	}
}
