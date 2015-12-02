package com.epickur.api;

import com.epickur.api.config.DAOConfig;
import com.epickur.api.config.ServiceConfig;
import com.epickur.api.config.ValidatorConfig;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(value = { "CatererService", "CatererDAO", "IntegrationTestUtils", "UserDAO", "UserService", "KeyService" })
@Import({ ServiceConfig.class, DAOConfig.class, ValidatorConfig.class })
public class ApplicationConfigTest {

	@Bean
	public IntegrationTestUtils integrationTestUtils() {
		return IntegrationTestUtils.getInstance();
	}
}
