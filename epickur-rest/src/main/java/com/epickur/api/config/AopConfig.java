package com.epickur.api.config;

import com.epickur.api.aop.ComplexAccessRightsAspect;
import com.epickur.api.aop.SimpleAccessRightsAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

	@Bean
	public SimpleAccessRightsAspect simpleAccessRights() {
		return new SimpleAccessRightsAspect();
	}

	@Bean
	public ComplexAccessRightsAspect complexAccessRights() {
		return new ComplexAccessRightsAspect();
	}
}
