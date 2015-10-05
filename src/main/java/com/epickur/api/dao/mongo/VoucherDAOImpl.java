package com.epickur.api.dao.mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;

import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

/**
 * Voucher DAO access with CRUD operations.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class VoucherDAOImpl extends CrudDAO<Voucher> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(VoucherDAOImpl.class.getSimpleName());

	/** Constructor */
	public VoucherDAOImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		super.initDB();
		setColl(getDb().getCollection("vouchers"));
	}

	@Override
	public List<Voucher> readAll() throws EpickurException {
		throw new EpickurException(ErrorUtils.NOT_IMPLEMENTED);
	}

	@Override
	public Voucher create(final Voucher voucher) throws EpickurException {
		voucher.setId(null);
		DateTime time = new DateTime();
		voucher.setCreatedAt(time);
		voucher.setUpdatedAt(time);
		Document doc = null;
		try {
			doc = voucher.getDocumentDBView();
			LOG.debug("Create voucher: " + voucher);
			getColl().insertOne(doc);
			return Voucher.getObject(doc);
		} catch (MongoException e) {
			throw new EpickurDBException("create", e.getMessage(), doc, e);
		}
	}

	@Override
	public Voucher read(final String code) throws EpickurException {
		try {
			LOG.debug("Read voucher: " + code);
			Document query = new Document().append("code", code);
			Document find = getColl().find(query).first();
			if (find != null) {
				return Voucher.getObject(find);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("read", e.getMessage(), code, e);
		}
	}

	@Override
	public Voucher update(final Voucher voucher) throws EpickurException {
		Document filter = new Document().append("_id", voucher.getId());
		DateTime time = new DateTime();
		voucher.setCreatedAt(null);
		voucher.setUpdatedAt(time);
		LOG.debug("Update voucher: " + voucher);
		Document update = voucher.getUpdateDocument();
		try {
			Document updated = getColl().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
			if (updated != null) {
				return Voucher.getObject(updated);
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("update", e.getMessage(), filter, update, e);
		}
	}

	@Override
	public boolean delete(final String id) throws EpickurException {
		throw new EpickurException(ErrorUtils.NOT_IMPLEMENTED);
	}

	/**
	 * Delete all
	 */
	public void deleteAll() {
		getColl().drop();
	}

	/**
	 * @return A list of voucher that can be cleaned
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public List<Voucher> readToClean() throws EpickurException {
		try {
			LOG.debug("Read all vouchers to clean");
			DateTime date = new DateTime();
			Bson query = and(eq("expirationType", ExpirationType.UNTIL.getType()), lt("expiration", date.getMillis()),
					eq("status", Status.VALID.getType()));
			FindIterable<Document> find = getColl().find(query);
			if (find != null) {
				List<Voucher> res = new ArrayList<Voucher>();
				MongoCursor<Document> cursor = find.iterator();
				while (cursor.hasNext()) {
					Voucher current = Voucher.getObject(cursor.next());
					res.add(current);
				}
				return res;
			} else {
				return null;
			}
		} catch (MongoException e) {
			throw new EpickurDBException("readToClean", e.getMessage(), e);
		}
	}
}
