package com.epickur.api.config;

import com.epickur.api.here.GeocoderHereImpl;
import com.epickur.api.here.Here;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EpickurPropertiesTestConfig.class)
public class GeocoderConfigTest {

	@Autowired
	private EpickurProperties epickurProperties;

	@Bean
	public GeocoderHereImpl geocoderHere() {
		return new GeocoderHereImpl();
	}

	@Bean
	public Here here() {
		return new Here(epickurProperties, objectMapper());
	}

	@Bean
	public Utils utils() {
		return new Utils(epickurProperties);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
