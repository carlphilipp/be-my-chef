package com.epickur.api.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by carl on 12/11/2015.
 */
@Configuration
public class AmazonWSConfig {

	@Value("{aws.access.KeyId}")
	private String accessKeyId;
	@Value("{aws.secretKey}")
	private String secretKey;

	@Bean
	public AmazonS3 amazonS3(){
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKeyId, secretKey);
		AmazonS3 amazonS3 = new AmazonS3Client(awsCreds);
		return amazonS3;
	}
}
