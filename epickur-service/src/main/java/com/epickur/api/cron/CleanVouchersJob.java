package com.epickur.api.cron;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Cron clean vouchers jobs.
 *
 * @author cph
 * @version 1.0
 */
public final class CleanVouchersJob implements Job {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(CleanVouchersJob.class.getSimpleName());
	/**
	 * Voucher business
	 */
	private VoucherDAO voucherDAO;

	/**
	 * Constructor
	 */
	public CleanVouchersJob() {
		voucherDAO = new VoucherDAO();
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("Clean vouchers job starting...");
		try {
			List<Voucher> vouchers = voucherDAO.readToClean();
			for (Voucher voucher : vouchers) {
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
