package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.epickur.api.entity.Caterer;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;

/**
 * Caterer DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class CatererDAOImpl extends CrudDAO<Caterer> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(CatererDAOImpl.class.getSimpleName());

	/** Constructor */
	public CatererDAOImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("caterers"));
	}

	@Override
	public Caterer create(final Caterer caterer) throws EpickurException {
		LOG.debug("Create caterer: " + caterer);
		Document doc = caterer.getDocumentDBView();
		insertDocument(doc);
		return Caterer.getDocumentAsCatererDBView(doc);
	}

	@Override
	public Caterer read(final String id) throws EpickurException {
		LOG.debug("Read caterer: " + id);
		Document query = convertAttributeToDocument("_id", new ObjectId(id));
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Caterer update(final Caterer caterer) throws EpickurException {
		LOG.debug("Update caterer: " + caterer);
		Document filter = convertAttributeToDocument("_id", caterer.getId());
		Document update = caterer.getUpdateDocument();
		Document updated = updateDocument(filter, update);
		return processAfterQuery(updated);
	}

	private Caterer processAfterQuery(final Document caterer) throws EpickurParsingException {
		if (caterer != null) {
			return Caterer.getDocumentAsCatererDBView(caterer);
		} else {
			return null;
		}
	}

	@Override
	public List<Caterer> readAll() throws EpickurException {
		List<Caterer> caterers = new ArrayList<Caterer>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Caterer user = Caterer.getDocumentAsCatererDBView(cursor.next());
				caterers.add(user);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAll", e.getLocalizedMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return caterers;
	}
}
