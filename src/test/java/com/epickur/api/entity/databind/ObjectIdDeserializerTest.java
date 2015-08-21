package com.epickur.api.entity.databind;

import java.io.IOException;

import org.junit.Test;

import com.epickur.api.entity.deserialize.ObjectIdDeserializer;

public class ObjectIdDeserializerTest {
	
	@Test(expected = IOException.class)
	public void testDateSerialize() throws IOException {
		ObjectIdDeserializer ser = new ObjectIdDeserializer();
		ser.deserialize(null, null);
	}
}
