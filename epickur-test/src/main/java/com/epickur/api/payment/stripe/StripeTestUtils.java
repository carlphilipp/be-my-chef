package com.epickur.api.payment.stripe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.stripe.Stripe;

public class StripeTestUtils {

	public static void setupStripe() {
		try {
			Stripe.apiKey = getStripeProperty();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void resetStripe() {
		Stripe.apiKey = null;
	}

	public static String getStripeTestKey() {
		try {
			return getStripeProperty();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getStripeProperty() throws IOException {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(StripeTestUtils.class.getClass().getResourceAsStream("/stripe-test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			return prop.getProperty("stripe.key");
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
