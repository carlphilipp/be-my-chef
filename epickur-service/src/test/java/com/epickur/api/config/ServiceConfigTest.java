package com.epickur.api.config;

import com.epickur.api.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@PropertySource("classpath:epickur-dev.properties")
@Configuration
public class ServiceConfigTest {
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
		final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		return pspc;
	}

	@Bean
	public Utils utils() {
		return new Utils();
	}
}
