package com.epickur.api;

import com.epickur.api.config.ApplicationConfig;
import com.epickur.api.config.EpickurProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class ApplicationInitializerTest {

	@Autowired
	private EpickurProperties properties;

	@Test
	public void testApplicationInit() {
		log.info("Starting configuration...");
		assertNotNull(properties);
		assertNotNull(properties.getName());
		assertNotNull(properties.getAddress());
		assertNotNull(properties.getFolder());
		assertNotNull(properties.getAdmins());
		assertNotNull(properties.getWebAddress());
		assertNotNull(properties.getMandrillKey());
		assertNotNull(properties.getMandrillFrom());
		assertNotNull(properties.getMandrillFromUsername());
		assertNotNull(properties.getMandrillVersion());
		assertNotNull(properties.getMandrillUrl());
		assertNotNull(properties.getSend());
		assertNotNull(properties.getMongodPath());
		assertNotNull(properties.getMongoBackupPath());
		assertNotNull(properties.getCleanKeysInterval());
		assertNotNull(properties.getOrderTimeLimit());
		assertNotNull(properties.getSessionTimeout());
		assertNotNull(properties.getStripeKey());
		assertNotNull(properties.getHereAppId());
		assertNotNull(properties.getHereAppCode());
		assertNotNull(properties.getHereApiResource());
		assertNotNull(properties.getHereApiVersion());
		assertNotNull(properties.getAwsAccessKeyId());
		assertNotNull(properties.getAwsSecretKey());
		assertNotNull(properties.getAwsBucket());
	}
}
