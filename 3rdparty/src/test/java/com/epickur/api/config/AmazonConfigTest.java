package com.epickur.api.config;

import com.amazonaws.services.s3.AmazonS3;
import com.epickur.api.aws.AmazonWebServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(EpickurPropertiesTestConfig.class)
public class AmazonConfigTest {

	@Autowired
	private EpickurProperties epickurProperties;

	@Bean
	public AmazonS3 s3clientMock() {
		return mock(AmazonS3.class);
	}

	@Bean
	public AmazonWebServices amazonWebServices() {
		return new AmazonWebServices(epickurProperties, s3clientMock());
	}
}
