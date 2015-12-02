package com.epickur.api.dao.mongo;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.epickur.api.utils.Info.USER_COLL;

/**
 * User DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
public class UserDAO extends CrudDAO<User> {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(UserDAO.class.getSimpleName());

	/**
	 * Constructor
	 */
	public UserDAO() {
		super();
		initCollection(USER_COLL);
	}

	@Override
	public User create(final User user) throws EpickurException {
		LOG.debug("Create user: " + user);
		Document doc = user.getDocumentDBView();
		insertDocument(doc);
		return User.getDocumentAsUser(doc);
	}

	@Override
	public User read(final String id) throws EpickurException {
		LOG.debug("Read user with id: " + id);
		Document query = convertAttributeToDocument("_id", new ObjectId(id));
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read a User with its Name
	 *
	 * @param name The name of the User
	 * @return The User
	 * @throws EpickurException if an epickur exception occurred
	 */
	public User readWithName(final String name) throws EpickurException {
		LOG.debug("Read user with name: " + name);
		Document query = convertAttributeToDocument("name", name);
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read a User with its email
	 *
	 * @param email The email
	 * @return The User
	 * @throws EpickurException if an epickur exception occurred
	 */
	public User readWithEmail(final String email) throws EpickurException {
		LOG.debug("Read user with email: " + email);
		Document query = convertAttributeToDocument("email", email);
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public User update(final User user) throws EpickurException {
		LOG.debug("Update user: " + user);
		Document filter = convertAttributeToDocument("_id", user.getId());
		Document update = user.getUpdateQuery();
		Document updated = updateDocument(filter, update);
		return processAfterQuery(updated);
	}

	private User processAfterQuery(final Document user) throws EpickurParsingException {
		if (user != null) {
			return User.getDocumentAsUser(user);
		} else {
			return null;
		}
	}

	@Override
	public List<User> readAll() throws EpickurException {
		List<User> users = new ArrayList<>();
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
	 * @param name  The User name
	 * @param email The User email
	 * @return true if the user if found
	 * @throws EpickurDBException If an epickur exception occurred
	 */
	public boolean exists(final String name, final String email) throws EpickurDBException {
		Document query = createExistsQuery(name, email);
		Document found = findDocument(query);
		return found != null;
	}

	private Document createExistsQuery(final String name, final String email) {
		Document query = new Document();
		BsonArray or = new BsonArray();

		BsonDocument bsonName = new BsonDocument();
		BsonDocument bsonEmail = new BsonDocument();
		bsonName.append("name", new BsonString(name));
		bsonEmail.append("email", new BsonString(email));

		or.add(bsonName);
		or.add(bsonEmail);

		query.append("$or", or);
		return query;
	}
}
