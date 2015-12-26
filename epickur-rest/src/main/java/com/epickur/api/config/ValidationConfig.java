package com.epickur.api.config;

import com.epickur.api.validation.CatererValidation;
import com.epickur.api.validation.DishValidation;
import com.epickur.api.validation.FactoryValidation;
import com.epickur.api.validation.UserValidation;
import com.epickur.api.validation.VoucherValidation;
import org.springframework.context.annotation.Bean;

public class ValidationConfig {

	@Bean
	public CatererValidation catererValidation(){
		return (CatererValidation) FactoryValidation.getValidation("caterer");
	}

	@Bean
	public DishValidation dishValidation(){
		return (DishValidation) FactoryValidation.getValidation("dish");
	}

	@Bean
	public UserValidation userValidation(){
		return (UserValidation) FactoryValidation.getValidation("user");
	}

	@Bean
	public VoucherValidation voucherValidation(){
		return (VoucherValidation) FactoryValidation.getValidation("voucher");
	}
}
