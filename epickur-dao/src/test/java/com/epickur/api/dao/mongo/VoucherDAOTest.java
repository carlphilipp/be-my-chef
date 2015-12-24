package com.epickur.api.dao.mongo;

import static com.epickur.api.dao.CollectionsName.VOUCHER_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import com.epickur.api.entity.Voucher;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class VoucherDAOTest {
	
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
	private VoucherDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(VOUCHER_COLL)).thenReturn(collMock);
	}

	@Test
	public void testCreate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		Voucher actual = dao.create(voucher);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		doThrow(new MongoException("")).when(collMock).insertOne(document);

		Voucher actual = dao.create(voucher);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Voucher actual = dao.read(code);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);

		when(collMock.find(query)).thenThrow(new MongoException(""));

		dao.read(code);
	}
	
	@Test
	public void testReadToClean() throws EpickurException {
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();

		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Voucher> actuals = dao.readToClean();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find(any(Document.class));
	}
	
	@Test
	public void testReadToCleanMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);
		
		when(collMock.find(any(Document.class))).thenThrow(new MongoException(""));

		dao.readToClean();
	}

	@Test
	public void testReadAll() throws EpickurException {
		thrown.expect(EpickurException.class);
		
		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		Voucher actual = dao.update(voucher);

		assertNotNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Voucher voucher = EntityGenerator.generateVoucher();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		Voucher actual = dao.update(voucher);

		assertNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Voucher voucher = EntityGenerator.generateVoucher();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class)))
				.thenThrow(new MongoException(""));

		dao.update(voucher);
	}
}
