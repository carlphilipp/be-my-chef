package com.epickur.api.stripe;

import com.epickur.api.enumeration.Currency;
import com.epickur.api.helper.EntityGenerator;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@PowerMockIgnore("javax.management.*")
@RunWith(org.powermock.modules.junit4.PowerMockRunner.class)
@PrepareForTest(Charge.class)
public class StripePaymentTest {

	private static Token TOKEN;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private Charge charge;
	@Mock
	private StripeException stripeException;
	private StripePayment stripePayment;
	
	
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
		Integer value = 1500;
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
		InvalidRequestException invalidReqException = new InvalidRequestException("error", null, null, null, new Throwable());
		
		when(Charge.create(anyObject())).thenThrow(invalidReqException);
		
		Integer value = -1500;
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAuthenticationExceptionFail() throws StripeException {
		thrown.expect(AuthenticationException.class);
		AuthenticationException authenticationException = new AuthenticationException(null, null, null);
		
		when(Charge.create(anyObject())).thenThrow(authenticationException);
		
		Integer value = -1500;
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardAPIConnectionExceptionFail() throws StripeException {
		thrown.expect(APIConnectionException.class);
		APIConnectionException apiConnectionException = new APIConnectionException("error");
		
		when(Charge.create(anyObject())).thenThrow(apiConnectionException);
		
		Integer value = -1500;
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testChargeCardStripeExceptionFail() throws StripeException {
		thrown.expect(StripeException.class);
		APIConnectionException apiConnectionException = new APIConnectionException("error");
		
		when(Charge.create(anyObject())).thenThrow(apiConnectionException);
		
		Integer value = -1500;
		
		stripePayment.chargeCard(TOKEN.getId(), value, Currency.AUD);
	}
}
