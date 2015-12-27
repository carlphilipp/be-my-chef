package com.epickur.api.stripe;

import com.stripe.Stripe;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class StripeTestUtils {

	public static void setupStripe() {
		try {
			Stripe.apiKey = getStripeProperty();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void resetStripe() {
		Stripe.apiKey = null;
	}

	public static String getStripeTestKey() {
		try {
			return getStripeProperty();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	protected static String getStripeProperty() throws IOException {
		@Cleanup InputStream in = StripeTestUtils.class.getResource("/stripe-test.properties").openStream();
		final Properties prop = new Properties();
		prop.load(in);
		return prop.getProperty("stripe.key");
	}
}
