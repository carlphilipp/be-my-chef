package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.epickur.api.enumeration.DishType;

public class DishTypeCodec implements Codec<DishType> {

	@Override
	public void encode(BsonWriter writer, DishType value, EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("dishType", value.getType());
		writer.writeEndDocument();
	}

	@Override
	public DishType decode(BsonReader reader, DecoderContext decoderContext) {
		return DishType.fromString(reader.readString("dishType"));
	}

	@Override
	public Class<DishType> getEncoderClass() {
		return DishType.class;
	}

}
