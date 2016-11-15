package com.epickur.api.dao.mongo;

import com.epickur.api.exception.EpickurException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.epickur.api.dao.CollectionsName.SEQUENCE_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class SequenceDAOTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private MongoDatabase db;
	@Mock
	private MongoCollection<Document> collection;
	@InjectMocks
	private SequenceDAO dao;

	@Before
	public void setUp() throws Exception {
		given(db.getCollection(SEQUENCE_COLL)).willReturn(collection);
	}

	@Test
	public void testNextId() throws EpickurException {
		Document found = new Document().append("seq", 5);
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class))).willReturn(found);

		String actual = dao.getNextId();

		assertNotNull(actual);
		assertEquals("5", actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class));
	}

	@Test
	public void testNextIdNotFound() throws EpickurException {
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class))).willReturn(null);

		String actual = dao.getNextId();

		assertNotNull(actual);
		assertEquals("0", actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class));
	}
}
