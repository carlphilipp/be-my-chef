package com.epickur.api.entity.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class PhoneNumberSerializer extends JsonSerializer<PhoneNumber> {

	@Override
	public void serialize(final PhoneNumber value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
		try {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeStartObject();
				gen.writeNumberField("nationalNumber", value.getNationalNumber());
				gen.writeNumberField("countryCode", value.getCountryCode());
				gen.writeEndObject();
			}
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
