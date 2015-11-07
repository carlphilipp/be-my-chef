package com.epickur.api.cron;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.epickur.api.dump.AmazonWebServices;
import com.epickur.api.dump.MongoDBDump;
import com.epickur.api.utils.Utils;

/**
 * Job that creat a dump of MongoDB and send it to Amazon servers.
 * 
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
		MongoDBDump mongoDBDump = new MongoDBDump(Utils.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
		boolean exported = mongoDBDump.exportMongo();
		LOG.info("DB dump done");
		if (exported) {
			LOG.info("Creating tar.gz...");
			List<String> list = mongoDBDump.getListFiles();
			Utils.createTarGz(list, mongoDBDump.getCurrentFullPathName());
			LOG.info("tar.gz generated: " + mongoDBDump.getCurrentFullPathName());

			AmazonWebServices aws = new AmazonWebServices();
			aws.deleteOldFile();
			aws.uploadFile(mongoDBDump.getCurrentFullPathName());

			// Clean after upload
			mongoDBDump.cleanDumpDirectory();
			mongoDBDump.deleteDumpFile();
		} else {
			LOG.info("DB dump failed...:(");
		}
	}
}
