package com.epickur.api.config;

import com.epickur.api.here.GeocoderHereImpl;
import com.epickur.api.here.Here;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:epickur-dev.properties")
@Import(
		PropertySourcesConfig.class
)
public class GeocoderConfigTest {
	@Bean
	public GeocoderHereImpl geocoderHere() {
		return new GeocoderHereImpl();
	}

	@Bean
	public Here here() {
		return new Here();
	}

	@Bean
	public Utils utils() {
		return new Utils();
	}

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
