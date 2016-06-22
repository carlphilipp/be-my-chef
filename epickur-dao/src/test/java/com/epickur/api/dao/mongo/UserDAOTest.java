package com.epickur.api.dao.mongo;

import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.USER_COLL;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserDAOTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private MongoDatabase db;
	@Mock
	private MongoCollection<Document> collection;
	@Mock
	private FindIterable<Document> findIteratble;
	@Mock
	private MongoCursor<Document> cursor;
	@InjectMocks
	private UserDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(db.getCollection(USER_COLL)).thenReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();

		User actual = dao.create(user);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();

		doThrow(new MongoException("")).when(collection).insertOne(document);

		User actual = dao.create(user);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<User> actual = dao.read(userId);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));

		when(collection.find(query)).thenThrow(new MongoException(""));

		dao.read(userId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		String userId = "myId";

		dao.read(userId);
	}

	@Test
	public void testReadWithName() throws EpickurException {
		String name = new ObjectId().toHexString();
		Document query = new Document().append("name", name);
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<User> actual = dao.readWithName(name);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadWithEmail() throws EpickurException {
		String email = new ObjectId().toHexString();
		Document query = new Document().append("email", email);
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<User> actual = dao.readWithEmail(email);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();

		when(collection.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<User> actuals = dao.readAll();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collection).find();
		verify(cursor).close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		when(collection.find()).thenThrow(new MongoException(""));

		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		User actual = dao.update(user);

		assertNotNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		User user = EntityGenerator.generateRandomUser();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		User actual = dao.update(user);

		assertNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		User user = EntityGenerator.generateRandomUser();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class)))
				.thenThrow(new MongoException(""));

		dao.update(user);
	}

	@Test
	public void testExists() throws EpickurDBException, EpickurParsingException {
		String name = EntityGenerator.generateRandomString();
		String email = EntityGenerator.generateRandomString();

		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();

		when(collection.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(document);

		boolean actual = dao.exists(name, email);
		assertTrue(actual);
		verify(collection).find(any(Document.class));
	}
}
