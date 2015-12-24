package com.epickur.api.integration;

import com.epickur.api.ApplicationConfigTest;
import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
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
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsDishIT {

	@Autowired
	private IntegrationTestUtils integrationTestUtils;

	private static final String ENDPOINT = "dishes";

	private static String PROTOCOL;
	private static String HOST;
	private static String PORT;
	private static String PATH;

//	private static String END_POINT;
//	private static String URL;
//	private static String URL_NO_KEY;
	private static final String jsonMimeType = "application/json";
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		@Cleanup InputStreamReader in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		PROTOCOL = prop.getProperty("protocol");
		HOST = prop.getProperty("host");
		PORT = prop.getProperty("port");
		PATH = prop.getProperty("api.path");
		mapper = new ObjectMapper();
		IntegrationTestUtils.setupDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		IntegrationTestUtils.cleanDB();
	}

	// User Administrator
	@Test
	public void testAdministratorDishCreate() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", admin.getKey())
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorDishRead() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createDish().getId().toHexString();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();

		HttpGet request = new HttpGet(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorDishUpdate() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		Dish dish = integrationTestUtils.createDish();
		String id = dish.getId().toHexString();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testAdministratorCatererDelete() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		Dish dish = integrationTestUtils.createDish();
		String id = dish.getId().toHexString();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", admin.getKey())
				.build()
				.expand(id)
				.encode();
		URI uri = uriComponents.toUri();

		HttpDelete request = new HttpDelete(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserDishCreate() throws IOException, EpickurException {
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDishCreate2() throws IOException, EpickurException {
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), user.getId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDishRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		Dish dish = integrationTestUtils.createDish();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		HttpGet request = new HttpGet(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testSuperUserDishUpdate() throws IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testSuperUserDishUpdate2() throws IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Dish dish = integrationTestUtils.createDishWithUserId(superUser.getId());
		String key = superUser.getKey();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(superUser.getId());
		dish.setCaterer(caterer);

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testSuperUserDishDelete() throws IOException, EpickurException {
		// Delete a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		HttpDelete request = new HttpDelete(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testSuperUserDishDelete2() throws IOException, EpickurException {
		// Delete a caterer created by current user - should pass it
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Dish dish = integrationTestUtils.createDishWithUserId(superUser.getId());
		String key = superUser.getKey();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(superUser.getId());
		dish.setCaterer(caterer);

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		HttpDelete request = new HttpDelete(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	// User User
	@Test
	public void testUserDishCreate() throws IOException, EpickurException {
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDishCreate2() throws IOException, EpickurException {
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT)
				.queryParam("key", key)
				.build()
				.encode();
		URI uri = uriComponents.toUri();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);
		dish.setCreatedBy(user.getId());

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDishRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		Dish dish = integrationTestUtils.createDish();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		HttpGet request = new HttpGet(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	@Test
	public void testUserDishUpdate() throws IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testSuperDishUpdate2() throws IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User user = integrationTestUtils.createUserAndLogin();
		Dish dish = integrationTestUtils.createDishWithUserId(user.getId());
		String key = user.getKey();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(user.getId());
		dish.setCaterer(caterer);

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(uri);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}

	@Test
	public void testUserDishDelete() throws IOException, EpickurException {
		// Delete a dish - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme(PROTOCOL).host(HOST).port(PORT).pathSegment(PATH, ENDPOINT, "{id}")
				.queryParam("key", key)
				.build()
				.expand(dish.getId().toHexString())
				.encode();
		URI uri = uriComponents.toUri();

		HttpDelete request = new HttpDelete(uri);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		String obj = integrationTestUtils.readResult(httpResponse);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.FORBIDDEN.value(), statusCode);
	}
}
