package com.epickur.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:epickur-dev.properties")
@Import(PropertySourcesConfig.class)
public class StripeConfigTest {

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}
}
