package com.epickur.api.entity.databind;

import java.io.IOException;

import com.epickur.api.enumeration.DishType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class DishTypeDeserializer extends JsonDeserializer<DishType> {

	@Override
	public DishType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		try {
			JsonNode objId = jp.readValueAsTree();
			return DishType.fromString(objId.asText());
		} catch (Exception e) {
			throw new IOException();
		}
	}
}
