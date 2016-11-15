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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
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
		given(db.getCollection(VOUCHER_COLL)).willReturn(collection);
		dao.initCollection();
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();

		// When
		Voucher actual = dao.create(voucher);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		dao.create(voucher);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<Voucher> actual = dao.read(code);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String code = new ObjectId().toHexString();
		Document query = new Document().append("code", code);
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.read(code);
	}

	@Test
	public void testReadToClean() throws EpickurException {
		// Given
		Document found = EntityGenerator.generateVoucher().getDocumentDBView();
		given(collection.find(any(Bson.class))).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Voucher> actual = dao.readToClean();

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find(any(Bson.class));
	}

	@Test
	public void testReadToCleanMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		given(collection.find(any(Bson.class))).willThrow(new MongoException(""));

		// When
		dao.readToClean();
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Then
		thrown.expect(EpickurException.class);

		// When
		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		Document document = voucher.getDocumentDBView();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(document);

		// When
		Voucher actual = dao.update(voucher);

		// Then
		assertNotNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(null);

		// When
		Voucher actual = dao.update(voucher);

		// Then
		assertNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Voucher voucher = EntityGenerator.generateVoucher();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willThrow(new MongoException(""));

		// When
		dao.update(voucher);
	}
}
