package com.epickur.api.entity.deserialize;

import java.io.IOException;

import com.epickur.api.enumeration.OrderStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Deserialize a order status
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class OrderStatusDeserializer extends JsonDeserializer<OrderStatus> {

	@Override
	public OrderStatus deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		try {
			final JsonNode objId = jp.readValueAsTree();
			return OrderStatus.fromString(objId.asText());
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
