package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.epickur.api.enumeration.DishType;

/**
 * A codec to serialize and deserialize at MongoDB level
 * 
 * @author cph
 * @version 1.0
 */
public final class DishTypeCodec implements Codec<DishType> {

	@Override
	public void encode(final BsonWriter writer, final DishType value, final EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("dishType", value.getType());
		writer.writeEndDocument();
	}

	@Override
	public DishType decode(final BsonReader reader, final DecoderContext decoderContext) {
		return DishType.fromString(reader.readString("dishType"));
	}

	@Override
	public Class<DishType> getEncoderClass() {
		return DishType.class;
	}
}
