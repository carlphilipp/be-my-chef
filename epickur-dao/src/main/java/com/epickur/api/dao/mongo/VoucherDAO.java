package com.epickur.api.dao.mongo;

import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurDBException;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epickur.api.dao.CollectionsName.VOUCHER_COLL;
import static com.mongodb.client.model.Filters.*;

/**
 * Voucher DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@Repository
public class VoucherDAO extends CrudDAO<Voucher> {

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
		log.debug("Create voucher: " + voucher);
		final Document doc = voucher.getDocumentDBView();
		insertDocument(doc);
		return Voucher.getDocumentAsVoucher(doc);
	}

	@Override
	public Optional<Voucher> read(final String code) throws EpickurException {
		log.debug("Read voucher with code: " + code);
		final Document query = convertAttributeToDocument("code", code);
		final Document find = findDocument(query);
		return processAfterQuery(find);
	}

	@Override
	public Voucher update(final Voucher voucher) throws EpickurException {
		log.debug("Update voucher: " + voucher);
		final Document filter = convertAttributeToDocument("_id", voucher.getId());
		final Document update = voucher.getUpdateQuery();
		final Document updated = updateDocument(filter, update);
		return processAfterQuery(updated).orElse(null);
	}

	private Optional<Voucher> processAfterQuery(final Document voucher) throws EpickurParsingException {
		return voucher != null
			? Optional.of(Voucher.getDocumentAsVoucher(voucher))
			: Optional.empty();
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
			log.debug("Read all vouchers to clean");
			final DateTime date = new DateTime();
			final Bson query = and(eq("expirationType", ExpirationType.UNTIL.getType()), lt("expiration", date.getMillis()),
				eq("status", Status.VALID.getType()));
			final FindIterable<Document> find = getColl().find(query);
			if (find != null) {
				final List<Voucher> res = new ArrayList<>();
				for (final Document document : find) {
					final Voucher current = Voucher.getDocumentAsVoucher(document);
					res.add(current);
				}
				return res;
			} else {
				return null;
			}
		} catch (final MongoException e) {
			throw new EpickurDBException("readToClean", e.getMessage(), e);
		}
	}
}
