package com.epickur.api.config;

import com.amazonaws.services.s3.AmazonS3;
import com.epickur.api.dump.AmazonWebServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

import static org.mockito.Mockito.mock;

@Configuration
public class AmazonConfigTest {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
		final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		Properties properties = new Properties();

		properties.setProperty("aws.bucket", "epickur-dbdump");

		pspc.setProperties(properties);
		return pspc;
	}

	@Bean
	public AmazonWebServices amazonWebServices() {
		return new AmazonWebServices();
	}

	@Bean
	public AmazonS3 s3clientMock() {
		return mock(AmazonS3.class);
	}
}
