package com.epickur.api.entity.serialize;

import java.io.IOException;

import com.epickur.api.enumeration.OrderStatus;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize a order status
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class OrderStatusSerializer extends JsonSerializer<OrderStatus> {

	@Override
	public void serialize(final OrderStatus status, final JsonGenerator jgen, final SerializerProvider serializers) throws IOException {
		try {
			if (status == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(status.toString());
			}
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
