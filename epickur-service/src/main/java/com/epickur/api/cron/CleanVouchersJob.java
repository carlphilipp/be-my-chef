package com.epickur.api.cron;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Cron clean vouchers jobs.
 *
 * @author cph
 * @version 1.0
 */
@Component
public final class CleanVouchersJob {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(CleanVouchersJob.class.getSimpleName());
	/**
	 * Voucher business
	 */
	@Autowired
	private VoucherDAO voucherDAO;

	@Scheduled(cron = "0 0 12 * * ?")
	public void execute() {
		LOG.info("Clean vouchers job starting...");
		try {
			final List<Voucher> vouchers = voucherDAO.readToClean();
			for (final Voucher voucher : vouchers) {
				LOG.info("Expire voucher " + voucher.getCode() + " " + voucher.getExpiration());
				voucher.setStatus(Status.EXPIRED);
				voucher.prepareForUpdateIntoDB();
				this.voucherDAO.update(voucher);
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage() + e.getMessage(), e);
		}
	}
}
