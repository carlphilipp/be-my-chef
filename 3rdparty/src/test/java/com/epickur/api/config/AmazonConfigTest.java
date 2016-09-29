package com.epickur.api.config;

import com.amazonaws.services.s3.AmazonS3;
import com.epickur.api.aws.AmazonWebServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import static org.mockito.Mockito.mock;

@Configuration
@PropertySource("classpath:epickur-dev.properties")
@Import(PropertySourcesConfig.class)
public class AmazonConfigTest {

	@Bean
	public AmazonS3 s3clientMock() {
		return mock(AmazonS3.class);
	}

	@Bean
	public AmazonWebServices amazonWebServices() {
		return new AmazonWebServices();
	}

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}
}
