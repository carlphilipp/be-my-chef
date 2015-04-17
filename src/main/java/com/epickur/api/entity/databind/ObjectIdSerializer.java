package com.epickur.api.entity.databind;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * ObjectIdSerializer
 * 
 * @author cph
 * @version 1.0
 */
public final class ObjectIdSerializer extends JsonSerializer<ObjectId> {

	@Override
	public void serialize(final ObjectId objectId, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
		try {
			if (objectId == null) {
				jgen.writeNull();
			} else {
				jgen.writeString(objectId.toHexString());
			}
		} catch (Exception e) {
			throw new IOException();
		}
	}

}
