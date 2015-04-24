package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Codec to allow Mongo to Serialize and Deserialize a tab of double that represents coordinates
 * 
 * @author cph
 * @version 1.0
 */
public final class CoordinatesCodec implements Codec<Double[]> {

	@Override
	public void encode(final BsonWriter writer, final Double[] value, final EncoderContext encoderContext) {
		writer.writeStartArray();
		writer.writeDouble(value[0]);
		writer.writeDouble(value[1]);
		writer.writeEndArray();
	}

	@Override
	public Double[] decode(final BsonReader reader, final DecoderContext decoderContext) {
		Double[] res = new Double[2];
		reader.readStartArray();
		res[0] = reader.readDouble();
		res[1] = reader.readDouble();
		reader.readEndArray();
		return res;
	}

	@Override
	public Class<Double[]> getEncoderClass() {
		return Double[].class;
	}
}
