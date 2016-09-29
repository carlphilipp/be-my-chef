package com.epickur.api.config;

import com.epickur.api.dao.mongo.codec.CoordinatesCodec;
import com.epickur.api.dao.mongo.codec.DishTypeCodec;
import com.epickur.api.dao.mongo.codec.ExpirationTypeCodec;
import com.epickur.api.dao.mongo.codec.StatusCodec;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

	@Autowired
	public EpickurProperties properties;

	@Bean
	public MongoDatabase database() {
		final CoordinatesCodec coordinatesCodec = new CoordinatesCodec();
		final DishTypeCodec dishTypeCodec = new DishTypeCodec();
		final ExpirationTypeCodec expirationTypeCodec = new ExpirationTypeCodec();
		final StatusCodec statusCodec = new StatusCodec();

		final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromCodecs(coordinatesCodec),
				CodecRegistries.fromCodecs(dishTypeCodec),
				CodecRegistries.fromCodecs(expirationTypeCodec),
				CodecRegistries.fromCodecs(statusCodec)
		);

		final MongoClientOptions options = MongoClientOptions.builder().serverSelectionTimeout(8000).codecRegistry(codecRegistry).build();
		final MongoClient mongoClient;
		if (!StringUtils.isBlank(properties.getMongoLogin()) && !StringUtils.isBlank(properties.getMongoPassword())) {
			final MongoCredential credential = MongoCredential
					.createCredential(properties.getMongoLogin(), properties.getMongoDbName(), properties.getMongoPassword().toCharArray());
			final List<MongoCredential> credentials = new ArrayList<>();
			credentials.add(credential);
			mongoClient = new MongoClient(new ServerAddress(properties.getMongoAddress(), properties.getMongoPort()), credentials, options);
		} else {
			mongoClient = new MongoClient(new ServerAddress(properties.getMongoAddress(), properties.getMongoPort()), options);
		}
		return mongoClient.getDatabase(properties.getMongoDbName());
	}
}
