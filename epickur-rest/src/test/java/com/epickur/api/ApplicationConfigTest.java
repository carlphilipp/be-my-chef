package com.epickur.api;

import com.epickur.api.config.*;
import com.epickur.api.dao.mongo.*;
import com.epickur.api.here.GeocoderHereImpl;
import com.epickur.api.here.Here;
import com.epickur.api.service.*;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import com.epickur.api.utils.email.EmailUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:epickur-dev.properties")
@Import({ SchedulerConfig.class, MongoConfig.class, ValidatorConfig.class, GeoCoder.class, AmazonWSConfig.class, StripeConfig.class,
				EmailConfig.class })
public class ApplicationConfigTest {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws Exception {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public IntegrationTestUtils integrationTestUtils() {
		return new IntegrationTestUtils();
	}

	@Bean
	public CatererDAO catererDAO() {
		return new CatererDAO();
	}

	@Bean
	public DishDAO dishDAO() {
		return new DishDAO();
	}

	@Bean
	public KeyDAO keyDAO() {
		return new KeyDAO();
	}

	@Bean
	public LogDAO logDAO() {
		return new LogDAO();
	}

	@Bean
	public OrderDAO orderDAO() {
		return new OrderDAO();
	}

	@Bean
	public SequenceDAO sequenceDAO() {
		return new SequenceDAO();
	}

	@Bean
	public UserDAO userDAO() {
		return new UserDAO();
	}

	@Bean
	public VoucherDAO voucherDAO() {
		return new VoucherDAO();
	}

	@Bean
	public CatererService catererService() {
		return new CatererService();
	}

	@Bean
	public DishService dishService() {
		return new DishService();
	}

	@Bean
	public KeyService keyService() {
		return new KeyService();
	}

	@Bean
	public OrderService orderService() {
		return new OrderService();
	}

	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public VoucherService voucherService() {
		return new VoucherService();
	}

	@Bean
	public Utils utils() {
		return new Utils();
	}

	@Bean
	public GeocoderHereImpl geocoderHere() {
		return new GeocoderHereImpl();
	}

	@Bean
	public Here here() {
		return new Here();
	}

	@Bean
	public EmailUtils emailUtils() {
		return new EmailUtils();
	}

	@Bean
	public EmailTemplate emailTemplate() {
		return new EmailTemplate();
	}

	@Bean
	public Email email() {
		return new Email();
	}

	@Bean
	public EpickurProperties epickurProperties() {
		return new EpickurProperties();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
