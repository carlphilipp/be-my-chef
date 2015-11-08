package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class OrderTest {
	
	private static final String stripeMessage = "Fail while acquiring Stripe token. Internet issue?";

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtils.setupStripe();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		TestUtils.resetStripe();
	}

	@Test
	public void testOrder() {
		try {
			Order order = TestUtils.generateRandomOrder();
			Order order2 = order.clone();

			assertEquals(order.hashCode(), order2.hashCode());
			assertEquals(order, order2);

			Order key3 = order2;
			assertEquals(order, key3);
			assertFalse(order.equals(null));
			assertFalse(order.equals(new User()));
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(stripeMessage);
		}
	}
}
