package com.epickur.api.cron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MongoDBDumpJob implements Job {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(MongoDBDumpJob.class.getSimpleName());

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		/*MongoDBDump m = new MongoDBDump(Utils.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
		String path = m.exportMongo();
		List<String> list = m.getListFiles();
		Utils.createTarGz(list, path + "/" + m.getCurrentNameFile());*/
	}
	
	public static void main(String [] args) throws JobExecutionException{
		MongoDBDumpJob job = new MongoDBDumpJob();
		job.execute(null);
	}
}
