package com.epickur.api.dao.mongo;

import static com.epickur.api.dao.CollectionsName.SEQUENCE_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.exception.EpickurException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class SequenceDAOTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private MongoDatabase dbMock;
	@Mock
	private MongoCollection<Document> collMock;
	@InjectMocks
	private SequenceDAO dao;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(SEQUENCE_COLL)).thenReturn(collMock);
	}
	
	@Test
	public void testNextId() throws EpickurException {
		Document found = new Document().append("seq", 5);
		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class))).thenReturn(found);
		
		String actual = dao.getNextId();

		assertNotNull(actual);
		assertEquals("5", actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class));
	}
	
	@Test
	public void testNextIdNotFound() throws EpickurException {
		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class))).thenReturn(null);
		
		String actual = dao.getNextId();

		assertNotNull(actual);
		assertEquals("0", actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class));
	}
}
