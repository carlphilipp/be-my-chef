package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

/**
 * Order DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public class OrderDAOImpl extends CrudDAO<Order> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(OrderDAOImpl.class.getSimpleName());

	/** Constructor */
	public OrderDAOImpl() {
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
		order.setId(null);
		DateTime time = new DateTime();
		order.setCreatedAt(time);
		order.setUpdatedAt(time);
		Document doc = null;
		try {
			doc = order.getDBView();
			LOG.debug("Create order: " + order);
			getColl().insertOne(doc);
			return Order.getObject(doc);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public final Order read(final String id) throws EpickurException {
		try {
			LOG.debug("Read order: " + id);
			Document query = new Document().append("_id", new ObjectId(id));
			Document find = getColl().find(query).first();
			if (find != null) {
				return Order.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	@Override
	public final Order update(final Order order) throws EpickurException {
		Document filter = new Document().append("_id", order.getId());
		DateTime time = new DateTime();
		order.setReadableId(null);
		order.setCreatedAt(null);
		order.setUpdatedAt(time);
		LOG.debug("Update order: " + order);
		Document update = order.getUpdateDocument();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return Order.getObject(updated);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	@Override
	public final boolean delete(final String id) throws EpickurException {
		try {
			Document filter = new Document().append("_id", new ObjectId(id));
			LOG.debug("Delete order: " + id);
			return this.isDeleted(getColl().deleteOne(filter), "delete");
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
		Document query = new Document().append("createdBy", userId);
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find(query).iterator();
			while (cursor.hasNext()) {
				Order user = Order.getObject(cursor.next());
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
	 * @param start
	 *            The start date to filter on
	 * @param end
	 *            The start end to filter on
	 * @return A list of Orders
	 * @throws EpickurException
	 *             If an epickur exception occurred
	 */
	public final List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
		List<Order> orders = new ArrayList<Order>();
		Document query = new Document().append("dish.caterer._id", catererId);
		Document filter = new Document();
		if (start != null) {
			filter.put("$gte", start.getMillis());
		}
		if (end != null) {
			filter.put("$lte", end.getMillis());
		}
		if (filter.keySet().size() != 0) {
			query.put("createdAt", filter);
		}
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find(query).iterator();
			while (cursor.hasNext()) {
				Order user = Order.getObject(cursor.next());
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