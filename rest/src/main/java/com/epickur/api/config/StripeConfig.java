package com.epickur.api.config;

import com.epickur.api.stripe.StripePayment;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

	@Autowired
	public EpickurProperties properties;

	@Bean
	public StripePayment stripe() {
		Stripe.apiKey = properties.getStripeKey();
		return new StripePayment();
	}
}
