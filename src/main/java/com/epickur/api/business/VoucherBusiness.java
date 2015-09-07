package com.epickur.api.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.epickur.api.dao.mongo.VoucherDAOImpl;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;

/**
 * Voucher business layer. Access voucher DAO layer and executes logic.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class VoucherBusiness {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(VoucherBusiness.class.getSimpleName());
	/** User dao */
	private VoucherDAOImpl voucherDAO;

	/**
	 * The constructor
	 */
	public VoucherBusiness() {
		this.voucherDAO = new VoucherDAOImpl();
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
	 * Generae vouchers.
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
			voucher.setCode(generateRandomCode());
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
					voucherDAO.create(voucher);
				}
			}
		} while (result.size() < count);
		return result;
	}

	/**
	 * @throws EpickurException If an EpickurException occurred
	 */
	public void clean() throws EpickurException {
		List<Voucher> vouchers = this.voucherDAO.readToClean();
		for (Voucher voucher : vouchers) {
			LOG.info("Expire voucher " + voucher.getCode() + " " + voucher.getExpiration());
			voucher.setStatus(Status.EXPIRED);
			this.voucherDAO.update(voucher);
		}
	}

	/**
	 * @param code
	 *            The code
	 * @return The Voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Voucher validateVoucher(final String code) throws EpickurException {
		Voucher found = this.voucherDAO.read(code);
		if (found == null) {
			throw new EpickurException("Voucher '" + code + "' not found");
		}
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
		Voucher updated = this.voucherDAO.update(found);
		return updated;
	}

	/**
	 * @param code
	 *            The voucher code
	 * @return The Voucher
	 * @throws EpickurException
	 *             If an EpickurException occurred
	 */
	public Voucher revertVoucher(final String code) throws EpickurException {
		Voucher found = this.voucherDAO.read(code);
		if (found == null) {
			throw new EpickurException("Voucher '" + code + "' not found");
		}
		if (found.getExpirationType() == ExpirationType.ONETIME) {
			found.setStatus(Status.VALID);
		} else if (found.getExpirationType() == ExpirationType.UNTIL) {
			found.setUsedCount(found.getUsedCount() - 1);
		}
		Voucher updated = this.voucherDAO.update(found);
		return updated;
	}

	/**
	 * @return A random voucher code.
	 */
	private String generateRandomCode() {
		StringBuilder stb = new StringBuilder();
		stb.append(getRandomConsonants(3));
		stb.append(getRandomNumber());
		stb.append(getRandomConsonants(3));
		stb.append(getRandomNumber());
		return stb.toString();
	}

	/**
	 * @return A random number between 2 and 9
	 */
	private int getRandomNumber() {
		RandomDataGenerator randomData = new RandomDataGenerator();
		// Removed 0 and 1
		return randomData.nextInt(2, 9);
	}

	/**
	 * @param size
	 *            Max size
	 * @return A Random consonants string
	 */
	private String getRandomConsonants(final int size) {
		// Removed l
		String[] consonants = { "q", "w", "r", "t", "p", "s", "d", "f", "g", "h", "j", "k", "z", "x", "c", "v", "b", "n", "m" };
		StringBuilder res = new StringBuilder();
		RandomDataGenerator randomData = new RandomDataGenerator();
		for (int i = 0; i < size; i++) {
			res.append(consonants[randomData.nextInt(0, consonants.length - 1)]);
		}
		return res.toString().toUpperCase();
	}
}
