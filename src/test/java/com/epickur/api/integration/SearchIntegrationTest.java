package com.epickur.api.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SearchIntegrationTest {

	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;

	@BeforeClass
	public static void beforeClass() throws IOException {
		InputStreamReader in = new InputStreamReader(SearchIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		in.close();
		String address = prop.getProperty("address");
		String path = prop.getProperty("api.path");
		URL_NO_KEY = address + path + "/search";

		in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/api.key"));
		BufferedReader br = new BufferedReader(in);
		API_KEY = br.readLine();
		in.close();
		URL = URL_NO_KEY + "?key=" + API_KEY;
		TestUtils.setupDB();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		TestUtils.cleanDB();
	}

	@Test
	public void testUnauthorized() throws ClientProtocolException, IOException {
		// Given
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
	}

	@Test
	public void testSearchUsa() throws ClientProtocolException, IOException {
		String type = "Meat";
		String limit = "100";
		String address = "832 W. Wrightwood, Chicago, Illinois";
		String pickupdate = "mon-19:00";
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext=" + URLEncoder.encode(address, "UTF-8"));
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + httpResponse.getEntity(), 200, statusCode);
		
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode jsonResult = mapper.readValue(obj, ArrayNode.class);
		Assert.assertThat(jsonResult.size(), is(1));
	}

	@Test
	public void testSearchAustralia() throws ClientProtocolException, IOException {
		String type = "Fish,Meat";
		String limit = "100";
		String address = "388 Bourke St Melbourne, Australia";
		String pickupdate = "mon-19:00";
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext=" + URLEncoder.encode(address, "UTF-8"));
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + httpResponse.getEntity(), 200, statusCode);
		
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode jsonResult = mapper.readValue(obj, ArrayNode.class);
		Assert.assertThat(jsonResult.size(), is(2));
	}

}
