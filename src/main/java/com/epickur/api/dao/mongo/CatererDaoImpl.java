package com.epickur.api.dao.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.Caterer;
import com.epickur.api.enumeration.View;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

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
		DBObject dbo = null;
		try {
			dbo = caterer.getDBView();
			getColl().insert(dbo);
			return Caterer.getObject(dbo, View.DB);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), dbo, e);
		}
	}

	@Override
	public Caterer read(final String id) throws EpickurException {
		try {
			LOG.debug("Read caterer: " + id);
			DBObject query = BasicDBObjectBuilder.start("_id", new ObjectId(id)).get();
			DBObject obj = (DBObject) getColl().findOne(query);
			if (obj != null) {
				return Caterer.getObject(obj, View.DB);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), id, e);
		}
	}

	@Override
	public Caterer update(final Caterer caterer) throws EpickurException {
		DBObject bdb = BasicDBObjectBuilder.start("_id", caterer.getId()).get();
		DateTime time = new DateTime();
		caterer.setCreatedAt(null);
		caterer.setUpdatedAt(time);
		LOG.debug("Update caterer: " + caterer);
		DBObject update = caterer.getUpdateBasicDBObject();
		try {
			DBObject temp = getColl().findAndModify(bdb, null, null, false, update, true, false);
			if (temp != null) {
				return Caterer.getObject(temp, View.DB);
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
			LOG.debug("Delete caterer: " + id);
			return this.succes(getColl().remove(bdb), "delete");
		} catch (MongoException e) {
			throw new EpickurDBException("delete", e.getMessage(), id, e);
		}
	}

	@Override
	public List<Caterer> readAll() throws EpickurException {
		List<Caterer> caterers = new ArrayList<Caterer>();
		DBCursor cursor = null;
		try {
			cursor = getColl().find();
			Iterator<DBObject> iterator = cursor.iterator();
			while (iterator.hasNext()) {
				Caterer user = Caterer.getObject(iterator.next(), View.DB);
				caterers.add(user);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readAll", e.getMessage(), e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return caterers;
	}
}
