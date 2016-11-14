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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.USER_COLL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
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
		given(db.getCollection(USER_COLL)).willReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();

		// When
		User actual = dao.create(user);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		dao.create(user);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<User> actual = dao.read(userId);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.read(userId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		// Then
		thrown.expect(IllegalArgumentException.class);

		// Given
		String userId = "myId";

		// When
		dao.read(userId);
	}

	@Test
	public void testReadWithName() throws EpickurException {
		// Given
		String name = new ObjectId().toHexString();
		Document query = new Document().append("name", name);
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<User> actual = dao.readWithName(name);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadWithEmail() throws EpickurException {
		// Given
		String email = new ObjectId().toHexString();
		Document query = new Document().append("email", email);
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<User> actual = dao.readWithEmail(email);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Document found = EntityGenerator.generateRandomUser().getDocumentDBView();
		given(collection.find()).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<User> actuals = dao.readAll();

		// Then
		assertNotNull(actuals);
		assertThat(actuals, hasSize(1));
		then(collection).should().find();
		then(cursor).should().close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		given(collection.find()).willThrow(new MongoException(""));

		// When
		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(document);

		// When
		User actual = dao.update(user);

		// Then
		assertNotNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		// Given
		User user = EntityGenerator.generateRandomUser();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(null);

		// When
		User actual = dao.update(user);

		// Then
		assertNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		User user = EntityGenerator.generateRandomUser();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willThrow(new MongoException(""));

		// When
		dao.update(user);
	}

	@Test
	public void testExists() throws EpickurDBException, EpickurParsingException {
		// Given
		String name = EntityGenerator.generateRandomString();
		String email = EntityGenerator.generateRandomString();
		User user = EntityGenerator.generateRandomUser();
		Document document = user.getDocumentDBView();
		given(collection.find(any(Document.class))).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(document);

		// When
		boolean actual = dao.exists(name, email);

		// Then
		assertTrue(actual);
		then(collection).should().find(any(Document.class));
	}
}
