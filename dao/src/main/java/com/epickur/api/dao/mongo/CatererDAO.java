package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Caterer;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.CATERER_COLL;

/**
 * Caterer DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class CatererDAO extends CrudDAO<Caterer> {

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(CATERER_COLL));
	}

	@Override
	public Caterer create(final Caterer caterer) throws EpickurException {
		log.debug("Create caterer: {}", caterer);
		final Document doc = caterer.getDocumentDBView();
		insertDocument(doc);
		return Caterer.getDocumentAsCatererDBView(doc);
	}

	@Override
	public Optional<Caterer> read(final String id) throws EpickurException {
		log.debug("Read caterer: {}", id);
		final Document query = convertAttributeToDocument("_id", new ObjectId(id));
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Caterer update(final Caterer caterer) throws EpickurException {
		log.debug("Update caterer: {}", caterer);
		final Document filter = convertAttributeToDocument("_id", caterer.getId());
		final Document update = caterer.getUpdateQuery();
		final Document updated = updateDocument(filter, update);
		return processAfterQuery(updated).orElse(null);
	}

	/**
	 * @param caterer The caterer.
	 * @return A Caterer
	 * @throws EpickurParsingException If an EpickurException occurred.
	 */
	private Optional<Caterer> processAfterQuery(final Document caterer) throws EpickurParsingException {
		return caterer != null
			? Optional.of(Caterer.getDocumentAsCatererDBView(caterer))
			: Optional.empty();
	}

	@Override
	public List<Caterer> readAll() throws EpickurException {
		final List<Caterer> caterers = new ArrayList<>();
		try (final MongoCursor<Document> cursor = getColl().find().iterator()) {
			while (cursor.hasNext()) {
				final Caterer user = Caterer.getDocumentAsCatererDBView(cursor.next());
				caterers.add(user);
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readAll", e.getLocalizedMessage(), e);
		}
		return caterers;
	}
}
