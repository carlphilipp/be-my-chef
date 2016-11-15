package com.epickur.api.dao.mongo;

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

import static com.epickur.api.dao.CollectionsName.KEY_COLL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@RunWith(MockitoJUnitRunner.class)
public class KeyDAOTest {

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
	@Mock
	private DeleteResult deleteResult;
	@InjectMocks
	private KeyDAO dao;

	@Before
	public void setUp() throws Exception {
		given(db.getCollection(KEY_COLL)).willReturn(collection);
		dao.initCollection();
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Document document = key.getDocumentDBView();

		// When
		Key actual = dao.create(key);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Key key = EntityGenerator.generateRandomAdminKey();
		Document document = key.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		Key actual = dao.create(key);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<Key> actual = dao.read(key);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.read(key);
	}

	@Test
	public void testReadWithName() throws EpickurException {
		// Given
		String userName = new ObjectId().toHexString();
		Document query = new Document().append("userName", userName);
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Key actual = dao.readWithName(userName);

		// Then
		assertNotNull(actual);
		then(collection).should().find(query);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Document found = EntityGenerator.generateRandomAdminKey().getDocumentDBView();
		given(collection.find()).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Key> actual = dao.readAll();

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find();
		then(cursor).should().close();
	}

	@Test
	public void testReadAllReadMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		given(collection.find()).willThrow(new MongoException(""));

		// When
		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Then
		thrown.expect(EpickurException.class);

		// Given
		Key key = EntityGenerator.generateRandomAdminKey();

		// When
		dao.update(key);
	}

	@Test
	public void testDelete() throws EpickurException {
		// Given
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		given(collection.deleteOne(query)).willReturn(deleteResult);
		given(deleteResult.getDeletedCount()).willReturn(1L);

		// When
		boolean actual = dao.deleteWithKey(key);

		// Then
		assertTrue(actual);
		then(collection).should().deleteOne(query);
	}

	@Test
	public void testDeleteFail() throws EpickurException {
		// Given
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		given(collection.deleteOne(query)).willReturn(deleteResult);
		given(deleteResult.getDeletedCount()).willReturn(0L);

		// When
		boolean actual = dao.deleteWithKey(key);

		// Then
		assertFalse(actual);
		then(collection).should().deleteOne(query);
	}

	@Test
	public void testDeleteMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurException.class);

		// Given
		String key = new ObjectId().toHexString();
		Document query = new Document().append("key", key);
		given(collection.deleteOne(query)).willThrow(new MongoException(""));

		// When
		dao.deleteWithKey(key);
	}
}
