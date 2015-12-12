package com.epickur.api.payment.stripe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.AfterClass;
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

import com.epickur.api.enumeration.Currency;
import com.epickur.api.helper.EntityGenerator;
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

	private APIConnectionException apiConnectionException = new APIConnectionException("error");

	private InvalidRequestException invalidReqException = new InvalidRequestException("error", null, null, null, new Throwable());

	private AuthenticationException authenticationException = new AuthenticationException(null, null, null);
	@Mock
	private StripeException stripeException;
	
	
	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			StripeTestUtils.setupStripe();
			TOKEN = Token.create(EntityGenerator.getTokenParam());
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
			fail(EntityGenerator.STRIPE_MESSAGE);
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		StripeTestUtils.resetStripe();
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException{
		PowerMockito.mockStatic(Charge.class);
		
		when(Charge.create(anyObject())).thenReturn(charge);
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
		
		when(Charge.create(anyObject())).thenThrow(invalidReqException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAuthenticationExceptionFail() throws StripeException {
		thrown.expect(AuthenticationException.class);
		
		when(Charge.create(anyObject())).thenThrow(authenticationException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAPIConnectionExceptionFail() throws StripeException {
		thrown.expect(APIConnectionException.class);
		
		when(Charge.create(anyObject())).thenThrow(apiConnectionException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardStripeExceptionFail() throws StripeException {
		thrown.expect(StripeException.class);
		
		when(Charge.create(anyObject())).thenThrow(apiConnectionException);
		
		Integer value = Integer.valueOf(-1500);
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
}
