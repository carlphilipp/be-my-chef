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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfigTest.class)
public class AccessRightsDishIT {

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
	public void testAdministratorDishCreate() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testAdministratorDishRead() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		String id = integrationTestUtils.createDish().getId().toHexString();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
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
	public void testAdministratorDishUpdate() throws IOException, EpickurException {
		User admin = integrationTestUtils.createAdminAndLogin();
		Dish dish = integrationTestUtils.createDish();
		String id = dish.getId().toHexString();
		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
		Dish dish = integrationTestUtils.createDish();
		String id = dish.getId().toHexString();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
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
	public void testSuperUserDishCreate() throws IOException, EpickurException {
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testSuperUserDishCreate2() throws IOException, EpickurException {
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), user.getId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testSuperUserDishRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		User user = integrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		Dish dish = integrationTestUtils.createDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
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
	public void testSuperUserDishUpdate() throws IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testSuperUserDishUpdate2() throws IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		Dish dish = integrationTestUtils.createDishWithUserId(superUser.getId());
		String key = superUser.getKey();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(superUser.getId());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testSuperUserDishDelete() throws IOException, EpickurException {
		// Delete a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
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

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);
		request.addHeader("content-type", jsonMimeType);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		@Cleanup InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		@Cleanup BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, HttpStatus.OK.value(), statusCode);
	}

	// User User
	@Test
	public void testUserDishCreate() throws IOException, EpickurException {
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testUserDishCreate2() throws IOException, EpickurException {
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = integrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);
		dish.setCreatedBy(user.getId());

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testUserDishRead() throws IOException, EpickurException {
		// Read another caterer - should pass it
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		Dish dish = integrationTestUtils.createDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
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
	public void testUserDishUpdate() throws IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User superUser = integrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testSuperDishUpdate2() throws IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User user = integrationTestUtils.createUserAndLogin();
		Dish dish = integrationTestUtils.createDishWithUserId(user.getId());
		String key = user.getKey();
		Caterer caterer = integrationTestUtils.createCatererWithUserId(user.getId());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
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
	public void testUserDishDelete() throws IOException, EpickurException {
		// Delete a dish - should not pass it
		Dish dish = integrationTestUtils.createDish();
		User user = integrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

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
