package com.epickur.api.dao.mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonInt32;
import org.bson.Document;

import com.epickur.api.dao.MongoDb;
import com.epickur.api.exception.EpickurDBException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class SequenceDaoImpl {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(SequenceDaoImpl.class.getSimpleName());
	/** Database collection */
	private MongoCollection<Document> coll;

	/** Construct a Order sequence DAO */
	public SequenceDaoImpl() {
		init();
	}

	/**
	 * Init function
	 */
	private void init() {
		MongoDatabase db = MongoDb.getInstance();
		this.coll = db.getCollection("seq");
	}

	/**
	 * Get next id
	 * 
	 * @return The next order id
	 * @throws EpickurDBException
	 *             If an epickur exception occurred
	 */
	public String getNextId() throws EpickurDBException {
		String sequenceId = "order";
		String sequenceField = "seq";

		Document query = new Document();
		query.append("_id", sequenceId);

		Document change = new Document(sequenceField, new BsonInt32(1));
		Document update = new Document("$inc", change);
		try {
			Document res = (Document) this.coll.findOneAndUpdate(query, update);
			LOG.debug("Read next Order id");
			if (res != null) {
				return Integer.toHexString(Integer.valueOf(res.get(sequenceField).toString()));
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
