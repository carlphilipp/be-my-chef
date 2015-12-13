package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.epickur.api.dao.CollectionsName.LOG_COLL;

/**
 * Log DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Repository
public final class LogDAO extends CrudDAO<Log> {

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(LOG_COLL));
	}

	@Override
	public List<Log> readAll() throws EpickurException {
		throw new NotImplementedException("Not implemented yet");
	}

	@Override
	public Log create(final Log obj) throws EpickurException {
		log.trace("Create log: {}", obj);
		final Document doc = obj.getDocumentDBView();
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
