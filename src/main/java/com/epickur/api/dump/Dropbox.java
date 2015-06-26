package com.epickur.api.dump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import com.epickur.api.utils.Utils;

public class Dropbox {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Dropbox.class.getSimpleName());

	private List<DbxClient> clients;

	public Dropbox() {
		init();
	}

	private void init() {
		Properties prop = Utils.getEpickurProperties();
		String tokensProperty = prop.getProperty("dropbox.tokens");
		String[] tokens = tokensProperty.split(",");
		this.clients = new ArrayList<DbxClient>();
		DbxRequestConfig config = new DbxRequestConfig("Epickur", Locale.getDefault().toString());
		for (int i = 0; i < tokens.length; i++) {
			DbxClient client = new DbxClient(config, tokens[i]);
			this.clients.add(client);
		}
	}

	public void deleteOldFile() throws DbxException, IOException {
		LOG.info("Deleting old file in dropbox...");
		for (DbxClient client : this.clients) {
			DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
			if (listing.children.size() > 9) {
				DbxEntry.File entry = null;
				DateTime dateTimeEntry = null;
				for (DbxEntry child : listing.children) {
					if (child.isFile()) {
						DbxEntry.File fileEntry = (DbxEntry.File) child;
						DateTime dateTime = new DateTime(fileEntry.clientMtime.getTime());
						if (dateTimeEntry == null || dateTime.isBefore(dateTimeEntry)) {
							entry = fileEntry;
							dateTimeEntry = new DateTime(fileEntry.clientMtime.getTime());
						}
						LOG.info("	" + fileEntry.name + ": " + fileEntry.clientMtime);
					}
				}
				LOG.info("Deleting: " + entry.name + ": " + entry.clientMtime);
				client.delete(entry.path);
			}
		}
		LOG.info("Delete done");
	}

	public void uploadFile(final String filePath) throws IOException, DbxException {
		LOG.info("Uploading file on dropbox...");
		int i = 0;
		for (DbxClient client : this.clients) {
			File inputFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(inputFile);
			try {
				client.uploadFile("/" + inputFile.getName(), DbxWriteMode.add(), inputFile.length(), inputStream);
				LOG.info("Dropbox " + i++ + " uploaded");
			} finally {
				inputStream.close();
			}
		}
		LOG.info("Upload done");
	}
}
