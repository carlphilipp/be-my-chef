package com.epickur.api.config;

import com.epickur.api.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:epickur-dev.properties")
@Configuration
@Import(
		PropertySourcesConfig.class
)
public class ServiceConfigTest {

	@Bean
	public Utils utils() {
		return new Utils(epickurProperties());
	}

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}
}
