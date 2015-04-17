package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsDishIntegrationTest {
	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;
	private static String jsonMimeType;
	private static String mongoPath;
	private static String mongoAddress;
	private static String mongoPort;
	private static String mongoDbName;
	private static String scriptCleanPath;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void beforeClass() throws IOException {
		InputStreamReader in = new InputStreamReader(CatererIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		String address = prop.getProperty("address");
		String path = prop.getProperty("api.path");
		END_POINT = address + path;

		in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/api.key"));
		BufferedReader br = new BufferedReader(in);
		API_KEY = br.readLine();
		in.close();

		jsonMimeType = "application/json";
		mapper = new ObjectMapper();

		mongoPath = prop.getProperty("mongo.path");
		mongoAddress = prop.getProperty("mongo.address");
		mongoPort = prop.getProperty("mongo.port");
		mongoDbName = prop.getProperty("mongo.db.name");
		scriptCleanPath = prop.getProperty("script.clean");
	}

	@AfterClass
	public static void afterClass() throws IOException {
		String cmd = mongoPath + " " + mongoAddress + ":" + mongoPort + "/" + mongoDbName + " " + scriptCleanPath;
		TestUtils.runShellCommand(cmd);
	}

	// User Administrator
	@Test
	public void testAdministratorDishCreate() throws ClientProtocolException, IOException, EpickurException {
		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + API_KEY;

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = TestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorDishRead() throws ClientProtocolException, IOException, EpickurException {
		String id = TestUtils.getIdNewDish();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testAdministratorDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		Dish dish = TestUtils.getDish();
		String id = dish.getId().toHexString();
		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY;

		Caterer caterer = TestUtils.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testAdministratorCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		Dish dish = TestUtils.getDish();
		String id = dish.getId().toHexString();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY;

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserDishCreate() throws ClientProtocolException, IOException, EpickurException {
		User user = TestUtils.getSuperUser();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = TestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDishCreate2() throws ClientProtocolException, IOException, EpickurException {
		User user = TestUtils.getSuperUser();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = TestUtils.createCaterer(dish.getCaterer(), user.getId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDishRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		User user = TestUtils.getSuperUser();
		String key = user.getKey();

		Dish dish = TestUtils.getDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testSuperUserDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = TestUtils.getDish();
		User superUser = TestUtils.getSuperUser();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperUserDishUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = TestUtils.getSuperUser();
		Dish dish = TestUtils.getDishUserId(superUser.getId().toHexString());
		String key = superUser.getKey();
		Caterer caterer = TestUtils.getCatererWithUserId(superUser.getId().toHexString());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testSuperUserDishDelete() throws ClientProtocolException, IOException, EpickurException {
		// Delete a dish not created by current user - should not pass it
		Dish dish = TestUtils.getDish();
		User superUser = TestUtils.getSuperUser();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new
				InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperUserDishDelete2() throws ClientProtocolException, IOException, EpickurException {
		// Delete a caterer created by current user - should pass it
		User superUser = TestUtils.getSuperUser();
		Dish dish = TestUtils.getDishUserId(superUser.getId().toHexString());
		String key = superUser.getKey();
		Caterer caterer = TestUtils.getCatererWithUserId(superUser.getId().toHexString());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new
				InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	// User User
	@Test
	public void testUserDishCreate() throws ClientProtocolException, IOException, EpickurException {
		User user = TestUtils.createUser();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = TestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDishCreate2() throws ClientProtocolException, IOException, EpickurException {
		User user = TestUtils.createUser();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = TestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);
		dish.setCreatedBy(user.getId());

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDishRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		User user = TestUtils.createUser();
		String key = user.getKey();

		Dish dish = TestUtils.getDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 200, statusCode);
	}

	@Test
	public void testUserDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = TestUtils.getDish();
		User superUser = TestUtils.createUser();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperDishUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User user = TestUtils.createUser();
		Dish dish = TestUtils.getDishUserId(user.getId().toHexString());
		String key = user.getKey();
		Caterer caterer = TestUtils.getCatererWithUserId(user.getId().toHexString());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testUserDishDelete() throws ClientProtocolException, IOException, EpickurException {
		// Delete a dish - should not pass it
		Dish dish = TestUtils.getDish();
		User user = TestUtils.createUser();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

}
