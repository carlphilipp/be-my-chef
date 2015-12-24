package com.epickur.api.entity.deserialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class PhoneNumberDeserializer extends JsonDeserializer<PhoneNumber> {

	@Override
	public PhoneNumber deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		try {
			final JsonNode node = jp.readValueAsTree();
			if (!node.has("nationalNumber")) {
				return null;
			} else {
				if (node.has("countryCode")) {
					PhoneNumber phoneNumber = new PhoneNumber();
					phoneNumber.setCountryCode(node.get("countryCode").asInt());
					phoneNumber.setNationalNumber(node.get("nationalNumber").asLong());
					return phoneNumber;
				} else {
					String nationalNumber = node.get("nationalNumber").asText();
					PhoneNumberUtil util = PhoneNumberUtil.getInstance();
					try {
						return util.parse(nationalNumber, null);
					} catch (NumberParseException e) {
						// Return dummy phone number because we do not want to handle illegal paramater here
						return new PhoneNumber();
					}
				}
			}
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
