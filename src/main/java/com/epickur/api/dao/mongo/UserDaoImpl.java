package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

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
		DateTime time = new DateTime();
		user.setCreatedAt(time);
		user.setUpdatedAt(time);
		user.setKey(null);
		Document doc = null;
		try {
			doc = user.getDBView();
			LOG.debug("Create user: " + user);
			getColl().insertOne(doc);
			return User.getObject(doc);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public User read(final String id) throws EpickurException {
		try {
			LOG.debug("Read user: " + id);
			Document query = new Document().append("_id", new ObjectId(id));
			Document find = getColl().find(query).first();
			if (find != null) {
				return User.getObject(find);
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
			Document query = new Document().append("name", name);
			Document find = getColl().find(query).first();
			if (find != null) {
				return User.getObject(find);
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
			Document query = new Document().append("email", email);
			Document find = getColl().find(query).first();
			if (find != null) {
				return User.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), email, e);
		}
	}

	@Override
	public User update(final User user) throws EpickurException {
		Document filter = new Document().append("_id", user.getId());
		DateTime time = new DateTime();
		user.setCreatedAt(null);
		user.setUpdatedAt(time);
		user.setKey(null);
		LOG.debug("Update user: " + user);
		Document update = user.getUpdateDocument();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return User.getObject(updated);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		try {
			Document filter = new Document().append("_id", new ObjectId(id));
			LOG.debug("Delete user: " + id);
			return this.isDeleted(getColl().deleteOne(filter), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public List<User> readAll() throws EpickurException {
		List<User> users = new ArrayList<User>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				User user = User.getObject(cursor.next());
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
