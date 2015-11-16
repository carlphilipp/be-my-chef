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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.IntegrationTestUtils;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.helper.EntityGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessRightsCatererIT {
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
	public void testAdministratorCatererCreate() throws EpickurException, ClientProtocolException, IOException {

		User admin = IntegrationTestUtils.createAdminAndLogin();
		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testAdministratorCatererRead() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		String id = IntegrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
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
	public void testAdministratorCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		Caterer caterer = IntegrationTestUtils.createCaterer();
		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + admin.getKey();

		Caterer user = EntityGenerator.generateRandomCatererWithId();
		user.setId(caterer.getId());

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
	public void testAdministratorCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User admin = IntegrationTestUtils.createAdminAndLogin();
		String id = IntegrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
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
	public void testSuperUserCaterCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = IntegrationTestUtils.createSuperUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testSuperUserCatererRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		String key = IntegrationTestUtils.createSuperUserAndLogin().getKey();
		String id = IntegrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
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
	public void testSuperUserCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = IntegrationTestUtils.createCaterer();
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testSuperUserCatererUpdate2() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer created by current user - should pass it
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		Caterer caterer = IntegrationTestUtils.createCatererWithUserId(superUser.getId());

		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testSuperUserCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		Caterer id = IntegrationTestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
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
	public void testUserCaterCreate() throws ClientProtocolException, IOException, EpickurException {
		String key = IntegrationTestUtils.createUserAndLogin().getKey();

		URL_NO_KEY = END_POINT + "/caterers";
		URL = URL_NO_KEY + "?key=" + key;

		Caterer caterer = EntityGenerator.generateRandomCatererWithId();

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testUserCatererRead() throws ClientProtocolException, IOException, EpickurException {
		// Read another caterer - should pass it
		String key = IntegrationTestUtils.createUserAndLogin().getKey();
		String id = IntegrationTestUtils.createCaterer().getId().toHexString();

		URL_NO_KEY = END_POINT + "/caterers/" + id;
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
	public void testUserCatererUpdate() throws ClientProtocolException, IOException, EpickurException {
		// Update a caterer not created by current user - should not pass it
		Caterer caterer = IntegrationTestUtils.createCaterer();
		User superUser = IntegrationTestUtils.createUserAndLogin();
		String key = superUser.getKey();

		URL_NO_KEY = END_POINT + "/caterers/" + caterer.getId().toHexString();
		URL = URL_NO_KEY + "?key=" + key;

		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
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
	public void testUserCatererDelete() throws ClientProtocolException, IOException, EpickurException {
		User superUser = IntegrationTestUtils.createSuperUserAndLogin();
		Caterer id = IntegrationTestUtils.createCaterer();

		URL_NO_KEY = END_POINT + "/caterers/" + id.getId().toHexString();
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
