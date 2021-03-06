package com.epickur.api.dao.mongo;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.USER_COLL;

/**
 * User DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class UserDAO extends CrudDAO<User> {

	@Autowired
	public UserDAO(final MongoDatabase db) {
		super(db);
	}

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(USER_COLL));
	}

	@Override
	public User create(final User user) throws EpickurException {
		log.debug("Create user: " + user);
		final Document doc = user.getDocumentDBView();
		insertDocument(doc);
		return User.getDocumentAsUser(doc);
	}

	@Override
	public Optional<User> read(final String id) throws EpickurException {
		log.debug("Read user with id: " + id);
		final Document query = convertAttributeToDocument("_id", new ObjectId(id));
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read a User with its Name
	 *
	 * @param name The name of the User
	 * @return The User
	 * @throws EpickurException if an epickur exception occurred
	 */
	public Optional<User> readWithName(final String name) throws EpickurException {
		log.debug("Read user with name: " + name);
		final Document query = convertAttributeToDocument("name", name);
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	/**
	 * Read a User with its email
	 *
	 * @param email The email
	 * @return The User
	 * @throws EpickurException if an epickur exception occurred
	 */
	public Optional<User> readWithEmail(final String email) throws EpickurException {
		log.debug("Read user with email: " + email);
		final Document query = convertAttributeToDocument("email", email);
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public User update(final User user) throws EpickurException {
		log.debug("Update user: " + user);
		final Document filter = convertAttributeToDocument("_id", user.getId());
		final Document update = user.getUpdateQuery();
		final Document updated = updateDocument(filter, update);
		return processAfterQuery(updated).orElse(null);
	}

	private Optional<User> processAfterQuery(final Document user) throws EpickurParsingException {
		return user != null
			? Optional.of(User.getDocumentAsUser(user))
			: Optional.empty();
	}

	@Override
	public List<User> readAll() throws EpickurException {
		final List<User> users = new ArrayList<>();
		try (final MongoCursor<Document> cursor = getColl().find().iterator()) {
			while (cursor.hasNext()) {
				final User user = User.getDocumentAsUser(cursor.next());
				users.add(user);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
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
		final Document query = createExistsQuery(name, email);
		final Document found = findDocument(query);
		return found != null;
	}

	private Document createExistsQuery(final String name, final String email) {
		final Document query = new Document();
		final BsonArray or = new BsonArray();

		final BsonDocument bsonName = new BsonDocument();
		final BsonDocument bsonEmail = new BsonDocument();
		bsonName.append("name", new BsonString(name));
		bsonEmail.append("email", new BsonString(email));

		or.add(bsonName);
		or.add(bsonEmail);

		query.append("$or", or);
		return query;
	}
}
