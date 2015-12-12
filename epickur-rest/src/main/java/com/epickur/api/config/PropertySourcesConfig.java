package com.epickur.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class PropertySourcesConfig {

	@Profile("dev")
	public static class Dev {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
			propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(false);
			return propertySourcesPlaceholderConfigurer;
		}
	}

	@Profile("prod")
	public static class Prod {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
			propertySourcesPlaceholderConfigurer.setIgnoreResourceNotFound(false);
			return propertySourcesPlaceholderConfigurer;
		}
	}
}
