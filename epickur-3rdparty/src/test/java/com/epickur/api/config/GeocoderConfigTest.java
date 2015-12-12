package com.epickur.api.config;

import com.epickur.api.geocoder.here.GeocoderHereImpl;
import com.epickur.api.geocoder.here.Here;
import com.epickur.api.utils.Utils;
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
}