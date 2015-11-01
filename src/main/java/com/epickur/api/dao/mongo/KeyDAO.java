package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ErrorUtils;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;

/**
 * Key DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public class KeyDAO extends CrudDAO<Key> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(KeyDAO.class.getSimpleName());

	/** Constructor */
	public KeyDAO() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("keys"));
	}

	@Override
	public Key create(final Key key) throws EpickurException {
		LOG.debug("Create key: " + key);
		Document doc = key.getDocumentDBView();
		insertDocument(doc);
		return Key.getDocumentAsKey(doc);
	}

	@Override
	public Key read(final String key) throws EpickurException {
		LOG.debug("Read key: " + key);
		Document query = convertAttributeToDocument("key", key);
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read with its name
	 * 
	 * @param userName
	 *            The user name
	 * @return The Key found
	 * @throws EpickurException
	 *             if an epickur exception occurred
	 */
	public Key readWithName(final String userName) throws EpickurException {
		LOG.debug("Read key with name: " + userName);
		Document query = convertAttributeToDocument("userName", userName);
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Key update(final Key key) throws EpickurException {
		throw new EpickurException(ErrorUtils.NOT_IMPLEMENTED);
	}

	/**
	 * @param key
	 *            The document key.
	 * @return The key.
	 * @throws EpickurParsingException
	 *             If an EpickurParsingException occurred.
	 */
	private Key processAfterQuery(final Document key) throws EpickurParsingException {
		if (key != null) {
			return Key.getDocumentAsKey(key);
		} else {
			return null;
		}
	}

	/**
	 * @param key
	 *            The key.
	 * @return A boolean
	 * @throws EpickurException
	 *             If an EpickurException occurred.
	 */
	public boolean deleteWithKey(final String key) throws EpickurException {
		LOG.debug("Delete key: " + key);
		Document filter = convertAttributeToDocument("key", key);
		return deleteDocument(filter);
	}

	@Override
	public List<Key> readAll() throws EpickurException {
		List<Key> keys = new ArrayList<Key>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Key key = Key.getDocumentAsKey(cursor.next());
				keys.add(key);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return keys;
	}
}
