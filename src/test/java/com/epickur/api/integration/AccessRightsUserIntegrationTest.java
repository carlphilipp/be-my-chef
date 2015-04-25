package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsUserIntegrationTest {
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
	public void testAdministratorUserCreate() throws ClientProtocolException, IOException {
		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + API_KEY;

		User user = TestUtils.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.addHeader("validate-agent", "false");
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
	public void testAdministratorUserRead() throws ClientProtocolException, IOException, EpickurException {
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testAdministratorUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		User normalUser = TestUtils.createUserAndLogin();
		URL_NO_KEY = END_POINT + "/users/" + normalUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY;

		User user = TestUtils.generateRandomUser();
		user.setId(normalUser.getId());

		StringEntity requestEntity = new StringEntity(user.toString());
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
	public void testAdministratorUserDelete() throws ClientProtocolException, IOException, EpickurException {
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testSuperUserCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = TestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.addHeader("validate-agent", "false");
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
	public void testSuperUserRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another user - should not pass it
		String key = TestUtils.createUserAndLogin().getKey();
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

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
	public void testSuperUserRead2() throws ClientProtocolException, IOException, EpickurException {
		// Read its own user - should pass it
		User newUser = TestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User superUser = TestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();
		user.setId(superUser.getId());

		StringEntity requestEntity = new StringEntity(user.toString());
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

		assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testSuperUserUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User superUser = TestUtils.createUserAndLogin();
		String key = superUser.getKey();
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId(id));

		StringEntity requestEntity = new StringEntity(user.toString());
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
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = TestUtils.createUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

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

	// User User
	@Test
	public void testUserCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = TestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("email-agent", "false");
		request.addHeader("validate-agent", "false");
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
	public void testUserRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another user - should not pass it
		String key = TestUtils.createUserAndLogin().getKey();
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

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
	public void testUserRead2() throws ClientProtocolException, IOException, EpickurException {
		// Read its own user - should pass it
		User newUser = TestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		in.close();

		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User normalUser = TestUtils.createUserAndLogin();
		String key = normalUser.getKey();

		URL_NO_KEY = END_POINT + "/users/" + normalUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();
		user.setId(normalUser.getId());

		StringEntity requestEntity = new StringEntity(user.toString());
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

		assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
	}

	@Test
	public void testUserUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User normalUser = TestUtils.createUserAndLogin();
		String key = normalUser.getKey();
		String id = TestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		User user = TestUtils.generateRandomUser();
		user.setId(new ObjectId(id));

		StringEntity requestEntity = new StringEntity(user.toString());
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
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = TestUtils.createUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

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
