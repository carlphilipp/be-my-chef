package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Caterer;
import com.epickur.api.enumeration.View;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

/**
 * Caterer DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 */
public final class CatererDaoImpl extends DaoCrud<Caterer> {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(CatererDaoImpl.class.getSimpleName());

	/** Constructor **/
	public CatererDaoImpl() {
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
		DateTime time = new DateTime();
		caterer.setCreatedAt(time);
		caterer.setUpdatedAt(time);
		LOG.debug("Create caterer: " + caterer);
		Document doc = null;
		try {
			doc = caterer.getDBView();
			getColl().insertOne(doc);
			return Caterer.getObject(doc, View.DB);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getLocalizedMessage(), doc, e);
		}
	}

	@Override
	public Caterer read(final String id) throws EpickurException {
		try {
			LOG.debug("Read caterer: " + id);
			Document query = new Document().append("_id", new ObjectId(id));
			Document find = getColl().find(query).first();
			if (find != null) {
				return Caterer.getObject(find, View.DB);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getLocalizedMessage(), id, e);
		}
	}

	@Override
	public Caterer update(final Caterer caterer) throws EpickurException {
		Document filter = new Document().append("_id", caterer.getId());
		DateTime time = new DateTime();
		caterer.setCreatedAt(null);
		caterer.setUpdatedAt(time);
		LOG.debug("Update caterer: " + caterer);
		Document update = caterer.getUpdateDocument();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return Caterer.getObject(updated, View.DB);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getLocalizedMessage(), filter, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		try {
			Document filter = new Document().append("_id", new ObjectId(id));
			LOG.debug("Delete caterer: " + id);
			return this.isDeleted(getColl().deleteOne(filter), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getLocalizedMessage(), id, e);
		}
	}

	@Override
	public List<Caterer> readAll() throws EpickurException {
		List<Caterer> caterers = new ArrayList<Caterer>();
		MongoCursor<Document> cursor = null;
		try {
			cursor = getColl().find().iterator();
			while (cursor.hasNext()) {
				Caterer user = Caterer.getObject(cursor.next(), View.DB);
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
