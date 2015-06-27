package com.epickur.api.dump;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.utils.Utils;
import com.wordnik.system.mongodb.RestoreUtil;
import com.wordnik.system.mongodb.SnapshotUtil;

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
			LOG.error("Host not found", e);
			computername = "unknown";
		}
		return "epickur_" + computername + "_" + date + TARGZEXT;
	}

	public String getCurrentFullPathName() {
		return backupPath + FILE_SEPARATOR + getCurrentNameFile();
	}

	public final List<String> getListFiles() {
		List<String> files = new ArrayList<String>();
		File folder = new File(backupPath);

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
	 * @throws Exception
	 */
	public String exportMongo() {
		cleanDirectory();
		List<String> cmd = new ArrayList<String>();
		cmd.add("-d");
		cmd.add(database);
		cmd.add("-o");
		cmd.add(backupPath);
		if (!StringUtils.isBlank(username)) {
			cmd.add("-u");
			cmd.add(username);
		}
		if (!StringUtils.isBlank(password)) {
			cmd.add("-p");
			cmd.add(password);
		}
		if (!StringUtils.isBlank(ip)) {
			String address = ip;
			if (!StringUtils.isBlank(port)) {
				address += ":" + port;
			}
			cmd.add("-h");
			cmd.add(address);
		}
		SnapshotUtil.main(Utils.convertListToArray(cmd));
		return backupPath;
	}

	public void importMongo() {
		String[] derp2 = new String[7];
		derp2[0] = "-i";
		derp2[1] = "C:/tmp";
		derp2[2] = "-h";
		derp2[3] = "localhost";
		derp2[4] = "-d";
		derp2[5] = "epickur";
		derp2[6] = "-D";
		RestoreUtil.main(derp2);
	}

	private void cleanDirectory() {
		List<String> files = getListFiles();
		if (files.size() != 0) {
			File folder = new File(backupPath);
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				listOfFiles[i].delete();
			}
		}
	}
}
