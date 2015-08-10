package com.epickur.api.dump;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.utils.Utils;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class MongoDBDump {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(MongoDBDump.class.getSimpleName());
	/** File separator */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/** Ip */
	private String ip;
	/** Port */
	private String port;
	/** Database */
	private String database;
	/** Database user */
	private String username;
	/** Database password */
	private String password;
	/** Backup path */
	private String backupPath;
	/** Compression of the file */
	private static final String TARGZEXT = ".tar.gz";
	/** Date */
	private String date;

	/**
	 * Construct a DB dump
	 * 
	 * @param date
	 *            The date
	 */
	public MongoDBDump(final String date) {
		this.date = date;
		Properties prop = Utils.getEpickurProperties();
		this.ip = prop.getProperty("mongo.address");
		this.port = prop.getProperty("mongo.port");
		this.database = prop.getProperty("mongo.db.name");
		this.username = prop.getProperty("mongo.user.login");
		this.password = prop.getProperty("mongo.user.password");
		this.backupPath = prop.getProperty("mongo.backup.path");
		createFolderIfNotExists();
	}

	/**
	 * 
	 */
	private void createFolderIfNotExists() {
		File folder = new File(this.backupPath);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	/**
	 * Get current file name
	 * 
	 * @return the file name
	 */
	public String getCurrentNameFile() {
		String computername = null;
		try {
			computername = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOG.warn("Host not found: " + e.getLocalizedMessage());
			computername = "unknown";
		}
		return "epickur_" + computername + "_" + date + TARGZEXT;
	}

	/**
	 * @return The current full path name
	 */
	public String getCurrentFullPathName() {
		return backupPath + FILE_SEPARATOR + getCurrentNameFile();
	}

	/**
	 * @return The list files
	 */
	public List<String> getListFiles() {
		List<String> files = new ArrayList<String>();
		File folder = new File(backupPath + FILE_SEPARATOR + database);

		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files.add(listOfFiles[i].getAbsolutePath());
			}
		}
		return files;
	}

	/**
	 * Get the dump database
	 * 
	 * @return The backup directory
	 */
	public boolean exportMongo() {
		boolean success = true;
		InputStream in = null;
		try {
			StringBuilder dumpCommand = new StringBuilder();
			dumpCommand.append("mongodump -d" + database + " -h " + ip + ":" + port);
			if (StringUtils.isNotBlank(username)) {
				dumpCommand.append(" -u " + username + " -p" + password);
			}
			dumpCommand.append(" -o " + backupPath);
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(dumpCommand.toString());
			process.waitFor();
			String output = IOUtils.toString(process.getInputStream());
			String errorOutput = IOUtils.toString(process.getErrorStream());
			if (StringUtils.isNotBlank(output)) {
				LOG.info("\n" + output);
			}
			if (StringUtils.isNotBlank(errorOutput)) {
				LOG.info("\n" + errorOutput);
			}
		} catch (Exception e) {
			LOG.error("Error while trying to mongodump: " + e.getLocalizedMessage(), e);
			success = false;
		} finally {
			IOUtils.closeQuietly(in);
		}
		return success;
	}

	public void cleanDumpDirectory() {
		File folder = new File(backupPath + FILE_SEPARATOR + database);
		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			LOG.error("Error while trying to delete dump directory: " + e.getLocalizedMessage(), e);
		}
	}

	public void deleteDumpFile() {
		File file = new File(getCurrentFullPathName());
		boolean deleted = file.delete();
		if (!deleted) {
			LOG.error("Could not delete dump file");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		LOG.info("Starting Mongo dump");
		MongoDBDump m = new MongoDBDump(Utils.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
		m.exportMongo();
		LOG.info("DB dump done");
		LOG.info("Creating tar.gz...");
		List<String> list = m.getListFiles();
		Utils.createTarGz(list, m.getCurrentFullPathName());
		LOG.info("tar.gz generated: " + m.getCurrentFullPathName());
		m.cleanDumpDirectory();
	}
}
