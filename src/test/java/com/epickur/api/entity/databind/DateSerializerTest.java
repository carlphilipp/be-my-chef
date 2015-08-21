package com.epickur.api.entity.databind;

import java.io.IOException;

import org.junit.Test;

import com.epickur.api.entity.serialize.DateSerializer;

public class DateSerializerTest {

	@Test(expected = IOException.class)
	public void testDateSerialize() throws IOException {
		DateSerializer ser = new DateSerializer();
		ser.serialize(null, null, null);
	}

}
