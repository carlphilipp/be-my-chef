package com.epickur.api.config;

import com.epickur.api.here.IGeocoder;
import com.epickur.api.here.GeocoderHereImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoCoder {

	@Bean
	public IGeocoder geocoder(){
		return new GeocoderHereImpl();
	}
}
