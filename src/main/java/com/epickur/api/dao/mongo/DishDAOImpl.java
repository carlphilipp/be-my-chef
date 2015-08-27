package com.epickur.api.dao.mongo;

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
import org.joda.time.DateTime;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

/**
 * Dish DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class DishDAOImpl extends CrudDAO<Dish> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(DishDAOImpl.class.getSimpleName());

	/** Constructor */
	public DishDAOImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("dishes"));
	}

	@Override
	public Dish create(final Dish dish) throws EpickurException {
		dish.setId(null);
		DateTime time = new DateTime();
		dish.setCreatedAt(time);
		dish.setUpdatedAt(time);
		LOG.debug("Create dish: " + dish);
		Document doc = null;
		try {
			doc = dish.getDBView();
			getColl().insertOne(doc);
			return Dish.getObject(doc);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public Dish read(final String id) throws EpickurException {
		try {
			Document query = new Document().append("_id", new ObjectId(id));
			LOG.debug("Read dish: " + id);
			Document find = getColl().find(query).first();
			if (find != null) {
				return Dish.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	@Override
	public Dish update(final Dish dish) throws EpickurException {
		Document filter = new Document().append("_id", dish.getId());
		DateTime time = new DateTime();
		dish.setCreatedAt(null);
		dish.setUpdatedAt(time);
		LOG.debug("Update dish: " + dish);
		Document update = dish.getUpdateDocument();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return Dish.getObject(updated);
			} else {
				return null;
			}
		} catch (MongoException e) {
			LOG.error(e.getLocalizedMessage(), e);
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		try {
			Document filter = new Document().append("_id", new ObjectId(id));
			LOG.debug("Delete dish: " + id);
			return this.isDeleted(getColl().deleteOne(filter), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public List<Dish> readAll() throws EpickurException {
		MongoCursor<Document> cursor = null;
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getObject(cursor.next());
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
				Dish dish = Dish.getObject(cursor.next());
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
	public List<Dish> search(final String catererId) throws EpickurException {
		MongoCursor<Document> cursor = null;
		List<Dish> dishes = new ArrayList<Dish>();
		Document find = new Document();
		find.append("caterer._id", catererId);
		try {
			cursor = getColl().find(find).iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getObject(cursor.next());
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