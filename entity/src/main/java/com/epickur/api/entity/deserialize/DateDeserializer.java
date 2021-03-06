package com.epickur.api.entity.deserialize;

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
		final JsonNode date;
		try {
			date = jp.readValueAsTree();
			if (date.size() == 1) {
				final JsonNode node = date.get("$numberLong");
				return getDateTime(node);
			} else {
				return getDateTime(date);
			}
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Get a DateTime from a JsonNode
	 * 
	 * @param node
	 *            The JsonNode to convert
	 * @return A DateTime
	 */
	private DateTime getDateTime(final JsonNode node) {
		if (node.isTextual()) {
			return new DateTime(NumberUtils.toLong(node.asText()));
		} else {
			return new DateTime(node.asLong());
		}
	}
}
