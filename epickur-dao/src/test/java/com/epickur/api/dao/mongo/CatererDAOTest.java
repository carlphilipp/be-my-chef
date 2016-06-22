package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Caterer;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
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

import static com.epickur.api.dao.CollectionsName.CATERER_COLL;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CatererDAOTest {
	
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
	private CatererDAO dao;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(db.getCollection(CATERER_COLL)).thenReturn(collection);
	}
	
	@Test
	public void testCreate() throws EpickurException {
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Document document = caterer.getDocumentDBView();

		Caterer actual = dao.create(caterer);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Document document = caterer.getDocumentDBView();

		doThrow(new MongoException("")).when(collection).insertOne(document);

		Caterer actual = dao.create(caterer);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String catererId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(catererId));
		Document found = EntityGenerator.generateRandomCatererWithId().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<Caterer> actual = dao.read(catererId);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String catererId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(catererId));

		when(collection.find(query)).thenThrow(new MongoException(""));

		dao.read(catererId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		String catererId = "myId";

		dao.read(catererId);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Document found = EntityGenerator.generateRandomCatererWithId().getDocumentDBView();

		when(collection.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Caterer> actuals = dao.readAll();

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
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Document document = caterer.getDocumentDBView();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		Caterer actual = dao.update(caterer);

		assertNotNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		Caterer actual = dao.update(caterer);

		assertNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenThrow(new MongoException(""));

		dao.update(caterer);
	}

}
