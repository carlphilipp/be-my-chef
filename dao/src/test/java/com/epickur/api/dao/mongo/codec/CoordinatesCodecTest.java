package com.epickur.api.dao.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

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
		// Given
		Double[] value = new Double[2];
		value[0] = 5.1;
		value[1] = 3.2;

		// When
		coordinatesCodec.encode(writer, value, null);
	}

	@Test
	public void testDecode() {
		// Given
		given(reader.readDouble()).willReturn(5.0);

		// When
		Double[] actual = coordinatesCodec.decode(reader, null);

		// Then
		assertEquals(5.0, actual[0], 0.0001);
		assertEquals(5.0, actual[1], 0.0001);
	}
}
