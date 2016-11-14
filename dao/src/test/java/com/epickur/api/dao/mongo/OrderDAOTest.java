package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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

import static com.epickur.api.dao.CollectionsName.ORDER_COLL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isA;

@RunWith(MockitoJUnitRunner.class)
public class OrderDAOTest {

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
	private OrderDAO dao;

	@Before
	public void setUp() throws Exception {
		given(db.getCollection(ORDER_COLL)).willReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();

		// When
		Order actual = dao.create(order);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		// Given
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		Order actual = dao.create(order);

		// Then
		assertNotNull(actual);
		then(db).should().getCollection(ORDER_COLL);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String orderId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(orderId));
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<Order> actual = dao.read(orderId);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		// Given
		String orderId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(orderId));

		// When
		given(collection.find(query)).willThrow(new MongoException(""));

		// Then
		dao.read(orderId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		// Given
		String orderId = "myId";

		// When
		dao.read(orderId);
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();
		given(collection.findOneAndUpdate(isA(Document.class), isA(Document.class), isA(FindOneAndUpdateOptions.class))).willReturn(document);

		// When
		Order actual = dao.update(order);

		// Then
		assertNotNull(actual);
		then(collection).should().findOneAndUpdate(isA(Document.class), isA(Document.class), isA(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws EpickurException {
		// Given
		Order order = EntityGenerator.generateRandomOrder();
		given(collection.findOneAndUpdate(isA(Document.class), isA(Document.class), isA(FindOneAndUpdateOptions.class))).willReturn(null);

		// When
		Order actual = dao.update(order);

		// Then
		assertNull(actual);
		then(collection).should().findOneAndUpdate(isA(Document.class), isA(Document.class), isA(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Order order = EntityGenerator.generateRandomOrder();
		given(collection.findOneAndUpdate(isA(Document.class), isA(Document.class), isA(FindOneAndUpdateOptions.class))).willThrow(new MongoException(""));

		// When
		dao.update(order);
	}

	@Test
	public void testReadAll() throws Exception {
		// Then
		thrown.expect(NotImplementedException.class);

		// When
		dao.readAll();
	}

	@Test
	public void testReadAllWithUserId() throws EpickurException {
		// Given
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("createdBy", userId);
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Order> actual = dao.readAllWithUserId(userId);

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find(query);
		then(cursor).should().close();
	}

	@Test
	public void testReadAllWithUserIdMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("createdBy", userId);
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.readAllWithUserId(userId);
	}

	@Test
	public void testReadAllWithCatererId() throws EpickurException {
		// Given
		String catererId = new ObjectId().toHexString();
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();
		given(collection.find(any(Document.class))).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);
		DateTime start = new DateTime().minusDays(5);
		DateTime end = new DateTime().plusDays(5);

		// When
		List<Order> actual = dao.readAllWithCatererId(catererId, start, end);

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find(isA(Document.class));
		then(cursor).should().close();
	}

	@Test
	public void testReadAllWithCatererIdMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String catererId = new ObjectId().toHexString();
		given(collection.find(isA(Document.class))).willThrow(new MongoException(""));
		DateTime start = new DateTime().minusDays(5);
		DateTime end = new DateTime().plusDays(5);

		// When
		dao.readAllWithCatererId(catererId, start, end);
	}
}
