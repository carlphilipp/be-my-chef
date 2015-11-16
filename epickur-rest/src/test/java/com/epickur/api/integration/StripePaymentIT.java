package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.enumeration.Currency;
import com.epickur.api.helper.EntityGenerator;
import com.epickur.api.payment.stripe.StripePayment;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;

public class StripePaymentIT {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		EntityGenerator.setupStripe();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		EntityGenerator.resetStripe();
	}

	@Test
	public void testChargeCard() throws StripeException {
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
		assertEquals(1500, charge.getAmount().intValue());
	}

	@Test(expected = StripeException.class)
	public void testChargeCardFail() throws StripeException {
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
