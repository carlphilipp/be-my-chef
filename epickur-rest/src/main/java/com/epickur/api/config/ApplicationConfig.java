package com.epickur.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@PropertySource("classpath:epickur.properties")
@EnableWebMvc
@ComponentScan(basePackages = "com.epickur.api")
@Import({
		ValidatorConfig.class
})
public class ApplicationConfig {
}
