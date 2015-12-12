package com.epickur.api.dao.mongo;

import com.epickur.api.exception.EpickurDBException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * Sequence DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Repository
public class SequenceDAO {

	/**
	 * Logger
	 */
	private static final Logger LOG = LogManager.getLogger(SequenceDAO.class.getSimpleName());
	@Autowired
	private MongoDatabase db;
	/**
	 * Database collection
	 */
	private MongoCollection<Document> coll;

	/**
	 * Post construct
	 */
	@PostConstruct
	public void postConstruct() {
		this.coll = db.getCollection("seq");
	}

	/**
	 * Get next id
	 *
	 * @return The next order id
	 * @throws EpickurDBException If an epickur exception occurred
	 */
	public String getNextId() throws EpickurDBException {
		String sequenceId = "order";
		String sequenceField = "seq";

		Document query = new Document();
		query.append("_id", sequenceId);

		Document change = new Document(sequenceField, new BsonInt32(1));
		Document update = new Document("$inc", change);
		try {
			Document res = coll.findOneAndUpdate(query, update);
			LOG.debug("Read next Order id");
			if (res != null) {
				return Integer.toHexString(Integer.parseInt(res.get(sequenceField).toString()));
			} else {
				// Put 1 as new seq and return 0 as current sequence
				query.append(sequenceField, 1);
				this.coll.insertOne(query);
				return Integer.toHexString(0);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("getNextId", e.getMessage(), sequenceId, e);
		}
	}
}
