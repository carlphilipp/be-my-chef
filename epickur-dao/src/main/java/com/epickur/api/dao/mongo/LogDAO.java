package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.LOG_COLL;

/**
 * Log DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class LogDAO extends CrudDAO<Log> {

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
	public Optional<Log> read(final String id) throws EpickurException {
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
