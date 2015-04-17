package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Dish DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class DishDaoImpl extends DaoCrud<Dish> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(DishDaoImpl.class.getSimpleName());

	/** Constructor **/
	public DishDaoImpl() {
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
		// dish.setId(new ObjectId());
		DateTime time = new DateTime();
		dish.setCreatedAt(time);
		dish.setUpdatedAt(time);
		LOG.debug("Create dish: " + dish);
		DBObject dbo = null;
		try {
			dbo = dish.getDBView();
			getColl().insert(dbo);
			return Dish.getObject(dbo);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), dbo, e);
		}
	}

	@Override
	public Dish read(final String id) throws EpickurException {
		try {
			DBObject query = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			LOG.debug("Read dish: " + id);

			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return Dish.getObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	@Override
	public Dish update(final Dish dish) throws EpickurException {
		BasicDBObject bdb = (BasicDBObject) BasicDBObjectBuilder.start("_id", dish.getId()).get();
		DateTime time = new DateTime();
		dish.setCreatedAt(null);
		dish.setUpdatedAt(time);
		LOG.debug("Update dish: " + dish);
		DBObject update = dish.getUpdateBasicDBObject();
		try {
			DBObject temp = getColl().findAndModify(bdb, null, null, false, update, true, false);
			if (temp != null) {
				return Dish.getObject(temp);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), bdb, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		try {
			DBObject bdb = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			LOG.debug("Delete dish: " + id);
			return this.succes(getColl().remove(bdb), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public List<Dish> readAll() throws EpickurException {
		DBCursor cursor = null;
		List<Dish> dishes = new ArrayList<Dish>();
		try {
			cursor = getColl().find();
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Dish dish = Dish.getObject(iterator.next());
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
	 * @param type
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
	public List<Dish> search(final String type, final Integer limit, final Geo geo, final Integer distance) throws EpickurException {
		DBObject bdb = BasicDBObjectBuilder.start().get();
		bdb.put("type", type);
		bdb.put("caterer.location.geo", geo.getSearch(0, distance));
		List<Dish> dishes = new ArrayList<Dish>();
		DBCursor cursor = null;
		try {
			cursor = getColl().find(bdb).limit(limit);
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Dish dish = Dish.getObject(iterator.next());
				dishes.add(dish);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("search", e.getMessage(), bdb, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dishes;
	}
}
