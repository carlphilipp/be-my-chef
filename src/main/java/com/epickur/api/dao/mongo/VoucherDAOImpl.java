package com.epickur.api.dao.mongo;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.joda.time.DateTime;

import com.epickur.api.entity.User;
import com.epickur.api.entity.Voucher;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.ErrorUtils;
import com.mongodb.MongoException;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class VoucherDAOImpl extends DAOCrud<Voucher> {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(VoucherDAOImpl.class.getSimpleName());

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
			doc = voucher.getDBView();
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

}
