package com.epickur.api.entity.databind;

import java.io.IOException;

import com.epickur.api.enumeration.DishType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Deserialize a Dish type.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class DishTypeDeserializer extends JsonDeserializer<DishType> {

	@Override
	public DishType deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		try {
			JsonNode objId = jp.readValueAsTree();
			return DishType.fromString(objId.asText());
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
