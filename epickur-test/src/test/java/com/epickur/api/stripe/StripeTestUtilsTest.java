package com.epickur.api.stripe;

import org.junit.Test;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

public class StripeTestUtilsTest {

	@Test
	public void testSetupStripe() throws IOException {
		StripeTestUtils.getStripeProperty();
	}

	@Test
	public void testGetStripeTestKey() throws IOException {
		assertNotNull(StripeTestUtils.getStripeTestKey());
	}

	@Test
	public void testGetStripeProperty() throws IOException {
		assertNotNull(StripeTestUtils.getStripeProperty());
	}
}
