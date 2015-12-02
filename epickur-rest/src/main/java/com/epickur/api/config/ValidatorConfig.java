package com.epickur.api.config;

import com.epickur.api.validator.*;
import org.springframework.context.annotation.Bean;

public class ValidatorConfig {

	@Bean
	public CatererValidator catererValidator(){
		return (CatererValidator) FactoryValidator.getValidator("caterer");
	}

	@Bean
	public DishValidator dishValidator(){
		return (DishValidator) FactoryValidator.getValidator("dish");
	}

	@Bean
	public UserValidator userValidator(){
		return (UserValidator) FactoryValidator.getValidator("user");
	}

	@Bean
	public VoucherValidator voucherValidator(){
		return (VoucherValidator) FactoryValidator.getValidator("voucher");
	}
}
