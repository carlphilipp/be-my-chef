package com.epickur.api.dao;

import java.util.Properties;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.epickur.api.dao.mongo.codec.CoordinatesCodec;
import com.epickur.api.dao.mongo.codec.DishTypeCodec;
import com.epickur.api.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB Singleton
 * 
 * @author cph
 * @version 1.0
 */
public final class MongoDb {

	/** Database **/
	private static MongoDatabase db;
	/** Mongo Client **/
	private static MongoClient mongoClient;
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
	public static MongoDatabase getInstance() {
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
		Properties prop = Utils.getEpickurProperties();
		String address = prop.getProperty("mongo.address");
		int port = Integer.parseInt(prop.getProperty("mongo.port"));
		String dbName = prop.getProperty("mongo.db.name");

		CoordinatesCodec coordinatesCodec = new CoordinatesCodec();
		DishTypeCodec dishTypeCodec = new DishTypeCodec();

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(coordinatesCodec),
				CodecRegistries.fromCodecs(dishTypeCodec)
				);

		MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

		mongoClient = new MongoClient(new ServerAddress(address, port), options);
		db = mongoClient.getDatabase(dbName);
	}

	@Override
	protected void finalize() throws Throwable {
		mongoClient.close();
	}
}