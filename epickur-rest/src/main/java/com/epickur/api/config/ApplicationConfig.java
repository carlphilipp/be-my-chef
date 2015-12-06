package com.epickur.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epickur.api")
@EnableAspectJAutoProxy
@Import({
		AopConfig.class,
		ValidatorConfig.class
})
public class ApplicationConfig {
}
