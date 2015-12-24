package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;

public class GeoTest {

	@Test
	public void testGeo() {
		Geo geo = EntityGenerator.generateGeo();
		Geo geo2 = geo.clone();

		assertEquals(geo.hashCode(), geo2.hashCode());
		assertEquals(geo, geo2);
	}

	@Test
	public void testGeo2() throws EpickurException {
		Geo geo = new Geo();
		Map<String, Object> actual = geo.getUpdateMap("prefix");
		assertEquals(2, actual.size());
		assertEquals("Point", actual.get("prefix.type"));
		assertEquals(new Double(0.0), ((Double[])actual.get("prefix.coordinates"))[0]);
		assertEquals(new Double(0.0), ((Double[])actual.get("prefix.coordinates"))[1]);
	}
}
