package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.epickur.api.dao.CollectionsName.LOG_COLL;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class LogDAOTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private MongoDatabase db;
	@Mock
	private MongoCollection<Document> collection;
	@Mock
	private FindIterable<Document> documentFindIterable;
	@Mock
	private MongoCursor<Document> cursor;
	@InjectMocks
	private LogDAO dao;

	@Before
	public void setUp() throws Exception {
		given(db.getCollection(LOG_COLL)).willReturn(collection);
		dao.initCollection();
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Log log = new Log();
		Document document = log.getDocumentDBView();

		// When
		Log actual = dao.create(log);

		// Then
		assertNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		// Then
		thrown.expect(NotImplementedException.class);

		// When
		dao.read("");
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Then
		thrown.expect(NotImplementedException.class);

		// When
		Log log = new Log();
		dao.update(log);
	}

	@Test
	public void testDelete() throws EpickurException {
		// Then
		thrown.expect(NotImplementedException.class);

		// When
		dao.delete("");
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Then
		thrown.expect(NotImplementedException.class);

		// When
		dao.readAll();
	}
}
