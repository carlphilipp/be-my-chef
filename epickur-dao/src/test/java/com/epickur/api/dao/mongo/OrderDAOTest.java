package com.epickur.api.dao.mongo;

import static com.epickur.api.dao.CollectionsName.ORDER_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class OrderDAOTest {

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
	private OrderDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(ORDER_COLL)).thenReturn(collMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();

		Order actual = dao.create(order);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();

		doThrow(new MongoException("")).when(collMock).insertOne(document);

		Order actual = dao.create(order);

		assertNotNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String orderId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(orderId));
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Order actual = dao.read(orderId);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String orderId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(orderId));

		when(collMock.find(query)).thenThrow(new MongoException(""));

		dao.read(orderId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		String orderId = "myId";

		dao.read(orderId);
	}

	@Test
	public void testUpdate() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();
		Document document = order.getDocumentDBView();

		when(collMock.findOneAndUpdate(anyObject(), anyObject(), anyObject()))
				.thenReturn(document);

		Order actual = dao.update(order);

		assertNotNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(anyObject(), anyObject(), anyObject());
	}

	@Test
	public void testUpdateNotFound() throws EpickurException {
		Order order = EntityGenerator.generateRandomOrder();

		when(collMock.findOneAndUpdate( anyObject(), anyObject(), anyObject())).thenReturn(null);

		Order actual = dao.update(order);

		assertNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(anyObject(), anyObject(), anyObject());
	}

	@Test
	public void testUpdateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);
		Order order = EntityGenerator.generateRandomOrder();

		when(collMock.findOneAndUpdate(anyObject(), anyObject(), anyObject())).thenThrow(new MongoException(""));

		dao.update(order);

		verify(dbMock, times(1)).getCollection(ORDER_COLL);
	}

	@Test
	public void testReadAll() throws Exception {
		thrown.expect(NotImplementedException.class);
		dao.readAll();
	}

	@Test
	public void testReadAllWithUserId() throws EpickurException {
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("createdBy", userId);
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Order> actuals = dao.readAllWithUserId(userId);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find(query);
		verify(cursor, times(1)).close();
	}

	@Test
	public void testReadAllWithUserIdMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String userId = new ObjectId().toHexString();
		Document query = new Document().append("createdBy", userId);

		when(collMock.find(query)).thenThrow(new MongoException(""));

		dao.readAllWithUserId(userId);
	}

	@Test
	public void testReadAllWithCatererId() throws EpickurException {
		String catererId = new ObjectId().toHexString();
		Document found = EntityGenerator.generateRandomOrder().getDocumentDBView();

		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		DateTime start = new DateTime().minusDays(5);
		DateTime end = new DateTime().plusDays(5);
		List<Order> actuals = dao.readAllWithCatererId(catererId, start, end);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find((Document) anyObject());
		verify(cursor, times(1)).close();
	}

	@Test
	public void testReadAllWithCatererIdMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String catererId = new ObjectId().toHexString();

		when(collMock.find((Document) anyObject())).thenThrow(new MongoException(""));

		DateTime start = new DateTime().minusDays(5);
		DateTime end = new DateTime().plusDays(5);
		dao.readAllWithCatererId(catererId, start, end);
	}
}
