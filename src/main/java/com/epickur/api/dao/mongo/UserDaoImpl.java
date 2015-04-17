package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * User DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class UserDaoImpl extends DaoCrud<User> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(UserDaoImpl.class.getSimpleName());

	/** Constructor **/
	public UserDaoImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("users"));
	}

	@Override
	public User create(final User user) throws EpickurException {
		// user.setId(new ObjectId());
		DateTime time = new DateTime();
		user.setCreatedAt(time);
		user.setUpdatedAt(time);
		user.setKey(null);
		DBObject dbo = null;
		try {
			dbo = user.getDBView();
			LOG.debug("Create user: " + user);
			getColl().insert(dbo);
			return User.getDBObject(dbo);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), dbo, e);
		}
	}

	@Override
	public User read(final String id) throws EpickurException {
		try {
			LOG.debug("Read user: " + id);
			DBObject query = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return User.getDBObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	/**
	 * Read a User with its Name
	 * 
	 * @param name
	 *            The name of the User
	 * @return The User
	 * @throws EpickurException
	 *             if an epickur exception occurred
	 */
	public User readWithName(final String name) throws EpickurException {
		try {
			LOG.debug("Read user name: " + name);
			DBObject query = BasicDBObjectBuilder.start("name", name).get();
			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return User.getDBObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), name, e);
		}
	}

	/**
	 * Read a User with its email
	 * 
	 * @param email
	 *            The email
	 * @return The User
	 * @throws EpickurException
	 *             if an epickur exception occurred
	 */
	public User readWithEmail(final String email) throws EpickurException {
		try {
			LOG.debug("Read user email: " + email);
			DBObject query = BasicDBObjectBuilder.start("email", email).get();
			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return User.getDBObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), email, e);
		}
	}

	@Override
	public User update(final User user) throws EpickurException {
		BasicDBObject bdb = (BasicDBObject) BasicDBObjectBuilder.start("_id", user.getId()).get();
		DateTime time = new DateTime();
		user.setCreatedAt(null);
		user.setUpdatedAt(time);
		user.setKey(null);
		LOG.debug("Update user: " + user);
		DBObject update = user.getUpdateBasicDBObject();
		try {
			DBObject temp = getColl().findAndModify(bdb, null, null, false, update, true, false);
			if (temp != null) {
				return User.getDBObject(temp);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), bdb, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		try {
			DBObject bdb = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			LOG.debug("Delete user: " + id);
			return this.succes(getColl().remove(bdb), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public List<User> readAll() throws EpickurException {
		List<User> users = new ArrayList<User>();
		DBCursor cursor = null;
		try {
			cursor = getColl().find();
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				User user = User.getDBObject(iterator.next());
				users.add(user);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return users;
	}
}
