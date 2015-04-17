package com.epickur.api.cron;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dao.mongo.KeyDaoImpl;
import com.epickur.api.entity.Key;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.utils.Utils;

/**
 * Cron clean keys job
 * 
 * @author cph
 * @version 1.0
 */
public final class CleanKeysJob implements Job {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(CleanKeysJob.class.getSimpleName());
	/** Key dao **/
	private KeyDaoImpl keyDao;

	/** Constructor **/
	public CleanKeysJob() {
		this.keyDao = new KeyDaoImpl();
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("Cleaning job");
		try {
			List<Key> keys = keyDao.readAll();
			for (Key key : keys) {
				if (!Utils.isValid(key)) {
					keyDao.delete(key.getId().toHexString());
				}
			}
		} catch (EpickurException e) {
			LOG.error(e.getLocalizedMessage() + e.getMessage(), e);
		}
	}
}
