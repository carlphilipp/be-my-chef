package com.epickur.api.config;

import com.epickur.api.dao.mongo.DishDAO;
import com.epickur.api.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@PropertySource("classpath:epickur-${spring.profiles.active:dev}.properties")
@EnableWebMvc
@ComponentScan(basePackages = "com.epickur.api")
@EnableAspectJAutoProxy
@EnableScheduling
@Import({
				AopConfig.class,
				AmazonWSConfig.class,
				EmailConfig.class,
				GeoCoder.class,
				MongoConfig.class,
				PropertySourcesConfig.class,
				StripeConfig.class,
				SchedulerConfig.class,
				ValidatorConfig.class
		})
public class ApplicationConfig {

	@Autowired
	DishDAO daol;

	@Bean
	public DishService dishService(){
		return new DishService(daol);
	}
}
