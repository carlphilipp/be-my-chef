package com.epickur.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:epickur-dev.properties")
@Import(PropertySourcesConfig.class)
@Configuration
public class EpickurPropertiesTestConfig {

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}
}
