package com.epickur.api.entity.databind;

import java.io.IOException;

import com.epickur.api.enumeration.Role;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author cph
 * @version 1.0
 */
public final class RoleSerializer extends JsonSerializer<Role> {

	@Override
	public void serialize(final Role role, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (role == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(role.toString().toLowerCase());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
