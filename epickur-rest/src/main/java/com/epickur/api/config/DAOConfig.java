package com.epickur.api.config;

import com.epickur.api.dao.mongo.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DAOConfig {

	@Bean
	public CatererDAO catererDAO(){
		return new CatererDAO();
	}

	@Bean
	public DishDAO dishDAO(){
		return new DishDAO();
	}

	@Bean
	public KeyDAO keyDAO(){
		return new KeyDAO();
	}

	@Bean
	public LogDAO logDAO(){
		return new LogDAO();
	}

	@Bean
	public OrderDAO orderDAO(){
		return new OrderDAO();
	}

	@Bean
	public SequenceDAO sequenceDAO(){
		return new SequenceDAO();
	}

	@Bean
	public UserDAO userDAO(){
		return new UserDAO();
	}

	@Bean
	public VoucherDAO voucherDAO(){
		return new VoucherDAO();
	}
}
