package com.epickur.api.payment.stripe;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.epickur.api.TestUtils;
import com.epickur.api.enumeration.Currency;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(Charge.class)
public class StripePaymentTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static Token TOKEN;
	
	private StripePayment stripePayment;
	@Mock
	private Charge charge;
	@Mock
	private APIConnectionException apiConnectionException;
	@Mock
	private InvalidRequestException invalidReqException;
	@Mock
	private AuthenticationException authenticationException;
	@Mock
	private StripeException stripeException;
	
	
	@BeforeClass
	public static void beforeClass() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
		TestUtils.setupStripe();
		TOKEN = Token.create(TestUtils.getTokenParam());
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		PowerMockito.mockStatic(Charge.class);
		
		when(Charge.create((HashMap<String, Object>) anyObject())).thenReturn(charge);
		stripePayment = new StripePayment();
	}

	@Test
	public void testChargeCard() throws StripeException {
		Integer value = Integer.valueOf(1500);
		when(charge.getPaid()).thenReturn(true);
		when(charge.getAmount()).thenReturn(value);
		
		Charge charge = stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
		assertEquals(true, charge.getPaid());
		assertEquals(value.intValue(), charge.getAmount().intValue());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardInvalidRequestExceptionFail() throws StripeException {
		thrown.expect(InvalidRequestException.class);
		
		when(Charge.create((HashMap<String, Object>) anyObject())).thenThrow(invalidReqException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAuthenticationExceptionFail() throws StripeException {
		thrown.expect(AuthenticationException.class);
		
		when(Charge.create((HashMap<String, Object>) anyObject())).thenThrow(authenticationException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAPIConnectionExceptionFail() throws StripeException {
		thrown.expect(APIConnectionException.class);
		
		when(Charge.create((HashMap<String, Object>) anyObject())).thenThrow(apiConnectionException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardStripeExceptionFail() throws StripeException {
		thrown.expect(StripeException.class);
		
		when(Charge.create((HashMap<String, Object>) anyObject())).thenThrow(apiConnectionException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
}
