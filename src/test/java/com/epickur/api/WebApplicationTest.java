package com.epickur.api;

import org.junit.Test;

public class WebApplicationTest {

	@Test
	public void testCreate() {
		new WebApplication();
		WebApplication.initialize();
	}

}
