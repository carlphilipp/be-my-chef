package com.epickur.api.dump;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a MongoDB dump.
 *
 * @author cph
 * @version 1.0
 */
public final class MongoDBDump {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(MongoDBDump.class.getSimpleName());
	@Autowired
	private Utils utils;
	@Autowired
	private EpickurProperties properties;
	/**
	 * File separator
	 */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/**
	 * Backup File
	 */
	private File backupFolder;
	/**
	 * Dump File
	 */
	private File dumpFile;
	/**
	 * Dump Directory
	 */
	private File dumpDirectory;
	/**
	 * Compression of the file
	 */
	private static final String TARGZEXT = ".tar.gz";
	/**
	 * Date
	 */
	private String date;
	/**
	 * Run time object for execute mongo
	 */
	private Runtime rt;

	/**
	 * Construct a DB dump
	 *
	 * @param date The date
	 */
	public MongoDBDump(final String date) {
		this.date = date;
	}

	@PostConstruct
	public void postConstruct() {
		this.backupFolder = new File(properties.getMongoBackupPath());
		this.dumpFile = new File(getCurrentFullPathName());
		this.dumpDirectory = new File(properties.getMongoBackupPath() + FILE_SEPARATOR + properties.getMongoDbName());
		this.rt = Runtime.getRuntime();
	}

	public void setBackupFolder(final File backupFolder) {
		this.backupFolder = backupFolder;
	}

	public void setDumpFile(final File dumpFile) {
		this.dumpFile = dumpFile;
	}

	public void setRuntime(final Runtime runtime) {
		this.rt = runtime;
	}

	public void setDumpDirectory(final File dumpDirectory) {
		this.dumpDirectory = dumpDirectory;
	}

	/**
	 * Get current file name
	 *
	 * @return the file name
	 */
	public String getCurrentNameFile() {
		String computername;
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
		return properties.getMongoBackupPath() + FILE_SEPARATOR + getCurrentNameFile();
	}

	/**
	 * @return The list files
	 */
	public List<String> getListFiles() {
		List<String> files = new ArrayList<>();
		File[] listOfFiles = dumpDirectory.listFiles();
		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				if (file.isFile()) {
					files.add(file.getAbsolutePath());
				}
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
			final Process process = rt.exec(buildDumpCommand());
			process.waitFor();
			logExportResult(process);
			success = true;
		} catch (Exception e) {
			LOG.error("Error while trying to mongodump: " + e.getLocalizedMessage(), e);
		}
		return success;
	}

	protected String buildDumpCommand() {
		final StringBuilder dumpCommand = new StringBuilder();
		dumpCommand
				.append(properties.getMongodPath() + " -d " + properties.getMongoDbName() + " -h " + properties.getMongoAddress() + ":" + properties
						.getMongoPort());
		if (StringUtils.isNotBlank(properties.getMongoLogin())) {
			dumpCommand.append(" -u " + properties.getMongoLogin() + " -p" + properties.getMongoPassword());
		}
		dumpCommand.append(" -o " + properties.getMongoBackupPath());
		return dumpCommand.toString();
	}

	protected void logExportResult(final Process process) throws IOException {
		final String output = IOUtils.toString(process.getInputStream());
		final String errorOutput = IOUtils.toString(process.getErrorStream());
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
