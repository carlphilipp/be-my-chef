package com.epickur.api.dao.mongo;

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
import org.bson.Document;
import org.bson.conversions.Bson;
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

import static com.epickur.api.dao.CollectionsName.VOUCHER_COLL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VoucherDAOTest {

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
	private VoucherDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(db.getCollection(VOUCHER_COLL)).thenReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		Voucher actual = dao.create(voucher);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		doThrow(new MongoException("")).when(collection).insertOne(document);

		Voucher actual = dao.create(voucher);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<Voucher> actual = dao.read(code);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);

		when(collection.find(query)).thenThrow(new MongoException(""));

		dao.read(code);
	}

	@Test
	public void testReadToClean() throws EpickurException {
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();

		given(collection.find(any(Bson.class))).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		List<Voucher> actual = dao.readToClean();

		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find(any(Bson.class));
	}

	@Test
	public void testReadToCleanMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		when(collection.find(any(Bson.class))).thenThrow(new MongoException(""));

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

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		Voucher actual = dao.update(voucher);

		assertNotNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Voucher voucher = EntityGenerator.generateVoucher();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		Voucher actual = dao.update(voucher);

		assertNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Voucher voucher = EntityGenerator.generateVoucher();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenThrow(new MongoException(""));

		dao.update(voucher);
	}
}
