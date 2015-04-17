package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Key DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class KeyDaoImpl extends DaoCrud<Key> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(KeyDaoImpl.class.getSimpleName());

	/** Constructor **/
	public KeyDaoImpl() {
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
		// key.setId(new ObjectId());
		DateTime time = new DateTime();
		key.setCreatedAt(time);
		key.setUpdatedAt(time);
		LOG.debug("Create key: " + key);
		DBObject dbo = null;
		try {
			dbo = key.getDBView();
			getColl().insert(dbo);
			return Key.getObject(dbo);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), dbo, e);
		}
	}

	@Override
	public Key read(final String key) throws EpickurException {
		try {
			DBObject query = BasicDBObjectBuilder.start("key", key).get();
			LOG.debug("Read key: " + key);

			DBObject dbo = (DBObject) getColl().findOne(query);
			if (dbo != null) {
				return Key.getObject(dbo);
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
			DBObject query = BasicDBObjectBuilder.start("userName", userName).get();
			LOG.debug("Read key name: " + userName);

			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return Key.getObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), userName, e);
		}
	}

	@Override
	public Key update(final Key key) throws EpickurException {
		DateTime time = new DateTime();
		key.setCreatedAt(null);
		key.setUpdatedAt(time);
		throw new EpickurDBException();
	}

	@Override
	public boolean delete(final String key) throws EpickurException {
		try {
			DBObject bdb = BasicDBObjectBuilder.start("key", key).get();
			LOG.debug("Delete key: " + key);
			return this.succes(getColl().remove(bdb), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), key, e);
		}
	}

	@Override
	public List<Key> readAll() throws EpickurException {
		List<Key> keys = new ArrayList<Key>();
		DBCursor cursor = null;
		try {
			cursor = getColl().find();
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Key key = Key.getObject(iterator.next());
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
