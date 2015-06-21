package com.epickur.api.entity.databind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public final class PhoneNumberSerializer extends JsonSerializer<PhoneNumber> {

	@Override
	public void serialize(final PhoneNumber value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException,
			JsonProcessingException {
		try {
			if (value == null) {
				gen.writeNull();
			} else {
				gen.writeStartObject();
				gen.writeNumberField("nationalNumber", value.getNationalNumber());
				gen.writeNumberField("countryCode", value.getCountryCode());
				gen.writeEndObject();
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
