package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Log DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Repository
public final class LogDAO extends CrudDAO<Log> {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(Log.class.getSimpleName());

	/**
	 * Constructor
	 */
	public LogDAO() {
		super();
		initCollection("logs");
	}

	@Override
	public List<Log> readAll() throws EpickurException {
		throw new NotImplementedException("Not implemented yet");
	}

	@Override
	public Log create(final Log obj) throws EpickurException {
		LOG.trace("Create log: " + obj);
		Document doc = obj.getDocumentDBView();
		insertDocument(doc);
		return null;
	}

	@Override
	public Log read(final String id) throws EpickurException {
		throw new NotImplementedException("Not implemented yet");
	}

	@Override
	public Log update(final Log obj) throws EpickurException {
		throw new NotImplementedException("Not implemented yet");
	}

	@Override
	public boolean delete(final String id) {
		throw new NotImplementedException("Not implemented yet");
	}
}
