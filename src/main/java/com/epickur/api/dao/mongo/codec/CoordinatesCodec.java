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
public class CoordinatesCodec implements Codec<Double[]> {

	@Override
	public void encode(BsonWriter writer, Double[] value, EncoderContext encoderContext) {
		writer.writeStartArray();
		writer.writeDouble(value[0]);
		writer.writeDouble(value[1]);
		writer.writeEndArray();
	}

	@Override
	public Double[] decode(BsonReader reader, DecoderContext decoderContext) {
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
