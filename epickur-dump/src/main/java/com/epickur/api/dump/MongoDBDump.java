package com.epickur.api.dump;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.utils.Utils;

/**
 * Create a MongoDB dump.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class MongoDBDump {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(MongoDBDump.class.getSimpleName());
	/** File separator */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/** Mongod */
	private String mongod;
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
	/** Backup File */
	private File backupFolder;
	/** Dump File */
	private File dumpFile;
	/** Dump Directory */
	private File dumpDirectory;
	/** Compression of the file */
	private static final String TARGZEXT = ".tar.gz";
	/** Date */
	private String date;
	/** Run time object for execute mongo */
	private Runtime rt;

	/**
	 * Construct a DB dump
	 * 
	 * @param date
	 *            The date
	 */
	public MongoDBDump(final String date) {
		this.date = date;
		Properties prop = Utils.getEpickurProperties();
		this.mongod = prop.getProperty("mongod.path");
		this.ip = prop.getProperty("mongo.address");
		this.port = prop.getProperty("mongo.port");
		this.database = prop.getProperty("mongo.db.name");
		this.username = prop.getProperty("mongo.user.login");
		this.password = prop.getProperty("mongo.user.password");
		this.backupPath = prop.getProperty("mongo.backup.path");
		this.backupFolder = new File(backupPath);
		this.dumpFile = new File(getCurrentFullPathName());
		this.dumpDirectory = new File(backupPath + FILE_SEPARATOR + database);
		this.rt = Runtime.getRuntime();
	}

	public MongoDBDump(final String date, final String mongod, final String ip, final String port, final String database, final String username,
			final String password, String backupPath, final Runtime rt) {
		this.date = date;
		this.mongod = mongod;
		this.ip = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.backupPath = backupPath;
		this.backupFolder = new File(backupPath);
		this.rt = rt;
	}
	
	public void setBackupFolder(final File backupFolder){
		this.backupFolder = backupFolder;
	}
	
	public void setDumpFile(final File dumpFile){
		this.dumpFile = dumpFile;
	}
	
	public void setDumpDirectory(final File dumpDirectory){
		this.dumpDirectory = dumpDirectory;
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
		List<String> files = new ArrayList<>();
		File[] listOfFiles = dumpDirectory.listFiles();
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
		createFolderIfNotExists(backupFolder);
		boolean success = false;
		try {
			Process process = rt.exec(buildDumpCommand());
			process.waitFor();
			logExportResult(process);
			success = true;
		} catch (Exception e) {
			LOG.error("Error while trying to mongodump: " + e.getLocalizedMessage(), e);
		}
		return success;
	}

	protected String buildDumpCommand() {
		StringBuilder dumpCommand = new StringBuilder();
		dumpCommand.append(mongod + " -d " + database + " -h " + ip + ":" + port);
		if (StringUtils.isNotBlank(username)) {
			dumpCommand.append(" -u " + username + " -p" + password);
		}
		dumpCommand.append(" -o " + backupPath);
		return dumpCommand.toString();
	}

	protected void logExportResult(final Process process) throws InterruptedException, IOException {
		String output = IOUtils.toString(process.getInputStream());
		String errorOutput = IOUtils.toString(process.getErrorStream());
		if (StringUtils.isNotBlank(output)) {
			LOG.info("\n" + output);
		}
		if (StringUtils.isNotBlank(errorOutput)) {
			LOG.info("\n" + errorOutput);
		}
	}

	/**
	 * 
	 */
	private void createFolderIfNotExists(final File folder) {
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	/**
	 * Clean dump directory
	 */
	public void cleanDumpDirectory() {
		try {
			FileUtils.deleteDirectory(dumpDirectory);
		} catch (IOException e) {
			LOG.error("Error while trying to delete dump directory: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Delete the dump file
	 */
	public void deleteDumpFile() {
		boolean deleted = dumpFile.delete();
		if (!deleted) {
			LOG.error("Could not delete dump file");
		}
	}
}
