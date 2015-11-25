package com.epickur.api.dao.mongo;

import static com.epickur.api.utils.Info.DISH_COLL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.times.Hours;
import com.epickur.api.entity.times.TimeFrame;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class DishDAOTest {
	
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
	private DishDAO dao;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(dbMock.getCollection(DISH_COLL)).thenReturn(collMock);
	}
	
	@Test
	public void testCreate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		Dish actual = dao.create(dish);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		doThrow(new MongoException("")).when(collMock).insertOne(document);

		Dish actual = dao.create(dish);

		assertNotNull(actual);
		verify(collMock, times(1)).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();

		when(collMock.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Dish actual = dao.read(dishId);

		assertNotNull(actual);
		verify(collMock, times(1)).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));

		when(collMock.find(query)).thenThrow(new MongoException(""));

		dao.read(dishId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		String dishId = "myId";

		dao.read(dishId);
	}

	@Test
	public void testReadAll() throws EpickurException {
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();

		when(collMock.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Dish> actuals = dao.readAll();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find();
		verify(cursor, times(1)).close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		when(collMock.find()).thenThrow(new MongoException(""));

		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		Dish actual = dao.update(dish);

		assertNotNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Dish dish = EntityGenerator.generateRandomDish();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		Dish actual = dao.update(dish);

		assertNull(actual);
		verify(collMock, times(1)).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Dish dish = EntityGenerator.generateRandomDish();

		when(collMock.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class)))
				.thenThrow(new MongoException(""));

		dao.update(dish);
	}
	
	@Test
	public void testSearchOneType() throws EpickurException{
		Dish dish = EntityGenerator.generateRandomDish();
		WorkingTimes wt = new WorkingTimes();
		Hours hours = new Hours();
		TimeFrame timeFrame = new TimeFrame();
		timeFrame.setOpen(1);
		timeFrame.setClose(11);
		List<TimeFrame> timeFrames = new ArrayList<>();
		timeFrames.add(timeFrame);
		hours.setMon(timeFrames);
		wt.setHours(hours);
		wt.setMinimumPreparationTime(0);
		dish.getCaterer().setWorkingTimes(wt);
		Document found = dish.getDocumentDBView();
		List<DishType> dishTypes = new ArrayList<>();
		dishTypes.add(DishType.MAIN);
		Geo geo = EntityGenerator.generateGeo();
		
		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.limit(10)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);
		
		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);
		
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find(any(Document.class));
		verify(cursor, times(1)).close();
	}
	
	@Test
	public void testSearchMultipleType() throws EpickurException{
		Dish dish = EntityGenerator.generateRandomDish();
		WorkingTimes wt = new WorkingTimes();
		Hours hours = new Hours();
		TimeFrame timeFrame = new TimeFrame();
		timeFrame.setOpen(1);
		timeFrame.setClose(11);
		List<TimeFrame> timeFrames = new ArrayList<>();
		timeFrames.add(timeFrame);
		hours.setMon(timeFrames);
		wt.setHours(hours);
		wt.setMinimumPreparationTime(0);
		dish.getCaterer().setWorkingTimes(wt);
		Document found = dish.getDocumentDBView();
		List<DishType> dishTypes = new ArrayList<>();
		dishTypes.add(DishType.MAIN);
		dishTypes.add(DishType.DESSERT);
		Geo geo = EntityGenerator.generateGeo();
		
		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.limit(10)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);
		
		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);
		
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find(any(Document.class));
		verify(cursor, times(1)).close();
	}
	
	@Test
	public void testSearchMongoException() throws EpickurException{
		thrown.expect(EpickurDBException.class);
		
		List<DishType> dishTypes = new ArrayList<>();
		dishTypes.add(DishType.MAIN);
		Geo geo = EntityGenerator.generateGeo();
		
		when(collMock.find(any(Document.class))).thenThrow(new MongoException(""));
		
		dao.search("mon", 5, dishTypes, 10, geo, 20);
	}

	@Test
	public void testSearchWithCatererId() throws EpickurException{
		String catererId = new ObjectId().toHexString();
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();
		
		when(collMock.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);
		
		List<Dish> actuals = dao.searchWithCatererId(catererId);
		
		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collMock, times(1)).find(any(Document.class));
		verify(cursor, times(1)).close();
	}
	
	@Test
	public void testSearchWithCatererIdMongoException() throws EpickurException{
		thrown.expect(EpickurDBException.class);
		
		String catererId = new ObjectId().toHexString();
		
		when(collMock.find(any(Document.class))).thenThrow(new MongoException(""));
		
		dao.searchWithCatererId(catererId);
	}
}
