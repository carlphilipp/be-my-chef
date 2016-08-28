package com.epickur.api.dump;

import com.epickur.api.config.EpickurProperties;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a MongoDB dump.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
public final class MongoDBDump {

	/**
	 * File separator
	 */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	/**
	 * Compression of the file
	 */
	private static final String TARGZEXT = ".tar.gz";

	@Autowired
	private EpickurProperties properties;
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
	 * Date
	 */
	private final String date;
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
		String computerName;
		try {
			computerName = InetAddress.getLocalHost().getHostName();
		} catch (final UnknownHostException e) {
			log.warn("Host not found: {}", e.getLocalizedMessage());
			computerName = "unknown";
		}
		return "epickur_" + computerName + "_" + date + TARGZEXT;
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
		final File[] listOfFiles = dumpDirectory.listFiles();
		if (listOfFiles != null) {
			return Arrays.stream(listOfFiles)
				.filter(File::isFile)
				.map(File::getAbsolutePath)
				.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
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
		} catch (final Exception e) {
			log.error("Error while trying to mongodump: {}", e.getLocalizedMessage(), e);
		}
		return success;
	}

	protected String buildDumpCommand() {
		final StringBuilder dumpCommand = new StringBuilder();
		dumpCommand
			.append(properties.getMongodPath())
			.append(" -d ")
			.append(properties.getMongoDbName())
			.append(" -h ")
			.append(properties.getMongoAddress())
			.append(":")
			.append(properties.getMongoPort());
		if (StringUtils.isNotBlank(properties.getMongoLogin())) {
			dumpCommand
				.append(" -u ")
				.append(properties.getMongoLogin())
				.append(" -p")
				.append(properties.getMongoPassword());
		}
		dumpCommand
			.append(" -o ")
			.append(properties.getMongoBackupPath());
		return dumpCommand.toString();
	}

	protected void logExportResult(final Process process) throws IOException {
		final String output = IOUtils.toString(process.getInputStream(), Charset.forName("UTF-8"));
		final String errorOutput = IOUtils.toString(process.getErrorStream(), Charset.forName("UTF-8"));
		if (StringUtils.isNotBlank(output)) {
			log.info("\n" + output);
		}
		if (StringUtils.isNotBlank(errorOutput)) {
			log.info("\n" + errorOutput);
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
			log.error("Error while trying to delete dump directory: {}", e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Delete the dump file
	 */
	public void deleteDumpFile() {
		boolean deleted = dumpFile.delete();
		if (!deleted) {
			log.error("Could not delete dump file");
		}
	}
}
