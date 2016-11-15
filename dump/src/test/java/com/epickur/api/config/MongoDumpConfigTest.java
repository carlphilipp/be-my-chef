package com.epickur.api.config;

import com.epickur.api.commons.CommonsUtil;
import com.epickur.api.dump.MongoDBDump;
import com.epickur.api.utils.Utils;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:dump-test-data.properties")
@Import(PropertySourcesConfig.class)
public class MongoDumpConfigTest {

	@Bean
	public Utils utils() {
		return new Utils(properties());
	}

	@Bean
	public EpickurProperties properties() {
		return new EpickurProperties();
	}

	@Bean
	@Scope("prototype")
	public MongoDBDump dbDump() {
		return new MongoDBDump(CommonsUtil.getCurrentDateInFormat("ddMMyyyy-hhmmss"));
	}
}
