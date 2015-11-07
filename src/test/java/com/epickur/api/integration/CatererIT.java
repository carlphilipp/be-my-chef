package com.epickur.api.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Location;
import com.epickur.api.entity.User;
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.exception.EpickurException;
import com.epickur.api.exception.EpickurParsingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CatererIT {

	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;
	private static String id;
	private static String name;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException, EpickurException {
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(CatererIT.class.getClass().getResourceAsStream("/test.properties"));
			Properties prop = new Properties();
			prop.load(in);
			String address = prop.getProperty("address");
			String path = prop.getProperty("api.path");
			URL_NO_KEY = address + path + "/caterers";
		} finally {
			IOUtils.closeQuietly(in);
		}
		User admin = TestUtils.createAdminAndLogin();
		API_KEY = admin.getKey();
		URL = URL_NO_KEY + "?key=" + API_KEY;

		String jsonMimeType = "application/json";

		// Create
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode caterer = mapper.createObjectNode();
		ObjectNode location = mapper.createObjectNode();
		ObjectNode address2 = mapper.createObjectNode();
		ObjectNode geo = mapper.createObjectNode();
		ArrayNode coordinates = mapper.createArrayNode();
		name = RandomStringUtils.randomAlphabetic(10);
		Float[] coord = new Float[2];
		coord[0] = -73.97f;
		coord[1] = 40.77f;
		coordinates.add(coord[0]);
		coordinates.add(coord[1]);
		geo.set("coordinates", coordinates);
		address2.put("label", "carl");
		address2.put("houseNumber", "832");
		address2.put("street", "Wrightwood");
		address2.put("city", "Chicago");
		address2.put("postalCode", "60614");
		address2.put("state", "Illinois");
		address2.put("country", "USA");
		location.set("address", address2);
		location.set("geo", geo);
		caterer.put("name", name);
		caterer.set("location", location);
		caterer.put("description", "Caterer description");
		caterer.put("manager", "Manager name");
		caterer.put("email", "email@email.com");
		caterer.put("phone", "000011222");
		WorkingTimes workingTimes = TestUtils.generateRandomWorkingTimes();

		caterer.set("workingTimes", mapper.readTree(workingTimes.toStringAPIView()));

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(caterer.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			BufferedReader br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals(Response.Status.OK.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

			// Create result
			id = jsonResult.get("id").asText();
		} finally {
			IOUtils.closeQuietly(in);
		}

	}

	@AfterClass
	public static void tearDownAfterClass() throws ClientProtocolException, IOException {
		if (id != null) {
			String jsonMimeType = "application/json";
			// Delete
			HttpDelete request = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(request);
		}
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
	public void testCreate() throws ClientProtocolException, IOException, EpickurParsingException {
		String jsonMimeType = "application/json";

		// Create
		ObjectMapper mapper = new ObjectMapper();
		String name = RandomStringUtils.randomAlphabetic(10);
		Caterer caterer = new Caterer();
		caterer.setName(name);
		caterer.setDescription("Caterer description");
		caterer.setManager("Manager name");
		caterer.setEmail("email@email2.com");
		caterer.setPhone("000011222");
		Location location = new Location();
		Address address = new Address();
		address.setCity("Chicago");
		address.setCountry("USA");
		address.setHouseNumber("832");
		address.setLabel("carl");
		address.setPostalCode(60614);
		address.setState("Illinois");
		address.setStreet("Wrightwood");
		location.setAddress(address);
		Geo geo = new Geo();
		geo.setLongitude(-73.97);
		geo.setLatitude(40.77);
		location.setGeo(geo);
		caterer.setLocation(location);
		caterer.setWorkingTimes(TestUtils.generateRandomWorkingTimes());

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(caterer.toStringAPIView());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals(Response.Status.OK.getStatusCode(), statusCode);
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

			// Create result
			assertEquals(name, jsonResult.get("name").asText());
			assertNotNull(jsonResult.get("createdAt"));
			assertNotNull(jsonResult.get("updatedAt"));
			assertNotNull(jsonResult.get("location"));
			JsonNode locationRes = jsonResult.get("location");
			assertNotNull(locationRes.get("address"));
			JsonNode addressRes = locationRes.get("address");
			assertEquals("carl", addressRes.get("label").asText());
			assertEquals("832", addressRes.get("houseNumber").asText());
			assertEquals("Wrightwood", addressRes.get("street").asText());
			assertEquals("Chicago", addressRes.get("city").asText());
			assertEquals(new Long(60614).longValue(), addressRes.get("postalCode").longValue());
			assertEquals("Illinois", addressRes.get("state").asText());
			assertEquals("USA", addressRes.get("country").asText());
			assertNotNull(locationRes.get("geo"));
			JsonNode geoRes = locationRes.get("geo");
			assertEquals("Point", geoRes.get("type").asText());
			ArrayNode coordinatesArray = (ArrayNode) geoRes.get("coordinates");
			assertEquals(new Float(-73.97).doubleValue(), coordinatesArray.get(0).doubleValue(), 0.01);
			assertEquals(new Float(40.77).doubleValue(), coordinatesArray.get(1).doubleValue(), 0.01);

			String id = jsonResult.get("id").asText();
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			assertEquals(jsonMimeType, mimeType);

			// Delete this user
			HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
			request.addHeader("content-type", jsonMimeType);
			HttpClientBuilder.create().build().execute(requestDelete);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}

	@Test
	public void testReadOneCaterer() throws ClientProtocolException, IOException {
		// Read
		String jsonMimeType = "application/json";
		HttpUriRequest request = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);

		// Read request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Read result
		assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatusLine().getStatusCode());
		InputStreamReader in = null;
		BufferedReader br = null;
		try {
			in = new InputStreamReader(httpResponse.getEntity().getContent());
			br = new BufferedReader(in);
			String obj = br.readLine();
			
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			assertEquals(Response.Status.OK.getStatusCode(), statusCode);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);
			String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
			assertEquals(jsonMimeType, mimeType);

			assertEquals(name, jsonResult.get("name").asText());
			assertNotNull(jsonResult.get("createdAt"));
			assertNotNull(jsonResult.get("updatedAt"));
			assertNotNull(jsonResult.get("location"));
			JsonNode locationRes = jsonResult.get("location");
			assertNotNull(locationRes.get("address"));
			JsonNode addressRes = locationRes.get("address");
			assertEquals("carl", addressRes.get("label").asText());
			assertEquals("832", addressRes.get("houseNumber").asText());
			assertEquals("Wrightwood", addressRes.get("street").asText());
			assertEquals("Chicago", addressRes.get("city").asText());
			assertEquals(new Long(60614).longValue(), addressRes.get("postalCode").longValue());
			assertEquals("Illinois", addressRes.get("state").asText());
			assertEquals("USA", addressRes.get("country").asText());
			assertNotNull(locationRes.get("geo"));
			JsonNode geoRes = locationRes.get("geo");
			assertEquals("Point", geoRes.get("type").asText());
			ArrayNode coordinates = (ArrayNode) geoRes.get("coordinates");
			assertEquals(new Float(-73.97).doubleValue(), coordinates.get(0).doubleValue(), 0.01);
			assertEquals(new Float(40.77).doubleValue(), coordinates.get(1).doubleValue(), 0.01);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(in);
		}
	}
}
