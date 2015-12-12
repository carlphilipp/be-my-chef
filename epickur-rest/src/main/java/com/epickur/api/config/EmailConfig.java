package com.epickur.api.config;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

	@Value("email.mandrill.key")
	private String mandrillKey;
	@Value("email.mandrill.version")
	private String mandrillVersion;
	@Value("email.mandrill.url")
	private String mandrillUrl;

	@Bean
	public MandrillConfiguration mandrillConfiguration(){
		MandrillConfiguration mandrillConfiguration = new MandrillConfiguration();
		mandrillConfiguration.setApiKey(mandrillKey);
		mandrillConfiguration.setApiVersion(mandrillVersion);
		mandrillConfiguration.setBaseURL(mandrillUrl);
		return mandrillConfiguration;
	}

	@Bean
	public MandrillMessagesRequest mandrillMessagesRequest(){
		return new MandrillMessagesRequest();
	}
}
