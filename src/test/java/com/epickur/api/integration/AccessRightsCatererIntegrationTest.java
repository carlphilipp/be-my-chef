package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsCatererIntegrationTest {
	private static String END_POINT;
	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;
	private static String jsonMimeType;
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
		TestUtils.setupDB();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		TestUtils.cleanDB();
	}

	// User Administrator
	@Test
	public void testAdministratorCatererCreate() throws ClientProtocolException, IOException {
		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + API_KEY;

		Caterer caterer = TestUtils.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertFalse("Content error: " + jsonResult, jsonResult.has("error"));
		assertFalse("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testAdministratorCatererRead() throws ClientProtocolException, IOException, EpickurException {
		String id = TestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	@Test
	public void testAdministratorCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		Caterer caterer = TestUtils.createCaterer();
		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + API_KEY;

		Caterer user = TestUtils.generateRandomCatererWithId();
		user.setId(caterer.getId());

		StringEntity requestEntity = new StringEntity(user.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	@Test
	public void testAdministratorCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		String id = TestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + API_KEY;

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	// User Super_User
	@Test
	public void testSuperUserCaterCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = TestUtils.createSuperUser().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = TestUtils.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testSuperUserCatererRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		String key = TestUtils.createSuperUser().getKey();
		String id = TestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	@Test
	public void testSuperUserCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = TestUtils.createCaterer();
		User superUser = TestUtils.createSuperUser();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
	}

	@Test
	public void testSuperUserCatererUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = TestUtils.createSuperUser();
		Caterer caterer = TestUtils.createCatererWithUserId(superUser.getId());

		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	@Test
	public void testSuperUserCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = TestUtils.createSuperUser();
		Caterer id = TestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
	}

	// User User
	@Test
	public void testUserCaterCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = TestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = TestUtils.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPost request = new HttpPost(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
		assertTrue("Content error: " + jsonResult, jsonResult.has("error"));
		assertTrue("Content error: " + jsonResult, jsonResult.has("message"));
	}

	@Test
	public void testUserCatererRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		String key = TestUtils.createUserAndLogin().getKey();
		String id = TestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
		URL = URL_NO_KEY + "?key=" + key;

		HttpGet request = new HttpGet(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.OK.getStatusCode(), statusCode);
	}

	@Test
	public void testUserCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = TestUtils.createCaterer();
		User superUser = TestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toString());
		HttpPut request = new HttpPut(URL);
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
	}

	@Test
	public void testUserCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = TestUtils.createSuperUser();
		Caterer id = TestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + superUser.getKey();

		HttpDelete request = new HttpDelete(URL);

		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + obj, Response.Status.FORBIDDEN.getStatusCode(), statusCode);
	}
}
