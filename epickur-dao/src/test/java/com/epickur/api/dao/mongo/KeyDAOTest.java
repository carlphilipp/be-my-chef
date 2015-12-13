package com.epickur.api.dao.mongo;

import static com.epickur.api.dao.CollectionsName.KEY_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class KeyDAOTest {
	
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
	@Mock
	private DeleteResult deleteResult;
	@InjectMocks
	private KeyDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(KEY_COLL)).thenReturn(collMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		Key key = EntityGenerator.generateRandomAdminKey();
		Document document = key.getDocumentDBView();

		Key actual = dao.create(key);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Key key = EntityGenerator.generateRandomAdminKey();
		Document document = key.getDocumentDBView();

		doThrow(new MongoException("")).when(collMock).insertOne(document);

		Key actual = dao.create(key);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Key actual = dao.read(key);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);

		when(collMock.find(query)).thenThrow(new MongoException(""));

		dao.read(key);
	}
	
	@Test
	public void testReadWithName() throws EpickurException {
		String userName = new ObjectId().toHexString();
		Document query = new Document().append("userName", userName);
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Key actual = dao.readWithName(userName);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();

		when(collMock.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Key> actuals = dao.readAll();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find();
		verify(cursor, times(1)).close();
	}
	
	@Test
	public void testReadAllReadMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);
		
		when(collMock.find()).thenThrow(new MongoException(""));

		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		thrown.expect(EpickurException.class);
	
		Key key = EntityGenerator.generateRandomAdminKey();
		
		dao.update(key);
	}
	
	@Test
	public void testDelete() throws EpickurException {
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);

		when(collMock.deleteOne(query)).thenReturn(deleteResult);
		when(deleteResult.getDeletedCount()).thenReturn(1L);

		boolean actual = dao.deleteWithKey(key);

		assertTrue(actual);
		verify(collMock, times(1)).deleteOne(query);
	}
	
	@Test
	public void testDeleteFail() throws EpickurException {
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);

		when(collMock.deleteOne(query)).thenReturn(deleteResult);
		when(deleteResult.getDeletedCount()).thenReturn(0L);

		boolean actual = dao.deleteWithKey(key);

		assertFalse(actual);
		verify(collMock, times(1)).deleteOne(query);
	}
	
	@Test
	public void testDeleteMongoException() throws EpickurException {
		thrown.expect(EpickurException.class);
		
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);

		when(collMock.deleteOne(query)).thenThrow(new MongoException(""));
		dao.deleteWithKey(key);
	}
}
