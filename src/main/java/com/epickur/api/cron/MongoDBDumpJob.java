package com.epickur.api.cron;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dump.MongoDBDump;
import com.epickur.api.utils.Utils;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class MongoDBDumpJob implements Job {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(MongoDBDumpJob.class.getSimpleName());

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		LOG.info("Start DB dump...");
		MongoDBDump m = new MongoDBDump(Utils.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
		m.exportMongo();
		LOG.info("DB dump done");
		LOG.info("Creating tar.gz...");
		List<String> list = m.getListFiles();
		Utils.createTarGz(list, m.getCurrentFullPathName());
		LOG.info("tar.gz generated: " + m.getCurrentFullPathName());
		/*
		 * Dropbox dropbox = new Dropbox(); try { dropbox.deleteOldFile(); dropbox.uploadFile(m.getCurrentFullPathName()); } catch (IOException e) {
		 * LOG.error(e.getLocalizedMessage(), e); } catch (DbxException e) { LOG.error(e.getLocalizedMessage(), e); }
		 */
	}
	
/*	public static void main(String [] args) throws JobExecutionException{
		MongoDBDumpJob dump = new MongoDBDumpJob();
		dump.execute(null);
	}*/
}
