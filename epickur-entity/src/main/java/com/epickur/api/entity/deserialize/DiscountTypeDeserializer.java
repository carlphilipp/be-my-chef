package com.epickur.api.entity.deserialize;

import java.io.IOException;

import com.epickur.api.enumeration.voucher.DiscountType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class DiscountTypeDeserializer extends JsonDeserializer<DiscountType> {

	@Override
	public DiscountType deserialize(final JsonParser jp, final DeserializationContext context) throws IOException {
		try {
			JsonNode objId = jp.readValueAsTree();
			return DiscountType.valueOf(objId.asText().toUpperCase());
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
