package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epickur.api.dao.CollectionsName.DISH_COLL;

/**
 * Dish DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class DishDAO extends CrudDAO<Dish> {

	@Autowired
	public DishDAO(final MongoDatabase db) {
		super(db);
	}

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(DISH_COLL));
	}

	@Override
	public Dish create(final Dish dish) throws EpickurException {
		log.debug("Create dish: {}", dish);
		final Document doc = dish.getDocumentDBView();
		insertDocument(doc);
		return Dish.getDocumentAsDish(doc);
	}

	@Override
	public Optional<Dish> read(final String id) throws EpickurException {
		log.debug("Read dish with id: {}", id);
		final Document query = convertAttributeToDocument("_id", new ObjectId(id));
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Dish update(final Dish dish) throws EpickurException {
		log.debug("Update dish: {}", dish);
		final Document filter = convertAttributeToDocument("_id", dish.getId());
		final Document update = dish.getUpdateQuery();
		final Document updated = updateDocument(filter, update);
		return processAfterQuery(updated).orElse(null);
	}

	/**
	 * @param document The document.
	 * @return The dish.
	 * @throws EpickurParsingException If an EpickurException occurred.
	 */
	private Optional<Dish> processAfterQuery(final Document document) throws EpickurParsingException {
		return document != null
			? Optional.of(Dish.getDocumentAsDish(document))
			: Optional.empty();
	}

	@Override
	public List<Dish> readAll() throws EpickurException {
		final List<Dish> dishes = new ArrayList<>();
		try (final MongoCursor<Document> cursor = getColl().find().iterator()) {
			while (cursor.hasNext()) {
				final Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		}
		return dishes;
	}

	/**
	 * Search a list of Dish
	 *
	 * @param day               The day
	 * @param pickupdateMinutes The pickup date in minutes
	 * @param types             The type of Dish to search
	 * @param limit             The max result returned
	 * @param geo               The Geo
	 * @param distance          The distance
	 * @return A list of Dish
	 * @throws EpickurException if an epickur exception occurred
	 */
	public List<Dish> search(final String day, final Integer pickupdateMinutes, final List<DishType> types, final Integer limit, final Geo geo,
							 final Integer distance) throws EpickurException {
		final Document find = new Document();
		if (types.size() == 1) {
			find.append("type", types.get(0).getType());
		} else {
			// db.dishes.find({$or: [{type:'meat'},{type:'fish'}]})
			final BsonArray or = new BsonArray();
			for (final DishType type : types) {
				final BsonDocument content = new BsonDocument();
				content.append("type", new BsonString(type.getType()));
				or.add(content);
			}
			find.append("$or", or);
		}
		find.put("caterer.location.geo", geo.getSearch(0, distance));
		final Document openClose = new Document();
		final Document elementMatch = new Document();
		final Document open = new Document();
		open.put("$lt", new BsonInt32(pickupdateMinutes));
		final Document close = new Document();
		close.put("$gt", new BsonInt32(pickupdateMinutes));
		elementMatch.append("open", open);
		elementMatch.append("close", close);
		openClose.put("$elemMatch", elementMatch);
		find.put("caterer.workingTimes.hours." + day, openClose);
		final List<Dish> dishes = new ArrayList<>();
		log.debug("Searching: {}", find);
		try (MongoCursor<Document> cursor = getColl().find(find).limit(limit).iterator()) {
			while (cursor.hasNext()) {
				final Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("search", e.getMessage(), find, e);
		}
		// TODO See how to optimize that and avoid doing that here.
		// Should be doable in MongoDB.
		return dishes.stream()
			.filter(dish -> dish.getCaterer().getWorkingTimes().canBePickup(day, pickupdateMinutes))
			.collect(Collectors.toList());
	}

	/**
	 * @param catererId The {@link Caterer} id.
	 * @return A list of {@link Dish} list.
	 * @throws EpickurException if an epickur exception occurred
	 */
	public List<Dish> searchWithCatererId(final String catererId) throws EpickurException {
		final List<Dish> dishes = new ArrayList<>();
		final Document find = new Document();
		find.append("caterer._id", new ObjectId(catererId));
		try (MongoCursor<Document> cursor = getColl().find(find).iterator()) {
			while (cursor.hasNext()) {
				final Dish dish = Dish.getDocumentAsDish(cursor.next());
				dishes.add(dish);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAllForOneCaterer", e.getMessage(), find, e);
		}
		return dishes;
	}
}
