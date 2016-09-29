package com.epickur.api.dao.mongo.codec;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CoordinatesCodecTest {

	@Mock
	private BsonWriter writer;
	@Mock
	private BsonReader reader;
	private CoordinatesCodec coordinatesCodec;

	@Before
	public void setUp() {
		coordinatesCodec = new CoordinatesCodec();
	}

	@Test
	public void testEncode() {
		Double[] value = new Double[2];
		value[0] = 5.1;
		value[1] = 3.2;
		coordinatesCodec.encode(writer, value, null);
	}

	@Test
	public void testDecode() {
		when(reader.readDouble()).thenReturn(5.0);

		Double[] actual = coordinatesCodec.decode(reader, null);

		assertEquals(5.0, actual[0], 0.0001);
		assertEquals(5.0, actual[1], 0.0001);
	}
}
