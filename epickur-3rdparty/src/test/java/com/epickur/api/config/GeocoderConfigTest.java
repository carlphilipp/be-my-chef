package com.epickur.api.config;

import com.epickur.api.geocoder.here.GeocoderHereImpl;
import com.epickur.api.geocoder.here.Here;
import com.epickur.api.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@Configuration
public class GeocoderConfigTest {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
		final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		Properties properties = new Properties();

		// TODO refactor that properties somewhere
		properties.setProperty("here.app.id", "jCZrpe59WjQkH2mCGz3L");
		properties.setProperty("here.app.code", "uMeIWCGXbXo-IO92WANtNA");
		properties.setProperty("here.api.resource", "geocode.json");
		properties.setProperty("here.api.version", "6.2");
		properties.setProperty("session.timeout", "5");

		pspc.setProperties(properties);
		return pspc;
	}

	@Bean
	public GeocoderHereImpl geocoderHere(){
		return new GeocoderHereImpl();
	}

	@Bean
	public Here here(){
		return new Here();
	}

	@Bean
	public Utils utils(){
		return new Utils();
	}
}
