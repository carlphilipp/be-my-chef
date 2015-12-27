package com.epickur.api.integration;

import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.config.EpickurProperties;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class AccessRights {
	protected static final String CONTENT_TYPE = "content-type";
	protected static final String JSON_MIME_TYPE = "application/json";

	protected String protocol;
	protected String host;
	protected String port;
	protected String path;

	@Autowired
	protected IntegrationTestUtils integrationTestUtils;
	@Autowired
	protected ObjectMapper mapper;
	@Autowired
	protected EpickurProperties properties;

	@PostConstruct
	public void postConstruct() throws IOException, EpickurException {
		IntegrationTestUtils.cleanDB();
		protocol = properties.getProtocol();
		host = properties.getHost();
		port = properties.getPort().toString();
		path = properties.getPath();
		IntegrationTestUtils.setupDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		IntegrationTestUtils.cleanDB();
	}
}
