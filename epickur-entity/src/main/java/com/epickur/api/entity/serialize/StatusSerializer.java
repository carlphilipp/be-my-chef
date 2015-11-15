package com.epickur.api.entity.serialize;

import java.io.IOException;

import com.epickur.api.enumeration.voucher.Status;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author cph
 * @version 1.0
 */
public final class StatusSerializer extends JsonSerializer<Status> {

	@Override
	public void serialize(final Status status, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (status == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(status.toString().toLowerCase());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
