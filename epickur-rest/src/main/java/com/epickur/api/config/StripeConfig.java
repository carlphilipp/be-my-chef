package com.epickur.api.config;

import com.epickur.api.payment.stripe.StripePayment;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

	@Value("${stripe.key}")
	private String apiKey;

	@Bean
	public StripePayment stripe(){
		Stripe.apiKey = apiKey;
		return new StripePayment();
	}
}
