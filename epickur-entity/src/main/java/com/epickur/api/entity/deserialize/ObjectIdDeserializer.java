package com.epickur.api.entity.deserialize;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.epickur.api.exception.EpickurIOException;
import com.epickur.api.exception.EpickurIllegalArgument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * ObjectIdDeserializer
 * 
 * @author cph
 * @version 1.0
 */
public final class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

	@Override
	public ObjectId deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		JsonNode objId = null;
		try {
			objId = jp.readValueAsTree();
			if (objId.size() == 1) {
				JsonNode node = objId.get("$oid");
				return new ObjectId(node.asText());
			} else {
				return new ObjectId(objId.asText());
			}
		} catch (IllegalArgumentException e) {
			if (objId != null && objId.isTextual()) {
				throw new EpickurIOException("The id " + objId.asText() + " is illegal");
			} else {
				throw new EpickurIllegalArgument();
			}
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
