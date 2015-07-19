package com.epickur.api.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.epickur.api.TestUtils;
import com.epickur.api.entity.Address;
import com.epickur.api.entity.Caterer;
import com.epickur.api.entity.Dish;
import com.epickur.api.entity.Geo;
import com.epickur.api.entity.Key;
import com.epickur.api.entity.Location;
import com.epickur.api.entity.NutritionFact;
import com.epickur.api.entity.User;
import com.epickur.api.exception.EpickurException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DishIntegrationTest {
	private static String URL;
	private static String URL_NO_KEY;
	private static String API_KEY;
	private static List<ObjectId> idsCatererToDelete;
	private static ContainerRequestContext context;
	private static String jsonMimeType = "application/json";

	@BeforeClass
	public static void beforeClass() throws IOException, EpickurException {
		TestUtils.setupDB();
		InputStreamReader in = new InputStreamReader(UserIntegrationTest.class.getClass().getResourceAsStream("/test.properties"));
		Properties prop = new Properties();
		prop.load(in);
		in.close();
		String address = prop.getProperty("address");
		String path = prop.getProperty("api.path");
		URL_NO_KEY = address + path + "/dishes";

		User admin = TestUtils.createAdminAndLogin();
		API_KEY = admin.getKey();
		URL = URL_NO_KEY + "?key=" + API_KEY;

		idsCatererToDelete = new ArrayList<ObjectId>();
		context = mock(ContainerRequestContext.class);
		Key key = TestUtils.generateRandomKey();
		Mockito.when(context.getProperty("key")).thenReturn(key);
	}

	@AfterClass
	public static void afterClass() throws EpickurException, IOException {
		TestUtils.cleanDB();
	}

	@Test
	public void testUnauthorized() throws ClientProtocolException, IOException {
		// Given
		HttpUriRequest request = new HttpGet(URL_NO_KEY);

		// When
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		// Then
		assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
	}

	@Test
	public void testCreate() throws ClientProtocolException, IOException, EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());

		// Create
		ObjectMapper mapper = new ObjectMapper();
		HttpPost request = new HttpPost(URL);
		// String asText = json.toString();
		StringEntity requestEntity = new StringEntity(dish.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readValue(obj, JsonNode.class);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Create result
		assertEquals(dish.getName(), jsonResult.get("name").asText());
		assertEquals(dish.getDescription(), jsonResult.get("description").asText());
		assertEquals(dish.getType().toString(), jsonResult.get("type").asText());
		assertEquals(new Long(dish.getPrice()), jsonResult.get("price").asLong(), 0.001);
		assertEquals(new Long(dish.getCookingTime()).longValue(), jsonResult.get("cookingTime").asLong());
		assertEquals(new Long(dish.getDifficultyLevel()).longValue(), jsonResult.get("difficultyLevel").asLong());
		assertEquals(dish.getVideoUrl(), jsonResult.get("videoUrl").asText());
		Caterer catererRes = TestUtils.getCaererObject(jsonResult.get("caterer").toString());
		assertEquals(cat, catererRes);
		List<NutritionFact> nutritionFactsRes = TestUtils.getListObject(jsonResult.get("nutritionFacts").toString());
		for (int i = 0; i < nutritionFactsRes.size(); i++) {
			assertEquals(dish.getNutritionFacts().size(), nutritionFactsRes.size());
			assertEquals(dish.getNutritionFacts().get(0).getName(), nutritionFactsRes.get(0).getName());
			assertEquals(dish.getNutritionFacts().get(0).getValue().doubleValue(), nutritionFactsRes.get(0).getValue().doubleValue(), 0.01);
			assertEquals(dish.getNutritionFacts().get(0).getUnit().getSymbol(), nutritionFactsRes.get(0).getUnit().getSymbol());
		}

		String id = jsonResult.get("id").asText();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);

	}

	@Test
	public void testReadOneDish() throws ClientProtocolException, IOException, EpickurException {

		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());

		// Create
		ObjectMapper mapper = new ObjectMapper();

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(dish.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		String id = jsonResult.get("id").asText();

		// Read
		HttpUriRequest request2 = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);

		// Read request
		httpResponse = HttpClientBuilder.create().build().execute(request2);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();

		// Read result
		assertEquals(Response.Status.OK.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		jsonResult = mapper.readTree(obj);
		in.close();
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);

		assertEquals(dish.getName(), jsonResult.get("name").asText());
		assertEquals(dish.getDescription(), jsonResult.get("description").asText());
		assertEquals(dish.getType().toString(), jsonResult.get("type").asText());
		assertEquals(new Long(dish.getPrice()), jsonResult.get("price").asLong(), 0.001);
		assertEquals(new Long(dish.getCookingTime()).longValue(), jsonResult.get("cookingTime").asLong());
		assertEquals(new Long(dish.getDifficultyLevel()).longValue(), jsonResult.get("difficultyLevel").asLong());
		assertEquals(dish.getVideoUrl(), jsonResult.get("videoUrl").asText());
		Caterer catererRes = TestUtils.getCaererObject(jsonResult.get("caterer").toString());
		assertEquals(cat, catererRes);
		List<NutritionFact> nutritionFactsRes = TestUtils.getListObject(jsonResult.get("nutritionFacts").toString());
		for (int i = 0; i < nutritionFactsRes.size(); i++) {
			assertEquals(dish.getNutritionFacts().size(), nutritionFactsRes.size());
			assertEquals(dish.getNutritionFacts().get(0).getName(), nutritionFactsRes.get(0).getName());
			assertEquals(dish.getNutritionFacts().get(0).getValue().doubleValue(), nutritionFactsRes.get(0).getValue().doubleValue(), 0.01);
			assertEquals(dish.getNutritionFacts().get(0).getUnit().getSymbol(), nutritionFactsRes.get(0).getUnit().getSymbol());
		}

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);
	}

	@Test
	public void testUpdateOneDish() throws ClientProtocolException, IOException, EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());

		// Create
		ObjectMapper mapper = new ObjectMapper();

		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(dish.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		String id = jsonResult.get("id").asText();

		// Put
		String namePut = "new name";
		String descriptionPut = "new descr";
		String type = TestUtils.generateRandomDishType().toString();
		Integer pricePut = 505;
		int cookingTimePut = 50;
		int difficultyLevelPut = 2;
		String videoURLPut = "http://www.yahoo.com";
		Caterer caterer2 = new Caterer();
		String catererName2 = RandomStringUtils.randomAlphabetic(10);
		caterer2.setName(catererName2);
		Location location2 = new Location();
		Address address2 = new Address();
		address2.setHouseNumber("105");
		address2.setLabel("derp address 2");
		address2.setState("NY");
		address2.setStreet("Wall Street");
		location2.setAddress(address2);
		Geo geo2 = new Geo();
		geo2.setLatitude(41.9254048);
		geo2.setLongitude(-87.6482463);
		location2.setGeo(geo2);
		caterer2.setLocation(location2);

		ObjectNode json = mapper.createObjectNode();
		json.put("id", id);
		json.put("name", namePut);
		json.put("description", descriptionPut);
		json.put("type", type);
		json.put("price", pricePut);
		json.put("cookingTime", cookingTimePut);
		json.put("difficultyLevel", difficultyLevelPut);
		json.put("videoUrl", videoURLPut);
		json.set("caterer", mapper.readTree(caterer2.toStringAPIView()));
		requestEntity = new StringEntity(json.toString());

		HttpPut putRequest = new HttpPut(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		putRequest.addHeader("content-type", jsonMimeType);
		putRequest.setEntity(requestEntity);

		// Put request
		httpResponse = HttpClientBuilder.create().build().execute(putRequest);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		jsonResult = mapper.readTree(obj);
		in.close();

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertEquals(id, jsonResult.get("id").asText());
		assertEquals(descriptionPut, jsonResult.get("description").asText());
		assertEquals(namePut, jsonResult.get("name").asText());
		assertEquals(descriptionPut, jsonResult.get("description").asText());
		assertEquals(type, jsonResult.get("type").asText());
		assertEquals(new Long(pricePut), jsonResult.get("price").asLong(), 0.001);
		assertEquals(new Long(cookingTimePut).longValue(), jsonResult.get("cookingTime").asLong(), 0.001);
		assertEquals(new Long(difficultyLevelPut).longValue(), jsonResult.get("difficultyLevel").asLong(), 0.001);
		assertEquals(videoURLPut, jsonResult.get("videoUrl").asText());
		Caterer caterer3 = TestUtils.getCaererObject(jsonResult.get("caterer").toString());
		assertEquals(cat.getId(), caterer3.getId());
		assertEquals(caterer2.getName(), caterer3.getName());
		assertEquals(cat.getLocation().getAddress().getCity(), caterer3.getLocation().getAddress().getCity());

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		HttpClientBuilder.create().build().execute(requestDelete);
	}

	@Test
	public void testDeleteOneDish() throws ClientProtocolException, IOException, EpickurException {
		Dish dish = TestUtils.generateRandomDish();
		dish.getCaterer().setId(null);
		Caterer cat = TestUtils.createCaterer(dish.getCaterer(), null);
		dish.setCaterer(cat);
		idsCatererToDelete.add(cat.getId());
		// Create
		ObjectMapper mapper = new ObjectMapper();
		HttpPost request = new HttpPost(URL);
		StringEntity requestEntity = new StringEntity(dish.toString());
		request.addHeader("content-type", jsonMimeType);
		request.setEntity(requestEntity);

		// Create request
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals(Response.Status.OK.getStatusCode(), statusCode);
		JsonNode jsonResult = mapper.readTree(obj);

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		String id = jsonResult.get("id").asText();

		// Delete this user
		HttpDelete requestDelete = new HttpDelete(URL_NO_KEY + "/" + id + "?key=" + API_KEY);
		request.addHeader("content-type", jsonMimeType);
		httpResponse = HttpClientBuilder.create().build().execute(requestDelete);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();
		in.close();
		int statusCode2 = httpResponse.getStatusLine().getStatusCode();
		assertEquals(Response.Status.OK.getStatusCode(), statusCode2);
		jsonResult = mapper.readTree(obj);
		String mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		assertEquals(jsonMimeType, mimeType);
		assertEquals(id, jsonResult.get("id").asText());
		assertEquals(true, Boolean.valueOf(jsonResult.get("deleted").toString()));

		// Read
		HttpUriRequest request2 = new HttpGet(URL_NO_KEY + "/" + id + "?key=" + API_KEY);

		// Read request
		httpResponse = HttpClientBuilder.create().build().execute(request2);
		in = new InputStreamReader(httpResponse.getEntity().getContent());
		br = new BufferedReader(in);
		obj = br.readLine();

		assertFalse("Failed request: " + obj, jsonResult.has("error"));

		// Read result
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), httpResponse.getStatusLine().getStatusCode());

		jsonResult = mapper.readTree(obj);
		in.close();
		mimeType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		assertEquals(jsonMimeType, mimeType);
		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), Integer.valueOf(jsonResult.get("error").toString()).intValue());
		assertEquals(Response.Status.NOT_FOUND.getReasonPhrase(), jsonResult.get("message").asText());
	}

	// Search tests

	@Test
	public void testSearchUsa() throws ClientProtocolException, IOException {
		String type = "Meat";
		String limit = "100";
		String address = "832 W. Wrightwood, Chicago, Illinois";
		String pickupdate = "mon-19:00";
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext="
				+ URLEncoder.encode(address, "UTF-8"));
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
	public void testSearchUsa2() throws ClientProtocolException, IOException {
		// Same test with another pickupdate
		String type = "Meat";
		String limit = "100";
		String address = "832 W. Wrightwood, Chicago, Illinois";
		String pickupdate = "mon-16:00";
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext="
				+ URLEncoder.encode(address, "UTF-8"));
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		assertEquals("Wrong status code: " + statusCode + " with " + httpResponse.getEntity(), 200, statusCode);

		InputStreamReader in = new InputStreamReader(httpResponse.getEntity().getContent());
		BufferedReader br = new BufferedReader(in);
		String obj = br.readLine();
		in.close();

		ObjectMapper mapper = new ObjectMapper();
		List<?> jsonResult = mapper.readValue(obj, List.class);
		Assert.assertEquals(0, jsonResult.size());
	}

	@Test
	public void testSearchUsa3() throws ClientProtocolException, IOException {
		String type = "Meat";
		String limit = "100";
		String address = "832 W. Wrightwood, Chicago, Illinois";
		String pickupdate = "mon-22:00";
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext="
				+ URLEncoder.encode(address, "UTF-8"));
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
		HttpGet request = new HttpGet(URL + "&pickupdate=" + pickupdate + "&types=" + type + "&limit=" + limit + "&searchtext="
				+ URLEncoder.encode(address, "UTF-8"));
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
