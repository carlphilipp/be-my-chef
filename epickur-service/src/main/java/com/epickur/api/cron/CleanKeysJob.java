package com.epickur.api.cron;

import com.epickur.api.dao.mongo.KeyDAO;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Cron clean keys job
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
public final class CleanKeysJob {

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
		log.info("Clean keys job starting...");
		try {
			final List<Key> keys = keyDao.readAll();
			for (final Key key : keys) {
				if (!utils.isValid(key)) {
					keyDao.delete(key.getId().toHexString());
				}
			}
		} catch (final EpickurException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}
