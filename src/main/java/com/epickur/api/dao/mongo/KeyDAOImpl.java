package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.joda.time.DateTime;

import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;

/**
 * Key DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class KeyDAOImpl extends CrudDAO<Key> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(KeyDAOImpl.class.getSimpleName());

	/** Constructor */
	public KeyDAOImpl() {
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
		key.setId(null);
		DateTime time = new DateTime();
		key.setCreatedAt(time);
		key.setUpdatedAt(time);
		LOG.debug("Create key: " + key);
		Document doc = null;
		try {
			doc = key.getDBView();
			getColl().insertOne(doc);
			return Key.getObject(doc);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public Key read(final String key) throws EpickurException {
		try {
			Document query = new Document().append("key", key);
			LOG.debug("Read key: " + key);
			Document find = getColl().find(query).first();
			if (find != null) {
				return Key.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), key, e);
		}
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
		try {
			LOG.debug("Read key name: " + userName);
			Document query = new Document().append("userName", userName);
			Document find = getColl().find(query).first();
			if (find != null) {
				return Key.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), userName, e);
		}
	}

	@Override
	public Key update(final Key key) throws EpickurException {
		// Not implemented
		throw new EpickurException(ErrorUtils.NOT_IMPLEMENTED);
	}

	@Override
	public boolean delete(final String key) throws EpickurException {
		try {
			Document filter = new Document().append("key", key);
			LOG.debug("Delete key: " + key);
			return this.isDeleted(getColl().deleteOne(filter), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), key, e);
		}
	}

	@Override
	public List<Key> readAll() throws EpickurException {
		List<Key> keys = new ArrayList<Key>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Key key = Key.getObject(cursor.next());
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
