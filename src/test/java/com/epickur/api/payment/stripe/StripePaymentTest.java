package com.epickur.api.payment.stripe;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.enumeration.Currency;
import com.epickur.api.integration.UserIntegrationTest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;

public class StripePaymentTest {

	private static String STRIPE_TEST_KEY;

	@BeforeClass
	public static void beforeClass() {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			in.close();
			STRIPE_TEST_KEY = prop.getProperty("stripe.key");
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
	public void testChargeCard() throws StripeException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);

		StripePayment payment = new StripePayment();
		Charge charge = payment.chargeCard(token.getId(), 1500, Currency.AUD);
		assertEquals(true, charge.getPaid());
	}

	@Test(expected = StripeException.class)
	public void testChargeCardFail() throws StripeException {
		Stripe.apiKey = STRIPE_TEST_KEY;
		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 2);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);

		StripePayment payment = new StripePayment();
		payment.chargeCard(token.getId(), -1500, Currency.AUD);
	}
}
