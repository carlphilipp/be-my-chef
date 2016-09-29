package com.epickur.api.config;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

	@Autowired
	public EpickurProperties properties;

	@Bean
	public MandrillConfiguration mandrillConfiguration() {
		final MandrillConfiguration mandrillConfiguration = new MandrillConfiguration();
		mandrillConfiguration.setApiKey(properties.getMandrillKey());
		mandrillConfiguration.setApiVersion(properties.getMandrillVersion());
		mandrillConfiguration.setBaseURL(properties.getMandrillUrl());
		return mandrillConfiguration;
	}

	@Bean
	public MandrillMessagesRequest mandrillMessagesRequest() {
		return new MandrillMessagesRequest();
	}
}
