package com.epickur.api.entity.databind;

import java.io.IOException;

import com.epickur.api.enumeration.DishType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize a Dish type
 * 
 * @author cph
 * @version 1.0
 */
public final class DishTypeSerializer extends JsonSerializer<DishType> {

	@Override
	public void serialize(final DishType type, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (type == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(type.toString());
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
