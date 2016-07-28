package com.epickur.api.dao.mongo;

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
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.DISH_COLL;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DishDAOTest {

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
	private DishDAO dao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(db.getCollection(DISH_COLL)).thenReturn(collection);
	}

	@Test
	public void testCreate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		Dish actual = dao.create(dish);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		doThrow(new MongoException("")).when(collection).insertOne(document);

		Dish actual = dao.create(dish);

		assertNotNull(actual);
		verify(collection).insertOne(document);
	}

	@Test
	public void testRead() throws EpickurException {
		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();

		when(collection.find(query)).thenReturn(findIteratble);
		when(findIteratble.first()).thenReturn(found);

		Optional<Dish> actual = dao.read(dishId);

		assertTrue(actual.isPresent());
		verify(collection).find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));

		when(collection.find(query)).thenThrow(new MongoException(""));

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

		when(collection.find()).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Dish> actuals = dao.readAll();

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collection).find();
		verify(cursor).close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		thrown.expect(EpickurDBException.class);

		when(collection.find()).thenThrow(new MongoException(""));

		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(document);

		Dish actual = dao.update(dish);

		assertNotNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		Dish dish = EntityGenerator.generateRandomDish();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).thenReturn(null);

		Dish actual = dao.update(dish);

		assertNull(actual);
		verify(collection).findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		thrown.expect(EpickurDBException.class);

		Dish dish = EntityGenerator.generateRandomDish();

		when(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class)))
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

		when(collection.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.limit(10)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collection).find(any(Document.class));
		verify(cursor).close();
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

		when(collection.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.limit(10)).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collection).find(any(Document.class));
		verify(cursor).close();
	}

	@Test
	public void testSearchMongoException() throws EpickurException{
		thrown.expect(EpickurDBException.class);

		List<DishType> dishTypes = new ArrayList<>();
		dishTypes.add(DishType.MAIN);
		Geo geo = EntityGenerator.generateGeo();

		when(collection.find(any(Document.class))).thenThrow(new MongoException(""));

		dao.search("mon", 5, dishTypes, 10, geo, 20);
	}

	@Test
	public void testSearchWithCatererId() throws EpickurException{
		String catererId = new ObjectId().toHexString();
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();

		when(collection.find(any(Document.class))).thenReturn(findIteratble);
		when(findIteratble.iterator()).thenReturn(cursor);
		when(cursor.hasNext()).thenReturn(true, false);
		when(cursor.next()).thenReturn(found);

		List<Dish> actuals = dao.searchWithCatererId(catererId);

		assertNotNull(actuals);
		assertEquals(1, actuals.size());
		verify(collection).find(any(Document.class));
		verify(cursor).close();
	}

	@Test
	public void testSearchWithCatererIdMongoException() throws EpickurException{
		thrown.expect(EpickurDBException.class);

		String catererId = new ObjectId().toHexString();

		when(collection.find(any(Document.class))).thenThrow(new MongoException(""));

		dao.searchWithCatererId(catererId);
	}
}
