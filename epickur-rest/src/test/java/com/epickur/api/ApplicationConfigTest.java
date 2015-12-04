package com.epickur.api;

import com.epickur.api.config.ValidatorConfig;
import com.epickur.api.dao.mongo.*;
import com.epickur.api.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(value = { "CatererService", "CatererDAO", "IntegrationTestUtils", "UserDAO", "UserService", "KeyService" })
@Import({ ValidatorConfig.class })
public class ApplicationConfigTest {

	@Bean
	public IntegrationTestUtils integrationTestUtils() {
		return IntegrationTestUtils.getInstance();
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
}
