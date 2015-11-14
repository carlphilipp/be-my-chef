package com.epickur.api.entity.serialize;

import java.io.IOException;

import com.epickur.api.enumeration.voucher.ExpirationType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author cph
 * @version 1.0
 */
public final class ExpirationTypeSerializer extends JsonSerializer<ExpirationType> {

	@Override
	public void serialize(final ExpirationType expirationType, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (expirationType == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(expirationType.toString().toLowerCase());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
