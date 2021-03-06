package com.epickur.api.entity.deserialize;

import java.io.IOException;

import com.epickur.api.enumeration.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author cph
 * @version 1.0
 */
public final class RoleDeserializer extends JsonDeserializer<Role> {

	@Override
	public Role deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
		try {
			final JsonNode objId = jp.readValueAsTree();
			return Role.getEnum(objId.asText());
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}
}
