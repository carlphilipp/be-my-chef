package com.epickur.api.dao.mongo;

import com.epickur.api.exception.EpickurDBException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

import static com.epickur.api.dao.CollectionsName.SEQUENCE_COLL;

/**
 * Sequence DAO access with CRUD operations.
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@Repository
public class SequenceDAO {

	@Autowired
	private MongoDatabase db;

	private MongoCollection<Document> coll;

	/**
	 * Post construct
	 */
	@PostConstruct
	public void postConstruct() {
		this.coll = db.getCollection(SEQUENCE_COLL);
	}

	/**
	 * Get next id
	 *
	 * @return The next order id
	 * @throws EpickurDBException If an epickur exception occurred
	 */
	public String getNextId() throws EpickurDBException {
		final String sequenceId = "order";
		final String sequenceField = "seq";

		final Document query = new Document();
		query.append("_id", sequenceId);

		final Document change = new Document(sequenceField, new BsonInt32(1));
		final Document update = new Document("$inc", change);
		try {
			final Document res = coll.findOneAndUpdate(query, update);
			log.debug("Read next Order id");
			if (res != null) {
				return Integer.toHexString(Integer.parseInt(res.get(sequenceField).toString()));
			} else {
				// Put 1 as new seq and return 0 as current sequence
				query.append(sequenceField, 1);
				coll.insertOne(query);
				return Integer.toHexString(0);
			}
		} catch (MongoException e) {
			throw new EpickurDBException("getNextId", e.getMessage(), sequenceId, e);
		}
	}
}
