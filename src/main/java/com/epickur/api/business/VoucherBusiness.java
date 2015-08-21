package com.epickur.api.business;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.dao.mongo.VoucherDAOImpl;
import com.epickur.api.entity.Voucher;
import com.epickur.api.exception.EpickurException;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class VoucherBusiness {
	/** Logger */
	private static final Logger LOG = LogManager.getLogger(VoucherBusiness.class.getSimpleName());
	/** User dao */
	private VoucherDAOImpl voucherDAO;

	public VoucherBusiness() {
		this.voucherDAO = new VoucherDAOImpl();
	}

	/**
	 * @param code
	 * @return
	 * @throws EpickurException
	 */
	public Voucher read(final String code) throws EpickurException {
		return voucherDAO.read(code);
	}
	
	public List<Voucher> create(){
		return null;
	}
}
