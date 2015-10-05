package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
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
public final class UserDAOImpl extends CrudDAO<User> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(UserDAOImpl.class.getSimpleName());

	/** Constructor */
	public UserDAOImpl() {
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
		user.prepareUserToInsertIntoDB();
		LOG.debug("Create user: " + user);
		Document doc = user.getDocumentDBView();
		insertUser(doc);
		return User.getDocumentAsUser(doc);
	}

	private void insertUser(final Document user) throws EpickurDBException {
		try {
			getColl().insertOne(user);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), user, e);
		}
	}

	@Override
	public User read(final String id) throws EpickurException {
		LOG.debug("Read user with id: " + id);
		Document query = convertAttibuteToDocument("_id", new ObjectId(id));
		Document find = findUser(query);
		if (find != null) {
			return User.getDocumentAsUser(find);
		} else {
			return null;
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
		LOG.debug("Read user with name: " + name);
		Document query = convertAttibuteToDocument("name", name);
		Document find = findUser(query);
		return processAfterQuery(find);
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
		LOG.debug("Read user with email: " + email);
		Document query = convertAttibuteToDocument("email", email);
		Document find = findUser(query); 
		return processAfterQuery(find);
	}
	
	private Document findUser(final Document query) throws EpickurDBException {
		try {
			return getColl().find(query).first();
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), query, e);
		}
	}

	@Override
	public User update(final User user) throws EpickurException {
		user.prepareUserToBeUpdatedIntoDB();
		LOG.debug("Update user: " + user);
		Document filter = convertAttibuteToDocument("_id", user.getId());
		Document update = user.getUserUpdateQuery();
		Document updated = updateUser(filter, update);
		return processAfterQuery(updated);
	}

	private User processAfterQuery(final Document user) throws EpickurParsingException {
		if (user != null) {
			return User.getDocumentAsUser(user);
		} else {
			return null;
		}
	}

	private Document updateUser(final Document filter, final Document update) throws EpickurDBException {
		try {
			return getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		LOG.debug("Delete user with id: " + id);
		Document filter = convertAttibuteToDocument("_id", new ObjectId(id));
		return deleteUser(filter);
	}

	private boolean deleteUser(final Document filter) throws EpickurDBException {
		try {
			return this.isDeleted(getColl().deleteOne(filter), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), filter, e);
		}
	}

	private Document convertAttibuteToDocument(final String attributeName, final Object attributeValue) {
		Document document = new Document().append(attributeName, attributeValue);
		return document;
	}

	@Override
	public List<User> readAll() throws EpickurException {
		List<User> users = new ArrayList<User>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				User user = User.getDocumentAsUser(cursor.next());
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

	/**
	 * Check if a user exists with it's name or email
	 * 
	 * @param name
	 *            The User name
	 * @param email
	 *            The User email
	 * @return true if the user if found
	 */
	public boolean exists(final String name, final String email) {
		boolean res = false;
		Document find = new Document();
		BsonArray or = new BsonArray();
		BsonDocument bsonName = new BsonDocument();
		bsonName.append("name", new BsonString(name));
		BsonDocument bsonEmail = new BsonDocument();
		bsonEmail.append("email", new BsonString(email));
		or.add(bsonName);
		or.add(bsonEmail);
		find.append("$or", or);
		Document found = getColl().find(find).first();
		if (found != null) {
			res = true;
		}
		return res;
	}
}
