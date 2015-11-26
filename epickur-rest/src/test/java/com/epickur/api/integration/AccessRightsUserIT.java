package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsUserIT {
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
	public void testAdministratorUserCreate() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();

		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		User user = EntityGenerator.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("validate-agent", "false");
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
	public void testAdministratorUserRead() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();

		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testAdministratorUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		User normalUser = IntegrationTestUtils.createUserAndLogin();
		URL_NO_KEY = END_POINT + "/users/" + normalUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		User user = EntityGenerator.generateRandomUser();
		user.setId(normalUser.getId());

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
	public void testAdministratorUserDelete() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testSuperUserCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = IntegrationTestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("validate-agent", "false");
		request.setEntity(requestEntity);
		BufferedReader br = null;
		InputStreamReader in = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
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
	public void testSuperUserRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another user - should not pass it
		String key = IntegrationTestUtils.createUserAndLogin().getKey();
		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testSuperUserRead2() throws ClientProtocolException, IOException, EpickurException {
		// Read its own user - should pass it
		User newUser = IntegrationTestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User superUser = IntegrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();
		user.setId(superUser.getId());

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

			assertNotEquals("Wrong status code: " + statusCode + " with " + jsonResult, 403, statusCode);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User superUser = IntegrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();
		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();
		user.setId(new ObjectId(id));

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
			assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testSuperUserDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = IntegrationTestUtils.createUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

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

	// User User
	@Test
	public void testUserCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = IntegrationTestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/users";
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.addHeader("validate-agent", "false");
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
	public void testUserRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another user - should not pass it
		String key = IntegrationTestUtils.createUserAndLogin().getKey();
		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
	public void testUserRead2() throws ClientProtocolException, IOException, EpickurException {
		// Read its own user - should pass it
		User newUser = IntegrationTestUtils.createUserAndLogin();
		String key = newUser.getKey();
		String id = newUser.getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
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
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
			assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User normalUser = IntegrationTestUtils.createUserAndLogin();
		String key = normalUser.getKey();

		URL_NO_KEY = END_POINT + "/users/" + normalUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();
		user.setId(normalUser.getId());

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
	public void testUserUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update another user - should not pass it
		User normalUser = IntegrationTestUtils.createUserAndLogin();
		String key = normalUser.getKey();
		String id = IntegrationTestUtils.createUser().getId().toHexString();

		URL_NO_KEY = END_POINT + "/users/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		User user = EntityGenerator.generateRandomUser();
		user.setId(new ObjectId(id));

		StringEntity requestEntity = new StringEntity(user.toStringAPIView());
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
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
			assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testUserDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = IntegrationTestUtils.createUserAndLogin();

		URL_NO_KEY = END_POINT + "/users/" + superUser.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

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