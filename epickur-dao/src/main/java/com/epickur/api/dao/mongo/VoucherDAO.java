package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.epickur.api.utils.Info.VOUCHER_COLL;
import static com.mongodb.client.model.Filters.*;

/**
 * Voucher DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Repository
public class VoucherDAO extends CrudDAO<Voucher> {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(VoucherDAO.class.getSimpleName());
	/**
	 * Not implemented
	 */
	private static final String NOT_IMPLEMENTED = "Not implemented";

	@PostConstruct
	protected void initCollection() {
		setColl(getDb().getCollection(VOUCHER_COLL));
	}

	@Override
	public List<Voucher> readAll() throws EpickurException {
		throw new EpickurException(NOT_IMPLEMENTED);
	}

	@Override
	public Voucher create(final Voucher voucher) throws EpickurException {
		LOG.debug("Create voucher: " + voucher);
		Document doc = voucher.getDocumentDBView();
		insertDocument(doc);
		return Voucher.getDocumentAsVoucher(doc);
	}

	@Override
	public Voucher read(final String code) throws EpickurException {
		LOG.debug("Read voucher with code: " + code);
		Document query = convertAttributeToDocument("code", code);
		Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Voucher update(final Voucher voucher) throws EpickurException {
		LOG.debug("Update voucher: " + voucher);
		Document filter = convertAttributeToDocument("_id", voucher.getId());
		Document update = voucher.getUpdateQuery();
		Document updated = updateDocument(filter, update);
		return processAfterQuery(updated);
	}

	private Voucher processAfterQuery(final Document voucher) throws EpickurParsingException {
		if (voucher != null) {
			return Voucher.getDocumentAsVoucher(voucher);
		} else {
			return null;
		}
	}

	/**
	 * Delete all
	 */
	public void deleteAll() {
		getColl().drop();
	}

	/**
	 * @return A list of voucher that can be cleaned
	 * @throws EpickurException If an EpickurException occurred
	 */
	public List<Voucher> readToClean() throws EpickurException {
		try {
			LOG.debug("Read all vouchers to clean");
			DateTime date = new DateTime();
			Bson query = and(eq("expirationType", ExpirationType.UNTIL.getType()), lt("expiration", date.getMillis()),
					eq("status", Status.VALID.getType()));
			FindIterable<Document> find = getColl().find(query);
			if (find != null) {
				List<Voucher> res = new ArrayList<>();
				MongoCursor<Document> cursor = find.iterator();
				while (cursor.hasNext()) {
					Voucher current = Voucher.getDocumentAsVoucher(cursor.next());
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
