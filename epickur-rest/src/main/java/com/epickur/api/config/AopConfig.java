package com.epickur.api.config;

import com.epickur.api.aop.ValidateRequestAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

	@Bean
	public ValidateRequestAspect validateRequestAspect() {
		return new ValidateRequestAspect();
	}
}
