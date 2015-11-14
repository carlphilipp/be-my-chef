package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.epickur.api.enumeration.voucher.Status;

/**
 * Codec to allow MongoDB to Serliaze and Deserialize a StatusCodec (enum). This is needed when using the enum into a query to MongoDB.
 * 
 * @author cph
 * @version 1.0
 *
 */
public final class StatusCodec implements Codec<Status> {

	@Override
	public void encode(final BsonWriter writer, final Status value, final EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("status", value.getType());
		writer.writeEndDocument();
	}

	@Override
	public Status decode(final BsonReader reader, final DecoderContext decoderContext) {
		return Status.fromString(reader.readString("status"));
	}

	@Override
	public Class<Status> getEncoderClass() {
		return Status.class;
	}
}
