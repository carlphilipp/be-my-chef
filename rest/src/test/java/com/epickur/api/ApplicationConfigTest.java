package com.epickur.api;

import com.epickur.api.config.*;
import com.epickur.api.cron.OrderJob;
import com.epickur.api.dao.mongo.*;
import com.epickur.api.here.GeocoderHereImpl;
import com.epickur.api.here.Here;
import com.epickur.api.service.*;
import com.epickur.api.stripe.ChargeWrapper;
import com.epickur.api.stripe.StripePayment;
import com.epickur.api.utils.Utils;
import com.epickur.api.utils.email.Email;
import com.epickur.api.utils.email.EmailTemplate;
import com.epickur.api.utils.email.EmailUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:epickur-dev.properties")
@Import({SchedulerConfig.class, MongoConfig.class, ValidationConfig.class, GeoCoder.class, AmazonWSConfig.class, StripeConfig.class,
	EmailConfig.class})
public class ApplicationConfigTest {

	@Autowired
	private MongoDatabase mongoDatabase;

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
		return new CatererDAO(mongoDatabase);
	}

	@Bean
	public DishDAO dishDAO() {
		return new DishDAO(mongoDatabase);
	}

	@Bean
	public KeyDAO keyDAO() {
		return new KeyDAO(mongoDatabase);
	}

	@Bean
	public LogDAO logDAO() {
		return new LogDAO(mongoDatabase);
	}

	@Bean
	public OrderDAO orderDAO() {
		return new OrderDAO(mongoDatabase);
	}

	@Bean
	public SequenceDAO sequenceDAO() {
		return new SequenceDAO(mongoDatabase);
	}

	@Bean
	public UserDAO userDAO() {
		return new UserDAO(mongoDatabase);
	}

	@Bean
	public VoucherDAO voucherDAO() {
		return new VoucherDAO(mongoDatabase);
	}

	@Bean
	public CatererService catererService() {
		return new CatererService(catererDAO());
	}

	@Bean
	public DishService dishService() {
		return new DishService(geocoderHere(), dishDAO());
	}

	@Bean
	public KeyService keyService() {
		return new KeyService(keyDAO());
	}

	@Bean
	public OrderJob orderJob() {
		return new OrderJob();
	}

	@Bean
	public StripePayment stripePayment() {
		return new StripePayment(new ChargeWrapper());
	}

	@Bean
	public OrderService orderService() {
		return new OrderService(orderDAO(), userDAO(), sequenceDAO(), voucherService(), orderJob(), emailUtils(), stripePayment());
	}

	@Bean
	public UserService userService() {
		return new UserService(userDAO(), keyService(), emailUtils(), utils());
	}

	@Bean
	public VoucherService voucherService() {
		return new VoucherService(voucherDAO());
	}

	@Bean
	public Utils utils() {
		return new Utils(epickurProperties());
	}

	@Bean
	public GeocoderHereImpl geocoderHere() {
		return new GeocoderHereImpl();
	}

	@Bean
	public Here here() {
		return new Here(epickurProperties(), objectMapper());
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
