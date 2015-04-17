package com.epickur.api.entity.databind;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * DateSerializer
 * 
 * @author cph
 * @version 1.0
 */
public final class DateSerializer extends JsonSerializer<DateTime> {

	@Override
	public void serialize(final DateTime date, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			jgen.writeNumber(date.getMillis());
		} catch (Exception e) {
			throw new IOException();
		}
	}

}
