package com.epickur.api.entity.databind;

import java.io.IOException;

import org.junit.Test;

import com.epickur.api.entity.serialize.ObjectIdSerializer;

public class ObjectIdSerializerTest {

	@Test(expected = IOException.class)
	public void testDateSerialize() throws IOException {
		ObjectIdSerializer ser = new ObjectIdSerializer();
		ser.serialize(null, null, null);
	}
}
