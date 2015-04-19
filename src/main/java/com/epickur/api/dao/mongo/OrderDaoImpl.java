package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Order DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public class OrderDaoImpl extends DaoCrud<Order> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(OrderDaoImpl.class.getSimpleName());

	/** Constructor **/
	public OrderDaoImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("orders"));
	}

	@Override
	public final Order create(final Order order) throws EpickurException {
		DateTime time = new DateTime();
		order.setCreatedAt(time);
		order.setUpdatedAt(time);
		DBObject dbo = null;
		try {
			dbo = order.getDBView();
			LOG.debug("Create order: " + order);
			getColl().insert(dbo);
			return Order.getObject(dbo);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), dbo, e);
		}
	}

	@Override
	public final Order read(final String id) throws EpickurException {
		try {
			LOG.debug("Read order: " + id);
			DBObject query = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return Order.getObject(obj);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	@Override
	public final Order update(final Order order) throws EpickurException {
		BasicDBObject bdb = (BasicDBObject) BasicDBObjectBuilder.start("_id", order.getId()).get();
		DateTime time = new DateTime();
		order.setCreatedAt(null);
		order.setUpdatedAt(time);
		LOG.debug("Update order: " + order);
		DBObject update = order.getUpdateBasicDBObject();
		try {
			DBObject temp = getColl().findAndModify(bdb, null, null, false, update, true, false);
			if (temp != null) {
				return Order.getObject(temp);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), bdb, update, e);
		}
	}

	@Override
	public final boolean delete(final String id) throws EpickurException {
		try {
			DBObject bdb = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			LOG.debug("Delete order: " + id);
			return this.succes(getColl().remove(bdb), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public final List<Order> readAll() throws EpickurException {
		throw new EpickurDBException();
	}

	/**
	 * Read all the Order for a User
	 * 
	 * @param userId
	 *            The User id
	 * @return A list of Order
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final List<Order> readAllWithUserId(final String userId) throws EpickurException {
		List<Order> orders = new ArrayList<Order>();
		DBObject query = BasicDBObjectBuilder.start("createdBy", userId).get();
		DBCursor cursor = null;
		try {
			cursor = getColl().find(query);
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Order user = Order.getObject(iterator.next());
				orders.add(user);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAllWithUserId", e.getMessage(), userId, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return orders;
	}

	/**
	 * @param catererId
	 *            the Caterer Id.
	 * @return A list of Order.
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
		List<Order> orders = new ArrayList<Order>();
		DBObject query = BasicDBObjectBuilder.start("dish.caterer._id", catererId).get();
		DBObject filter = BasicDBObjectBuilder.start().get();
		if (start != null) {
			filter.put("$gte", start.getMillis());
		}
		if (end != null) {
			filter.put("$lte", end.getMillis());
		}
		if(filter.keySet().size() != 0){
			query.put("createdAt", filter);
		}
		DBCursor cursor = null;
		try {
			cursor = getColl().find(query);
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Order user = Order.getObject(iterator.next());
				orders.add(user);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAllWithCatererId", e.getMessage(), catererId, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return orders;
	}
}
