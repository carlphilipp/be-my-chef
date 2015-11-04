package com.epickur.api.dao.mongo;

import static com.epickur.api.utils.Info.DISH_COLL;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;

/**
 * Dish DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public class DishDAO extends CrudDAO<Dish> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(DishDAO.class.getSimpleName());

	/** Constructor */
	public DishDAO() {
		super();
		initCollection(DISH_COLL);
	}

	@Override
	public Dish create(final Dish dish) throws EpickurException {
		LOG.debug("Create dish: " + dish);
		Document doc = dish.getDocumentDBView();
		insertDocument(doc);
		return Dish.getDocumentAsDish(doc);
	}

	@Override
	public Dish read(final String id) throws EpickurException {
		LOG.debug("Read dish with id: " + id);
		Document query = convertAttributeToDocument("_id", new ObjectId(id));
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Dish update(final Dish dish) throws EpickurException {
		LOG.debug("Update dish: " + dish);
		Document filter = convertAttributeToDocument("_id", dish.getId());
		Document update = dish.getUpdateDocument();
		Document updated = updateDocument(filter, update);
		return processAfterQuery(updated);
	}

	/**
	 * @param document
	 *            The document.
	 * @return The dish.
	 * @throws EpickurParsingException
	 *             If an EpickurException occurred.
	 */
	private Dish processAfterQuery(final Document document) throws EpickurParsingException {
		if (document != null) {
			return Dish.getDocumentAsDish(document);
		} else {
			return null;
		}
	}

	@Override
	public List<Dish> readAll() throws EpickurException {
		MongoCursor<Document> cursor = null;
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dishes;
	}

	/**
	 * Search a list of Dish
	 * 
	 * @param day
	 *            The day
	 * @param pickupdateMinutes
	 *            The pickup date in minutes
	 * @param types
	 *            The type of Dish to search
	 * @param limit
	 *            The max result returned
	 * @param geo
	 *            The Geo
	 * @param distance
	 *            The distance
	 * @return A list of Dish
	 * @throws EpickurException
	 *             if an epickur exception occurred
	 */
	public List<Dish> search(final String day, final Integer pickupdateMinutes, final List<DishType> types, final Integer limit, final Geo geo,
			final Integer distance) throws EpickurException {
		Document find = new Document();
		if (types.size() == 1) {
			find.append("type", types.get(0).getType());
		} else {
			// db.dishes.find({$or: [{type:'meat'},{type:'fish'}]})
			BsonArray or = new BsonArray();
			for (DishType type : types) {
				BsonDocument content = new BsonDocument();
				content.append("type", new BsonString(type.getType()));
				or.add(content);
			}
			find.append("$or", or);
		}
		find.put("caterer.location.geo", geo.getSearch(0, distance));
		Document openClose = new Document();
		Document elementMatch = new Document();
		Document open = new Document();
		open.put("$lt", new BsonInt32(pickupdateMinutes));
		Document close = new Document();
		close.put("$gt", new BsonInt32(pickupdateMinutes));
		elementMatch.append("open", open);
		elementMatch.append("close", close);
		openClose.put("$elemMatch", elementMatch);
		find.put("caterer.workingTimes.hours." + day, openClose);
		List<Dish> dishes = new ArrayList<Dish>();
		MongoCursor<Document> cursor = null;
		LOG.debug("Searching: " + find);
		try {
			cursor = getColl().find(find).limit(limit).iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("search", e.getMessage(), find, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		// TODO See how to optimize that and avoid doing that here.
		// Should be doable in MongoDB.
		List<Dish> res = new ArrayList<Dish>();
		for (Dish dish : dishes) {
			Caterer cat = dish.getCaterer();
			WorkingTimes workingTimes = cat.getWorkingTimes();
			if (workingTimes.canBePickup(day, pickupdateMinutes)) {
				res.add(dish);
			}
		}
		return res;
	}

	/**
	 * @param catererId
	 *            The {@link Caterer} id.
	 * @return A list of {@link Dish} list.
	 * @throws EpickurException
	 *             if an epickur exception occurred
	 */
	public List<Dish> searchWithCatererId(final String catererId) throws EpickurException {
		MongoCursor<Document> cursor = null;
		List<Dish> dishes = new ArrayList<Dish>();
		Document find = new Document();
		find.append("caterer._id", new ObjectId(catererId));
		try {
			cursor = getColl().find(find).iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAllForOneCaterer", e.getMessage(), find, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dishes;
	}
}
