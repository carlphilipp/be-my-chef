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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.DISH_COLL;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
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
		given(db.getCollection(DISH_COLL)).willReturn(collection);
		dao.initCollection();
	}

	@Test
	public void testCreate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();

		// When
		Dish actual = dao.create(dish);

		// Then
		assertNotNull(actual);
		then(collection).should().insertOne(document);
	}

	@Test
	public void testCreateMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();
		willThrow(new MongoException("")).given(collection).insertOne(document);

		// When
		dao.create(dish);
	}

	@Test
	public void testRead() throws EpickurException {
		// Given
		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();
		given(collection.find(query)).willReturn(findIteratble);
		given(findIteratble.first()).willReturn(found);

		// When
		Optional<Dish> actual = dao.read(dishId);

		// Then
		assertTrue(actual.isPresent());
		then(collection).should().find(query);
	}

	@Test
	public void testReadMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String dishId = new ObjectId().toHexString();
		Document query = new Document().append("_id", new ObjectId(dishId));
		given(collection.find(query)).willThrow(new MongoException(""));

		// When
		dao.read(dishId);
	}

	@Test
	public void testReadWrongIdFormat() throws Exception {
		// Then
		thrown.expect(IllegalArgumentException.class);

		// Given
		String dishId = "myId";

		// When
		dao.read(dishId);
	}

	@Test
	public void testReadAll() throws EpickurException {
		// Given
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();
		given(collection.find()).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Dish> actual = dao.readAll();

		// Then
		assertNotNull(actual);
		assertEquals(1, actual.size());
		then(collection).should().find();
		then(cursor).should().close();
	}

	@Test
	public void testReadAllMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		given(collection.find()).willThrow(new MongoException(""));

		// When
		dao.readAll();
	}

	@Test
	public void testUpdate() throws EpickurException {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		Document document = dish.getDocumentDBView();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(document);

		// When
		Dish actual = dao.update(dish);

		// Then
		assertNotNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateNotFound() throws Exception {
		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willReturn(null);

		// When
		Dish actual = dao.update(dish);

		// Then
		assertNull(actual);
		then(collection).should().findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class));
	}

	@Test
	public void testUpdateMongoException() throws Exception {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		Dish dish = EntityGenerator.generateRandomDish();
		given(collection.findOneAndUpdate(any(Document.class), any(Document.class), any(FindOneAndUpdateOptions.class))).willThrow(new MongoException(""));

		// When
		dao.update(dish);
	}

	@Test
	public void testSearchOneType() throws EpickurException {
		// Given
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
		given(collection.find(any(Document.class))).willReturn(findIteratble);
		given(findIteratble.limit(10)).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);

		// Then
		assertNotNull(actuals);
		assertThat(actuals, hasSize(1));
		then(collection).should().find(any(Document.class));
		then(cursor).should().close();
	}

	@Test
	public void testSearchMultipleType() throws EpickurException {
		// Given
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
		given(collection.find(any(Document.class))).willReturn(findIteratble);
		given(findIteratble.limit(10)).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Dish> actuals = dao.search("mon", 5, dishTypes, 10, geo, 20);

		// Then
		assertNotNull(actuals);
		assertThat(actuals, hasSize(1));
		then(collection).should().find(any(Document.class));
		then(cursor).should().close();
	}

	@Test
	public void testSearchMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		List<DishType> dishTypes = new ArrayList<>();
		dishTypes.add(DishType.MAIN);
		Geo geo = EntityGenerator.generateGeo();
		given(collection.find(any(Document.class))).willThrow(new MongoException(""));

		// When
		dao.search("mon", 5, dishTypes, 10, geo, 20);
	}

	@Test
	public void testSearchWithCatererId() throws EpickurException {
		// Given
		String catererId = new ObjectId().toHexString();
		Document found = EntityGenerator.generateRandomDish().getDocumentDBView();
		given(collection.find(any(Document.class))).willReturn(findIteratble);
		given(findIteratble.iterator()).willReturn(cursor);
		given(cursor.hasNext()).willReturn(true, false);
		given(cursor.next()).willReturn(found);

		// When
		List<Dish> actual = dao.searchWithCatererId(catererId);

		// Then
		assertNotNull(actual);
		assertThat(actual, hasSize(1));
		then(collection).should().find(any(Document.class));
		then(cursor).should().close();
	}

	@Test
	public void testSearchWithCatererIdMongoException() throws EpickurException {
		// Then
		thrown.expect(EpickurDBException.class);

		// Given
		String catererId = new ObjectId().toHexString();
		given(collection.find(any(Document.class))).willThrow(new MongoException(""));

		// When
		dao.searchWithCatererId(catererId);
	}
}
