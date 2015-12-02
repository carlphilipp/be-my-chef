package com.epickur.api.config;

import com.epickur.api.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

	@Bean
	public CatererService catererService(){
		return new CatererService();
	}

	@Bean
	public DishService dishService(){
		return new DishService();
	}

	@Bean
	public KeyService keyService(){
		return new KeyService();
	}

	@Bean
	public OrderService orderService(){
		return new OrderService();
	}

	@Bean
	public UserService userService(){
		return new UserService();
	}

	@Bean
	public VoucherService voucherService(){
		return new VoucherService();
	}
}
