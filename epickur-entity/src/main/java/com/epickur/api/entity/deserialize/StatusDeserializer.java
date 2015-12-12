package com.epickur.api.entity.deserialize;

import java.io.IOException;

import com.epickur.api.enumeration.voucher.Status;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class StatusDeserializer extends JsonDeserializer<Status> {

	@Override
	public Status deserialize(final JsonParser jp, final DeserializationContext context) throws IOException {
		try {
			final JsonNode objId = jp.readValueAsTree();
			return Status.valueOf(objId.asText().toUpperCase());
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
