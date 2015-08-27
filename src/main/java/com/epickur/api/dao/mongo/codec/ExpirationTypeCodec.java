package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.epickur.api.enumeration.voucher.ExpirationType;

/**
 * @author cph
 * @version 1.0
 *
 */
public final class ExpirationTypeCodec implements Codec<ExpirationType> {

	@Override
	public void encode(final BsonWriter writer, final ExpirationType value, final EncoderContext encoderContext) {
		writer.writeStartDocument();
		writer.writeString("expirationType", value.getType());
		writer.writeEndDocument();
	}

	@Override
	public ExpirationType decode(final BsonReader reader, final DecoderContext decoderContext) {
		return ExpirationType.fromString(reader.readString("expirationType"));
	}

	@Override
	public Class<ExpirationType> getEncoderClass() {
		return ExpirationType.class;
	}
}
