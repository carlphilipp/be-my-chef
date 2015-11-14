package com.epickur.api.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.business.VoucherBusiness;
import com.epickur.api.exception.EpickurException;

/**
 * Cron clean vouchers jobs.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class CleanVouchersJob implements Job {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(CleanVouchersJob.class.getSimpleName());
	/** Voucher business */
	private VoucherBusiness voucherBusiness;

	/** Constructor */
	public CleanVouchersJob() {
		voucherBusiness = new VoucherBusiness();
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("Clean vouchers job starting...");
		try {
			this.voucherBusiness.clean();
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage() + e.getMessage(), e);
		}
	}
}
