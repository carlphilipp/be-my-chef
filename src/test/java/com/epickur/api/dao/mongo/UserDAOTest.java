package com.epickur.api.dao.mongo;

import static com.epickur.api.utils.Info.USER_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class UserDAOTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private MongoDatabase dbMock;
	@Mock
	private MongoCollection<Document> collMock;
	@Mock
	private FindIterable<Document> findIteratble;
	@Mock
	private MongoCursor<Document> cursor;
	@InjectMocks
	private UserDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(USER_COLL)).thenReturn(collMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Document document = user.getDocumentDBView();

		User actual = dao.create(user);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		User user = TestUtils.generateRandomUser();
		Document document = user.getDocumentDBView();

		doThrow(new MongoException("")).when(collMock).insertOne(document);

		User actual = dao.create(user);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));
		Document found = TestUtils.generateRandomUser().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		User actual = dao.read(userId);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String userId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(userId));

		when(collMock.find(query)).thenThrow(new MongoException(""));

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
		Document found = TestUtils.generateRandomUser().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		User actual = dao.readWithName(name);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadWithEmail() throws EpickurException {
		String email = new ObjectId().toHexString();
		Document query = new Document().append("email", email);
		Document found = TestUtils.generateRandomUser().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		User actual = dao.readWithEmail(email);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Document found = TestUtils.generateRandomUser().getDocumentDBView();

		when(collMock.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<User> actuals = dao.readAll();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find();
		verify(cursor, times(1)).close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		when(collMock.find()).thenThrow(new MongoException(""));

		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		User user = TestUtils.generateRandomUser();
		Document document = user.getDocumentDBView();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		User actual = dao.update(user);

		assertNotNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		User user = TestUtils.generateRandomUser();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		User actual = dao.update(user);

		assertNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		User user = TestUtils.generateRandomUser();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class)))
				.thenThrow(new MongoException(""));

		dao.update(user);
	}

	@Test
	public void testExists() throws EpickurDBException, EpickurParsingException {
		String name = TestUtils.generateRandomString();
		String email = TestUtils.generateRandomString();

		User user = TestUtils.generateRandomUser();
		Document document = user.getDocumentDBView();

		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(document);

		boolean actual = dao.exists(name, email);
		assertTrue(actual);
		verify(collMock, times(1)).find(any(Document.class));
	}
}
