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
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;

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
		order.prepareForInsertionIntoDB();
		LOG.debug("Create order: " + order);
		Document doc = order.getDocumentDBView();
		insert(doc);
		return Order.getDocumentAsOrder(doc);
	}

	@Override
	public final Order read(final String id) throws EpickurException {
		LOG.debug("Read order with id: " + id);
		Document query = convertAttributeToDocument("_id", new ObjectId(id));
		Document find = find(query);
		return processAfterQuery(find);
	}

	@Override
	public final Order update(final Order order) throws EpickurException {
		order.prepareForUpdateIntoDB();
		LOG.debug("Update order: " + order);
		Document filter = convertAttributeToDocument("_id", order.getId());
		Document update = order.getOrderUpdateQuery();
		Document updated = update(filter, update);
		return processAfterQuery(updated);
	}

	private Order processAfterQuery(final Document document) throws EpickurParsingException {
		if (document != null) {
			return Order.getDocumentAsOrder(document);
		} else {
			return null;
		}
	}

	@Override
	public final boolean delete(final String id) throws EpickurException {
		LOG.debug("Delete order with id: " + id);
		Document filter = convertAttributeToDocument("_id", new ObjectId(id));
		return delete(filter);
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
				Order user = Order.getDocumentAsOrder(cursor.next());
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
				Order user = Order.getDocumentAsOrder(cursor.next());
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
