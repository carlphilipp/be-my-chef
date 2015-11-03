package com.epickur.api.dao.mongo;

import static com.epickur.api.utils.Info.ORDER_COLL;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.epickur.api.InitMocks;
import com.epickur.api.TestUtils;
import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class OrderDAOTest extends InitMocks {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private OrderDAO dao;
	@Mock
	private MongoDatabase dbMock;
	@Mock
	private MongoCollection<Document> collMock;
	@Mock
	private FindIterable<Document> findIteratble;
	@Mock
	private MongoCursor<Document> cursor;

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestUtils.resetStripe();
	}

	@Before
	public void setUp() throws Exception {
		when(dbMock.getCollection(ORDER_COLL)).thenReturn(collMock);
		dao = new OrderDAO(dbMock);
	}

	@After
	public void tearDown() throws Exception {
		dao = null;
	}

	@Test
	public void testDBParameters() {
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
	}

	@Test
	public void testCreate() throws Exception {
		Order order = TestUtils.generateRandomOrder();
		Document document = order.getDocumentDBView();

		Order actual = dao.create(order);

		assertNotNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Order order = TestUtils.generateRandomOrder();
		Document document = order.getDocumentDBView();

		Mockito.doThrow(new MongoException("")).when(collMock).insertOne(document);

		Order actual = dao.create(order);

		assertNotNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws Exception {
		String orderId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(orderId));
		Document found = TestUtils.generateRandomOrder().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Order actual = dao.read(orderId);

		assertNotNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		String orderId = "myId";

		dao.read(orderId);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
	}

	@Test
	public void testUpdate() throws Exception {
		Order order = TestUtils.generateRandomOrder();
		Document document = order.getDocumentDBView();

		when(collMock.findOneAndUpdate((Document) anyObject(), (Document) anyObject(), (FindOneAndUpdateOptions) anyObject())).thenReturn(document);

		Order actual = dao.update(order);

		assertNotNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).findOneAndUpdate((Document) anyObject(), (Document) anyObject(), (FindOneAndUpdateOptions) anyObject());
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Order order = TestUtils.generateRandomOrder();

		when(collMock.findOneAndUpdate((Document) anyObject(), (Document) anyObject(), (FindOneAndUpdateOptions) anyObject())).thenReturn(null);

		Order actual = dao.update(order);

		assertNull(actual);
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
		verify(collMock, times(1)).findOneAndUpdate((Document) anyObject(), (Document) anyObject(), (FindOneAndUpdateOptions) anyObject());
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Order order = TestUtils.generateRandomOrder();

		when(collMock.findOneAndUpdate((Document) anyObject(), (Document) anyObject(), (FindOneAndUpdateOptions) anyObject()))
				.thenThrow(new MongoException(""));

		dao.update(order);

		verify(dbMock, times(1)).getCollection(ORDER_COLL);
	}

	@Test
	public void testReadAll() throws Exception {
		thrown.expect(NotImplementedException.class);
		dao.readAll();
	}

	@Test
	public void testReadAllWithUserId() throws Exception {
		String userId = new ObjectId().toHexString();
		Document query = new Document().append("createdBy", userId);
		Document found = TestUtils.generateRandomOrder().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Order> actuals = dao.readAllWithUserId(userId);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
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
	public void testReadAllWithCatererId() throws Exception {
		String catererId = new ObjectId().toHexString();
		Document found = TestUtils.generateRandomOrder().getDocumentDBView();

		when(collMock.find((Document) anyObject())).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		DateTime start = new DateTime().minusDays(5);
		DateTime end = new DateTime().plusDays(5);
		List<Order> actuals = dao.readAllWithCatererId(catererId, start, end);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(dbMock, times(1)).getCollection(ORDER_COLL);
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
