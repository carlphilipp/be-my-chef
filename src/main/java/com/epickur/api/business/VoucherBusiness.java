package com.epickur.api.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Utils;

/**
 * Voucher business layer. Access voucher DAO layer and executes logic.
 * 
 * @author cph
 * @version 1.0
 *
 */
public class VoucherBusiness {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(VoucherBusiness.class.getSimpleName());
	/** User dao */
	private VoucherDAO voucherDAO;

	/**
	 * The constructor
	 */
	public VoucherBusiness() {
		this.voucherDAO = new VoucherDAO();
	}

	/**
	 * @param code
	 *            The Voucher code
	 * @return A Voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Voucher read(final String code) throws EpickurException {
		return voucherDAO.read(code);
	}

	/**
	 * Generate vouchers. We use a Set to store data, and we need to check in the database if the voucher code has not been generated already.
	 * 
	 * @param count
	 *            The number of vouchers
	 * @param discountType
	 *            The discount type
	 * @param discount
	 *            The discount amount or percentage
	 * @param expirationType
	 *            The expiration type
	 * @param expiration
	 *            The expiration date
	 * @return a Set of vouchers generated
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Set<Voucher> generate(final int count, final DiscountType discountType, final int discount, final ExpirationType expirationType,
			final DateTime expiration) throws EpickurException {
		Set<Voucher> result = new HashSet<Voucher>();
		do {
			Voucher voucher = new Voucher();
			voucher.setCode(Utils.generateRandomCode());
			voucher.setDiscount(discount);
			voucher.setDiscountType(discountType);
			voucher.setExpirationType(expirationType);
			if (expiration != null) {
				voucher.setExpiration(expiration);
				voucher.setUsedCount(0);
			}
			voucher.setStatus(Status.VALID);
			boolean added = result.add(voucher);
			if (added) {
				Voucher temp = voucherDAO.read(voucher.getCode());
				if (temp != null) {
					result.remove(voucher);
				} else {
					voucher.prepareForInsertionIntoDB();
					voucherDAO.create(voucher);
				}
			}
		} while (result.size() < count);
		return result;
	}

	/**
	 * @return A list of voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred.
	 */
	public List<Voucher> clean() throws EpickurException {
		List<Voucher> vouchers = this.voucherDAO.readToClean();
		for (Voucher voucher : vouchers) {
			LOG.info("Expire voucher " + voucher.getCode() + " " + voucher.getExpiration());
			voucher.setStatus(Status.EXPIRED);
			voucher.prepareForUpdateIntoDB();
			this.voucherDAO.update(voucher);
		}
		return vouchers;
	}

	/**
	 * @param code
	 *            The code
	 * @return The Voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Voucher validateVoucher(final String code) throws EpickurException {
		Voucher found = this.readAndThrowException(code);
		if (found.getStatus() == Status.EXPIRED) {
			throw new EpickurException("Voucher '" + code + "' expired");
		}
		if (found.getExpirationType() == ExpirationType.ONETIME) {
			found.setStatus(Status.EXPIRED);
		} else if (found.getExpirationType() == ExpirationType.UNTIL) {
			if (found.getUsedCount() != null) {
				found.setUsedCount(found.getUsedCount() + 1);
			} else {
				found.setUsedCount(1);
			}
		}
		found.prepareForUpdateIntoDB();
		return this.voucherDAO.update(found);
	}

	/**
	 * @param code
	 *            The voucher code
	 * @return The Voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Voucher revertVoucher(final String code) throws EpickurException {
		Voucher found = this.readAndThrowException(code);
		if (found.getExpirationType() == ExpirationType.ONETIME) {
			found.setStatus(Status.VALID);
		} else if (found.getExpirationType() == ExpirationType.UNTIL) {
			found.setUsedCount(found.getUsedCount() - 1);
		}
		found.prepareForUpdateIntoDB();
		return this.voucherDAO.update(found);
	}

	protected Voucher readAndThrowException(final String code) throws EpickurException {
		Voucher found = this.voucherDAO.read(code);
		if (found == null) {
			throw new EpickurException("Voucher '" + code + "' not found");
		}
		return found;
	}
}
