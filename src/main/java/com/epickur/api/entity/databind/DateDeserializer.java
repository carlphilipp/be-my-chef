package com.epickur.api.entity.databind;

import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * DateDeserializer
 * 
 * @author cph
 * @version 1.0
 */
public final class DateDeserializer extends JsonDeserializer<DateTime> {

	@Override
	public DateTime deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		JsonNode date = null;
		try {
			date = jp.readValueAsTree();
			if (date.isTextual()) {
				return new DateTime(NumberUtils.toLong(date.asText()));
			} else {
				return new DateTime(date.asLong());
			}
		} catch (Exception e) {
			throw new IOException();
		}
	}
}
