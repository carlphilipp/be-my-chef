package com.epickur.api.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonWSConfig {

	@Autowired
	public EpickurProperties properties;

	@Bean
	public AmazonS3 amazonS3() {
		final BasicAWSCredentials awsCreds = new BasicAWSCredentials(properties.getAwsAccessKeyId(), properties.getAwsSecretKey());
		return new AmazonS3Client(awsCreds);
	}
}