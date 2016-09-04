package com.epickur.api.stripe;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.config.StripeConfigTest;
import com.epickur.api.enumeration.Currency;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = StripeConfigTest.class)
public class StripePaymentIT {

	@Autowired
	private EpickurProperties properties;

	@Before
	public void setUp() {
		Stripe.apiKey = properties.getStripeKey();
	}

	@After
	public void tearDown() throws Exception {
		Stripe.apiKey = null;
	}

	@Test
	public void testChargeCard() throws StripeException {
		Map<String, Object> tokenParams = new HashMap<>();
		Map<String, Object> cardParams = new HashMap<>();
		cardParams.put("number", "4242424242424242");
		cardParams.put("exp_month", 12);
		cardParams.put("exp_year", 2016);
		cardParams.put("cvc", "314");
		tokenParams.put("card", cardParams);
		Token token = Token.create(tokenParams);

		StripePayment payment = new StripePayment();
		Charge charge = payment.chargeCard(token.getId(), 1500, Currency.AUD);

		assertTrue(charge.getPaid());
		assertEquals(1500, charge.getAmount().intValue());
	}

	@Test(expected = StripeException.class)
	public void testChargeCardFail() throws StripeException {
		Map<String, Object> tokenParams = new HashMap<>();
		Map<String, Object> cardParams = new HashMap<>();
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
