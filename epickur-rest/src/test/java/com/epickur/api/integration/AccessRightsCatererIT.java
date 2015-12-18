package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsCatererIT {

	@Autowired
	private IntegrationTestUtils integrationTestUtils;

	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String jsonMimeType;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		@Cleanup InputStreamReader in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		String address = prop.getProperty("address");
		String path = prop.getProperty("api.path");
		END_POINT = address + path;
		jsonMimeType = "application/json";
		mapper = new ObjectMapper();
		EntityGenerator.setupDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		EntityGenerator.cleanDB();
	}

	// User Administrator
	@Test
	public void testAdministratorCatererCreate() throws EpickurException, IOException {

		User admin = integrationTestUtils.createAdminAndLogin();
		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorCatererRead() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorCatererUpdate() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		Caterer caterer = integrationTestUtils.createCaterer();
		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer user = EntityGenerator.generateRandomCatererWithId();
		user.setId(caterer.getId());

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorCatererDelete() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserCatererCreate() throws IOException, EpickurException {
		String key = integrationTestUtils.createSuperUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserCatererRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		String key = integrationTestUtils.createSuperUserAndLogin().getKey();
		String id = integrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testSuperUserCatererUpdate() throws IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = integrationTestUtils.createCaterer();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);

	}

	@Test
	public void testSuperUserCatererUpdate2() throws IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(superUser.getId());

		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testSuperUserCatererDelete() throws IOException, EpickurException {
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Caterer id = integrationTestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	// User User
	@Test
	public void testUserCaterCreate() throws IOException, EpickurException {
		String key = integrationTestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserCatererRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		String key = integrationTestUtils.createUserAndLogin().getKey();
		String id = integrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testUserCatererUpdate() throws IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = integrationTestUtils.createCaterer();
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testUserCatererDelete() throws IOException, EpickurException {
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Caterer id = integrationTestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}
}
