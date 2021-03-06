package com.epickur.api.stripe;

import com.epickur.api.enumeration.Currency;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class StripePaymentTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ChargeWrapper chargeWrapper;
	@Mock
	private Charge charge;
	@Mock
	private Token token;
	@Mock
	private StripeException stripeException;
	private StripePayment stripePayment;

	@Before
	public void setUp() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		//PowerMockito.mockStatic(Charge.class);

		given(chargeWrapper.createCharge(anyMap())).willReturn(charge);
		stripePayment = new StripePayment(chargeWrapper);
	}

	@Test
	public void testChargeCard() throws StripeException {
		// Given
		Integer value = 1500;
		given(charge.getPaid()).willReturn(true);
		given(charge.getAmount()).willReturn(value.longValue());

		// When
		Charge charge = stripePayment.chargeCard(token.getId(), value, Currency.AUD);

		// Then
		assertTrue(charge.getPaid());
		assertEquals(value.intValue(), charge.getAmount().intValue());
	}

	@Test
	public void testChargeCardInvalidRequestExceptionFail() throws StripeException {
		// Then
		thrown.expect(InvalidRequestException.class);

		// Given
		InvalidRequestException invalidReqException = new InvalidRequestException("error", null, null, null, new Throwable());
		given(chargeWrapper.createCharge(anyMap())).willThrow(invalidReqException);
		Integer value = -1500;

		// When
		stripePayment.chargeCard(token.getId(), value, Currency.AUD);
	}

	@Test
	public void testChargeCardAuthenticationExceptionFail() throws StripeException {
		// Then
		thrown.expect(AuthenticationException.class);

		// Given
		AuthenticationException authenticationException = new AuthenticationException(null, null, null);
		given(chargeWrapper.createCharge(anyMap())).willThrow(authenticationException);
		Integer value = -1500;

		// When
		stripePayment.chargeCard(token.getId(), value, Currency.AUD);
	}

	@Test
	public void testChargeCardAPIConnectionExceptionFail() throws StripeException {
		// Then
		thrown.expect(APIConnectionException.class);

		// Given
		APIConnectionException apiConnectionException = new APIConnectionException("error");
		given(chargeWrapper.createCharge(anyMap())).willThrow(apiConnectionException);
		Integer value = -1500;

		// When
		stripePayment.chargeCard(token.getId(), value, Currency.AUD);
	}

	@Test
	public void testChargeCardStripeExceptionFail() throws StripeException {
		// Then
		thrown.expect(StripeException.class);

		// Given
		APIConnectionException apiConnectionException = new APIConnectionException("error");
		given(chargeWrapper.createCharge(anyMap())).willThrow(apiConnectionException);
		Integer value = -1500;

		// When
		stripePayment.chargeCard(token.getId(), value, Currency.AUD);
	}
}
