package com.epickur.api.cron;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.dump.AmazonWebServices;
import com.epickur.api.dump.MongoDBDump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Job that creat a dump of MongoDB and send it to Amazon servers.
 *
 * @author cph
 * @version 1.0
 */
public class MongoDBDumpJob {

	@Autowired
	private AmazonWebServices aws;
	@Autowired
	private MongoDBDump mongoDBDump;

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(MongoDBDumpJob.class.getSimpleName());

	@Scheduled(cron = "0 0 0/2 * * ?")
	public void execute() {
		LOG.info("Start DB dump...");
		final boolean exported = mongoDBDump.exportMongo();
		LOG.info("DB dump done");
		if (exported) {
			LOG.info("Creating tar.gz...");
			final List<String> list = mongoDBDump.getListFiles();
			CommonsUtil.createTarGz(list, mongoDBDump.getCurrentFullPathName());
			LOG.info("tar.gz generated: " + mongoDBDump.getCurrentFullPathName());

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
