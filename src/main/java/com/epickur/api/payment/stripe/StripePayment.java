package com.epickur.api.payment.stripe;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.enumeration.Currency;
import com.epickur.api.utils.Utils;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

/**
 * Make a payment to Stripe.
 * 
 * @author cph
 * @version 1.0
 */
public class StripePayment {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(StripePayment.class.getSimpleName());

	static {
		Properties prop = Utils.getEpickurProperties();
		Stripe.apiKey = prop.getProperty("stripe.key");
	}

	/**
	 * Charge card
	 * 
	 * @param cardToken
	 *            the card token
	 * @param amount
	 *            the amount
	 * @param currency
	 *            the currency
	 * @return a charge
	 * @throws StripeException
	 *             If a StripException occured
	 */
	public Charge chargeCard(final String cardToken, final int amount, final Currency currency) throws StripeException {
		Charge charge = null;
		Map<String, Object> chargeMap = null;
		try {
			chargeMap = new HashMap<String, Object>();
			chargeMap.put("amount", amount);
			chargeMap.put("currency", currency.getCode());
			chargeMap.put("card", cardToken);
			charge = Charge.create(chargeMap);
			LOG.debug(charge);
		} catch (CardException e) {
			// Since it's a decline, CardException will be caught
			StringBuilder stb = new StringBuilder();
			stb.append("Card declined: " + chargeMap);
			stb.append("\nStatus is: " + e.getCode());
			stb.append("\nMessage is: " + e.getParam());
			stb.append("\n" + e.getLocalizedMessage());
			LOG.error(stb.toString(), e);
			throw e;
		} catch (InvalidRequestException e) {
			// Invalid parameters were supplied to Stripe's API
			LOG.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (AuthenticationException e) {
			// Authentication with Stripe's API failed (maybe you changed API keys recently)
			LOG.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (APIConnectionException e) {
			// Network communication with Stripe failed
			LOG.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (StripeException e) {
			// Display a very generic error to the user, and maybe send yourself an email
			LOG.error(e.getLocalizedMessage(), e);
			throw e;
		}
		return charge;
	}
}
