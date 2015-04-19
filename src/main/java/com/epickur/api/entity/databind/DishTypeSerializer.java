package com.epickur.api.entity.databind;

import java.io.IOException;

import com.epickur.api.enumeration.DishType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DishTypeSerializer extends JsonSerializer<DishType> {

	@Override
	public void serialize(DishType type, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		try {
			if (type == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(type.toString());
			}
		} catch (Exception e) {
			throw new IOException();
		}
	}
}
