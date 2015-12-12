package com.epickur.api.cron;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Cron clean keys job
 *
 * @author cph
 * @version 1.0
 */
public final class CleanKeysJob {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(CleanKeysJob.class.getSimpleName());
	/**
	 * Key dao
	 */
	@Autowired
	private KeyDAO keyDao;
	@Autowired
	private Utils utils;

	// TODO load properties for cron value
	@Scheduled(cron = "0 0/5 * * * ?")
	public void execute() {
		LOG.info("Clean keys job starting...");
		try {
			final List<Key> keys = keyDao.readAll();
			for (final Key key : keys) {
				if (!utils.isValid(key)) {
					keyDao.delete(key.getId().toHexString());
				}
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage() + e.getMessage(), e);
		}
	}
}
