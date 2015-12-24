package com.epickur.api.dao.mongo;

import static com.epickur.api.dao.CollectionsName.LOG_COLL;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.epickur.api.entity.Log;
import com.epickur.api.exception.EpickurException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class LogDAOTest {
	
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
	private LogDAO dao;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(LOG_COLL)).thenReturn(collMock);
	}
	
	@Test
	public void testCreate() throws EpickurException {
		Log log = new Log();
		Document document = log.getDocumentDBView();

		Log actual = dao.create(log);

		assertNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}
	
	@Test
	public void testRead() throws EpickurException{
		thrown.expect(NotImplementedException.class);
		
		dao.read("");
	}
	
	@Test
	public void testUpdate() throws EpickurException{
		thrown.expect(NotImplementedException.class);
		
		Log log = new Log();
		dao.update(log);
	}
	
	@Test
	public void testDelete() throws EpickurException{
		thrown.expect(NotImplementedException.class);
		
		dao.delete("");
	}
	
	@Test
	public void testReadAll() throws EpickurException{
		thrown.expect(NotImplementedException.class);
		
		dao.readAll();
	}
}
