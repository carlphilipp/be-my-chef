package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Order;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.ORDER_COLL;

/**
 * Order DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class OrderDAO extends CrudDAO<Order> {

	@Autowired
	public OrderDAO(final MongoDatabase db) {
		super(db);
	}

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(ORDER_COLL));
	}

	@Override
	public Order create(final Order order) throws EpickurException {
		log.debug("Create order: {}", order);
		final Document doc = order.getDocumentDBView();
		insertDocument(doc);
		return Order.getDocumentAsOrder(doc);
	}

	@Override
	public Optional<Order> read(final String id) throws EpickurException {
		log.debug("Read order with id: {}", id);
		final Document query = convertAttributeToDocument("_id", new ObjectId(id));
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Order update(final Order order) throws EpickurException {
		log.debug("Update order: {}", order);
		final Document filter = convertAttributeToDocument("_id", order.getId());
		final Document update = order.getUpdateQuery();
		final Document updated = updateDocument(filter, update);
		return processAfterQuery(updated).orElse(null);
	}

	private Optional<Order> processAfterQuery(final Document document) throws EpickurParsingException {
		return document != null
			? Optional.of(Order.getDocumentAsOrder(document))
			: Optional.empty();
	}

	@Override
	public List<Order> readAll() throws EpickurException {
		throw new NotImplementedException("Not implemented yet");
	}

	/**
	 * Read all the Order for a User
	 *
	 * @param userId The User id
	 * @return A list of Order
	 * @throws EpickurException If an epickur exception occurred
	 */
	public List<Order> readAllWithUserId(final String userId) throws EpickurException {
		final List<Order> orders = new ArrayList<>();
		final Document query = new Document().append("createdBy", userId);
		try (final MongoCursor<Document> cursor = getColl().find(query).iterator()) {
			while (cursor.hasNext()) {
				final Order user = Order.getDocumentAsOrder(cursor.next());
				orders.add(user);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAllWithUserId", e.getMessage(), userId, e);
		}
		return orders;
	}

	/**
	 * @param catererId the Caterer Id.
	 * @param start     The start date to filter on
	 * @param end       The start end to filter on
	 * @return A list of Orders
	 * @throws EpickurException If an epickur exception occurred
	 */
	public List<Order> readAllWithCatererId(final String catererId, final DateTime start, final DateTime end) throws EpickurException {
		final List<Order> orders = new ArrayList<>();
		final Document query = new Document().append("dish.caterer._id", catererId);
		final Document filter = new Document();
		if (start != null) {
			filter.put("$gte", start.getMillis());
		}
		if (end != null) {
			filter.put("$lte", end.getMillis());
		}
		if (filter.keySet().size() != 0) {
			query.put("createdAt", filter);
		}
		try (final MongoCursor<Document> cursor = getColl().find(query).iterator()) {
			while (cursor.hasNext()) {
				final Order user = Order.getDocumentAsOrder(cursor.next());
				orders.add(user);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAllWithCatererId", e.getMessage(), catererId, e);
		}
		return orders;
	}
}
