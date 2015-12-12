package com.epickur.api.entity.serialize;

import java.io.IOException;

import com.epickur.api.enumeration.voucher.DiscountType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author cph
 * @version 1.0
 */
public final class DiscountTypeSerializer extends JsonSerializer<DiscountType> {

	@Override
	public void serialize(final DiscountType discountType, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (discountType == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(discountType.toString().toLowerCase());
			}
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
