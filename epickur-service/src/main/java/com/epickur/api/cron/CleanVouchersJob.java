package com.epickur.api.cron;

import com.epickur.api.dao.mongo.VoucherDAO;
import com.epickur.api.entity.Voucher;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public final class CleanVouchersJob {

	/**
	 * Voucher business
	 */
	@Autowired
	private VoucherDAO voucherDAO;

	@Scheduled(cron = "0 0 12 * * ?")
	public void execute() {
		log.info("Clean vouchers job starting...");
		try {
			final List<Voucher> vouchers = voucherDAO.readToClean();
			for (final Voucher voucher : vouchers) {
				log.info("Expire voucher {} {}", voucher.getCode(), voucher.getExpiration());
				voucher.setStatus(Status.EXPIRED);
				voucher.prepareForUpdateIntoDB();
				this.voucherDAO.update(voucher);
			}
		} catch (final EpickurException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
