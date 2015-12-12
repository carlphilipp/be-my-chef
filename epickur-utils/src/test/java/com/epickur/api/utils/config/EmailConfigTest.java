package com.epickur.api.utils.config;

import com.cribbstechnologies.clients.mandrill.request.MandrillMessagesRequest;
import com.cribbstechnologies.clients.mandrill.util.MandrillConfiguration;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Properties;

@PropertySource("classpath:epickur-dev.properties")
@Configuration
public class EmailConfigTest {

	@Value("email.mandrill.key")
	private String mandrillKey;
	@Value("email.mandrill.version")
	private String mandrillVersion;
	@Value("email.mandrill.url")
	private String mandrillUrl;

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
		final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		return pspc;
	}

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
