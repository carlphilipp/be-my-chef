package com.epickur.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.epickur.api.dao.mongo.codec.CoordinatesCodec;
import com.epickur.api.dao.mongo.codec.DishTypeCodec;
import com.epickur.api.dao.mongo.codec.ExpirationTypeCodec;
import com.epickur.api.dao.mongo.codec.StatusCodec;
import com.epickur.api.utils.Utils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB Singleton
 * 
 * @author cph
 * @version 1.0
 */
public final class MongoDb {

	/** Database */
	private static MongoDatabase db;
	/** Mongo Client */
	private static MongoClient mongoClient;
	/** Lock object */
	private static final Object LOCK = new Object();

	/** Constructor */
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

	/** Create instance */
	private static void createInstance() {
		Properties prop = Utils.getEpickurProperties();
		String address = prop.getProperty("mongo.address");
		int port = Integer.parseInt(prop.getProperty("mongo.port"));
		String dbName = prop.getProperty("mongo.db.name");
		String userName = prop.getProperty("mongo.user.login");
		String password = prop.getProperty("mongo.user.password");

		CoordinatesCodec coordinatesCodec = new CoordinatesCodec();
		DishTypeCodec dishTypeCodec = new DishTypeCodec();
		ExpirationTypeCodec expirationTypeCodec = new ExpirationTypeCodec();
		StatusCodec statusCodec = new StatusCodec();

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(coordinatesCodec),
				CodecRegistries.fromCodecs(dishTypeCodec),
				CodecRegistries.fromCodecs(expirationTypeCodec),
				CodecRegistries.fromCodecs(statusCodec)
				);

		MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();

		if (!StringUtils.isBlank(userName) && !StringUtils.isBlank(password)) {
			MongoCredential credential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
			List<MongoCredential> credentials = new ArrayList<MongoCredential>();
			credentials.add(credential);
			mongoClient = new MongoClient(new ServerAddress(address, port), credentials, options);
		} else {
			mongoClient = new MongoClient(new ServerAddress(address, port), options);
		}
		db = mongoClient.getDatabase(dbName);
	}

	@Override
	protected void finalize() throws Throwable {
		mongoClient.close();
	}
}