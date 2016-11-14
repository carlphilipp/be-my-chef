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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.CATERER_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class CatererDAOTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private MongoDatabase db;
	@Mock
	private MongoCollection<Document> collection;
	@Mock
	private FindIterable<Document> findIterable;
	@Mock
	private MongoCursor<Document> cursor;
	@InjectMocks
	private CatererDAO dao;

	@Before
	public void setUp() throws Exception {
		given(db.getCollection(CATERER_COLL)).willReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Document document = caterer.getDocumentDBView();

		// When
		Caterer actual = dao.create(caterer);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithoutId();
		Document document = caterer.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		Caterer actual = dao.create(caterer);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String catererId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(catererId));
		Document found = EntityGenerator.generateRandomCatererWithId().getDocumentDBView();
		given(collection.find(query)).willReturn(findIterable);
		given(findIterable.first()).willReturn(found);

		// When
		Optional<Caterer> actual = dao.read(catererId);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String catererId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(catererId));
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.read(catererId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		// Then
		thrown.expect(IllegalArgumentException.class);

		// Given
		String catererId = "myId";

		// When
		dao.read(catererId);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Document found = EntityGenerator.generateRandomCatererWithId().getDocumentDBView();
		given(collection.find()).willReturn(findIterable);
		given(findIterable.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Caterer> actual = dao.readAll();

		// Then
		assertNotNull(actual);
		assertEquals(1, actual.size());
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
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		Document document = caterer.getDocumentDBView();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(document);

		// When
		Caterer actual = dao.update(caterer);

		// Then
		assertNotNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(null);

		// When
		Caterer actual = dao.update(caterer);

		// Then
		assertNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willThrow(new MongoException(""));

		// When
		dao.update(caterer);
	}
}
