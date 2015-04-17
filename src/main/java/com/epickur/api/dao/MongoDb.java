package com.epickur.api.dao;

import java.net.UnknownHostException;
import java.util.Properties;

import com.epickur.api.utils.Utils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * MongoDB Singleton
 * 
 * @author cph
 * @version 1.0
 */
public final class MongoDb {

	/** Database **/
	private static DB db;
	/** Lock object **/
	private static final Object LOCK = new Object();

	/** Constructor **/
	private MongoDb() {
	}

	/**
	 * Get instance
	 * 
	 * @return The DB instance
	 */
	public static DB getInstance() {
		if (db == null) {
			synchronized (LOCK) {
				if (db == null) {
					createInstance();
				}
			}
		}
		return db;
	}

	/** Create instance **/
	private static void createInstance() {
		try {
			Properties prop = Utils.getEpickurProperties();
			String address = prop.getProperty("mongo.address");
			int port = Integer.parseInt(prop.getProperty("mongo.port"));
			String dbName = prop.getProperty("mongo.db.name");

			MongoClient mongoClient = new MongoClient(new ServerAddress(address, port));
			db = mongoClient.getDB(dbName);
		} catch (UnknownHostException e) {
			throw new RuntimeException("Error while MongoDB init: " + e);
		}
	}
}