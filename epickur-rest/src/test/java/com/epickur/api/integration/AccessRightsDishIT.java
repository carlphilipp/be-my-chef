package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
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

import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsDishIT {
	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String jsonMimeType;
	private static ObjectMapper mapper;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			String address = prop.getProperty("address");
			String path = prop.getProperty("api.path");
			END_POINT = address + path;

			jsonMimeType = "application/json";
			mapper = new ObjectMapper();
			EntityGenerator.setupDB();
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		EntityGenerator.cleanDB();
	}

	// User Administrator
	@Test
	public void testAdministratorDishCreate() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = IntegrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorDishRead() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		String id = IntegrationTestUtils.createDish().getId().toHexString();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpGet request = new HttpGet(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		Dish dish = IntegrationTestUtils.createDish();
		String id = dish.getId().toHexString();
		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testAdministratorCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		Dish dish = IntegrationTestUtils.createDish();
		String id = dish.getId().toHexString();

		URL_NO_KEY = END_POINT + "/dishes/" + id;
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		HttpDelete request = new HttpDelete(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	// User Super_User
	@Test
	public void testSuperUserDishCreate() throws ClientProtocolException, IOException, EpickurException {
		User user = IntegrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = IntegrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
			assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishCreate2() throws ClientProtocolException, IOException, EpickurException {
		User user = IntegrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = IntegrationTestUtils.createCaterer(dish.getCaterer(), user.getId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		User user = IntegrationTestUtils.createSuperUserAndLogin();
		String key = user.getKey();

		Dish dish = IntegrationTestUtils.createDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = IntegrationTestUtils.createDish();
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		Dish dish = IntegrationTestUtils.createDishWithUserId(superUser.getId());
		String key = superUser.getKey();
		Caterer caterer = IntegrationTestUtils.createCatererWithUserId(superUser.getId());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishDelete() throws ClientProtocolException, IOException, EpickurException {
		// Delete a dish not created by current user - should not pass it
		Dish dish = IntegrationTestUtils.createDish();
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDishDelete2() throws ClientProtocolException, IOException, EpickurException {
		// Delete a caterer created by current user - should pass it
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		Dish dish = IntegrationTestUtils.createDishWithUserId(superUser.getId());
		String key = superUser.getKey();
		Caterer caterer = IntegrationTestUtils.createCatererWithUserId(superUser.getId());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	// User User
	@Test
	public void testUserDishCreate() throws ClientProtocolException, IOException, EpickurException {
		User user = IntegrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = IntegrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
			assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserDishCreate2() throws ClientProtocolException, IOException, EpickurException {
		User user = IntegrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes";
		URL = URL_NO_KEY + "?key=" + key;

		Dish dish = EntityGenerator.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer caterer = IntegrationTestUtils.createCaterer(dish.getCaterer(), new ObjectId());
		dish.setCaterer(caterer);
		dish.setCreatedBy(user.getId());

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
			assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserDishRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		User user = IntegrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		Dish dish = IntegrationTestUtils.createDish();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserDishUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a dish not created by current user - should not pass it
		Dish dish = IntegrationTestUtils.createDish();
		User superUser = IntegrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperDishUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User user = IntegrationTestUtils.createUserAndLogin();
		Dish dish = IntegrationTestUtils.createDishWithUserId(user.getId());
		String key = user.getKey();
		Caterer caterer = IntegrationTestUtils.createCatererWithUserId(user.getId());
		dish.setCaterer(caterer);

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(dish.toStringAPIView());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserDishDelete() throws ClientProtocolException, IOException, EpickurException {
		// Delete a dish - should not pass it
		Dish dish = IntegrationTestUtils.createDish();
		User user = IntegrationTestUtils.createUserAndLogin();
		String key = user.getKey();

		URL_NO_KEY = END_POINT + "/dishes/" + dish.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		HttpDelete request = new HttpDelete(URL);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}
}
