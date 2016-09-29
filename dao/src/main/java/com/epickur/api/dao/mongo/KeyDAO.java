package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.KEY_COLL;

/**
 * Key DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class KeyDAO extends CrudDAO<Key> {

	/**
	 * Not implemented
	 */
	private static final String NOT_IMPLEMENTED = "Not implemented";

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(KEY_COLL));
	}

	@Override
	public Key create(final Key key) throws EpickurException {
		log.debug("Create key: " + key);
		final Document doc = key.getDocumentDBView();
		insertDocument(doc);
		return Key.getDocumentAsKey(doc);
	}

	@Override
	public Optional<Key> read(final String key) throws EpickurException {
		log.debug("Read key: " + key);
		final Document query = convertAttributeToDocument("key", key);
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read with its name
	 *
	 * @param userName The user name
	 * @return The Key found
	 * @throws EpickurException if an epickur exception occurred
	 */
	public Key readWithName(final String userName) throws EpickurException {
		log.debug("Read key with name: " + userName);
		final Document query = convertAttributeToDocument("userName", userName);
		final Document find = findDocument(query);
		return processAfterQuery(find).orElse(null);
	}

	@Override
	public Key update(final Key key) throws EpickurException {
		throw new EpickurException(NOT_IMPLEMENTED);
	}

	/**
	 * @param key The document key.
	 * @return The key.
	 * @throws EpickurParsingException If an EpickurParsingException occurred.
	 */
	private Optional<Key> processAfterQuery(final Document key) throws EpickurParsingException {
		return key != null
			? Optional.of(Key.getDocumentAsKey(key))
			: Optional.empty();
	}

	/**
	 * @param key The key.
	 * @return A boolean
	 * @throws EpickurException If an EpickurException occurred.
	 */
	public boolean deleteWithKey(final String key) throws EpickurException {
		log.debug("Delete key: " + key);
		final Document filter = convertAttributeToDocument("key", key);
		return deleteDocument(filter);
	}

	@Override
	public List<Key> readAll() throws EpickurException {
		final List<Key> keys = new ArrayList<>();
		try (final MongoCursor<Document> cursor = getColl().find().iterator()) {
			while (cursor.hasNext()) {
				final Key key = Key.getDocumentAsKey(cursor.next());
				keys.add(key);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		}
		return keys;
	}
}
