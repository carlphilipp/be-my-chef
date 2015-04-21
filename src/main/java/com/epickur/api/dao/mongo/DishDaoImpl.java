package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
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
		Document update = dish.getUpdateBasicDBObject();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return Dish.getObject(updated);
			} else {
				return null;
			}
		} catch (MongoException e) {
			e.printStackTrace();
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
	public List<Dish> search(final DishType type, final Integer limit, final Geo geo, final Integer distance) throws EpickurException {
		Document document = new Document();
		document.append("type", type.getType());
		document.put("caterer.location.geo", geo.getSearch(0, distance));
		List<Dish> dishes = new ArrayList<Dish>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find(document).limit(limit).iterator();
			while (cursor.hasNext()) {
				Dish dish = Dish.getObject(cursor.next());
				dishes.add(dish);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("search", e.getMessage(), document, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return dishes;
	}
}
