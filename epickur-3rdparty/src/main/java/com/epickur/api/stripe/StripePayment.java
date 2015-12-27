package com.epickur.api.stripe;

import com.epickur.api.enumeration.Currency;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Make a payment to Stripe.
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
public class StripePayment {

	/**
	 * Charge card
	 *
	 * @param cardToken the card token
	 * @param amount    the amount
	 * @param currency  the currency
	 * @return a charge
	 * @throws StripeException If a StripException occured
	 */
	public Charge chargeCard(final String cardToken, final int amount, final Currency currency) throws StripeException {
		final Charge charge;
		Map<String, Object> chargeMap = null;
		try {
			chargeMap = new HashMap<>();
			chargeMap.put("amount", amount);
			chargeMap.put("currency", currency.getCode());
			chargeMap.put("card", cardToken);
			charge = Charge.create(chargeMap);
			log.debug("Charge customer: " + charge.toString());
		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			final StringBuilder stb = new StringBuilder();
			stb.append("Card declined: ").append(chargeMap);
			stb.append("\nStatus is: ").append(e.getCode());
			stb.append("\nMessage is: ").append(e.getParam());
			stb.append("\n").append(e.getLocalizedMessage());
			log.error(stb.toString(), e);
			throw e;
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			log.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed (maybe you changed API keys recently)
			log.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			log.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (StripeException e) {
			// Display a very generic error to the user, and maybe send yourself an email
			log.error(e.getLocalizedMessage(), e);
			throw e;
		}
		return charge;
	}
}
