package com.epickur.api;

import static org.junit.Assert.fail;

import org.junit.Test;

public class WebApplicationTest {

	@Test
	public void testCreate() {
		try {
			new WebApplication();
			WebApplication.initialize();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
	}
}
