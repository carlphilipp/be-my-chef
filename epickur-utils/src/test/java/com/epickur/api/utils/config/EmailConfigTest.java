package com.epickur.api.utils.config;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.config.PropertySourcesConfig;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@PropertySource("classpath:epickur-dev.properties")
@Configuration
@Import(
		PropertySourcesConfig.class
)
public class EmailConfigTest {

	@Bean
	public Utils utils(){
		return new Utils();
	}

	@Bean
	public EmailTemplate emailTemplate(){
		return new EmailTemplate();
	}

	@Bean
	public Email email(){
		return new Email();
	}

	@Bean
	public MandrillConfiguration mandrillConfiguration(){
		MandrillConfiguration mandrillConfiguration = new MandrillConfiguration();
		mandrillConfiguration.setApiKey(epickurProperties().getMandrillKey());
		mandrillConfiguration.setApiVersion(epickurProperties().getMandrillVersion());
		mandrillConfiguration.setBaseURL(epickurProperties().getMandrillUrl());
		return mandrillConfiguration;
	}

	@Bean
	public MandrillMessagesRequest mandrillMessagesRequest(){
		return new MandrillMessagesRequest();
	}

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}
}
