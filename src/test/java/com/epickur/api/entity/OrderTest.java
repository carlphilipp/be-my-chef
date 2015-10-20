package com.epickur.api.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.integration.UserIT;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

public class OrderTest {
	
	@BeforeClass
	public static void beforeClass() {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			Stripe.apiKey = prop.getProperty("stripe.key");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Test
	public void testOrder() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		Order order = TestUtils.generateRandomOrder();
		Order order2 = order.clone();

		assertEquals(order.hashCode(), order2.hashCode());
		assertEquals(order, order2);

		Order key3 = order2;
		assertEquals(order, key3);
		assertFalse(order.equals(null));
		assertFalse(order.equals(new User()));
	}
}
