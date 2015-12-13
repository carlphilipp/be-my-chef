package com.epickur.api.cron;

import com.epickur.api.aws.AmazonWebServices;
import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.dump.MongoDBDump;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Job that creat a dump of MongoDB and send it to Amazon servers.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
public class MongoDBDumpJob {

	@Autowired
	private AmazonWebServices aws;
	@Autowired
	private MongoDBDump mongoDBDump;

	@Scheduled(cron = "0 0 0/2 * * ?")
	public void execute() {
		log.info("Start DB dump...");
		final boolean exported = mongoDBDump.exportMongo();
		log.info("DB dump done");
		if (exported) {
			log.info("Creating tar.gz...");
			final List<String> list = mongoDBDump.getListFiles();
			CommonsUtil.createTarGz(list, mongoDBDump.getCurrentFullPathName());
			log.info("tar.gz generated: " + mongoDBDump.getCurrentFullPathName());

			aws.deleteOldFile();
			aws.uploadFile(mongoDBDump.getCurrentFullPathName());

			// Clean after upload
			mongoDBDump.cleanDumpDirectory();
			mongoDBDump.deleteDumpFile();
		} else {
			log.info("DB dump failed...:(");
		}
	}
}
