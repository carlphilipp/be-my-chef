package com.epickur.api.utils.email;

import com.epickur.api.config.EpickurProperties;
import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author cph
 */
@Slf4j
@Component
public class EmailTemplate {

	@Autowired
	public EpickurProperties properties;
	@Autowired
	private Utils utils;

	private Map<String, Map<String, String>> templates;

	/**
	 * The constructor
	 */
	public EmailTemplate() {
		this.templates = new HashMap<>();
	}

	/**
	 * Load template into HashMap
	 */
	@PostConstruct
	private void loadTemplates() {
		final String base = getBaseTemplate();
		try {
			final Charset charset = Charset.forName("UTF-8");
			@Cleanup InputStream is = utils.getResource("email-template.json");
			@Cleanup Reader in = new InputStreamReader(is, charset);
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode obj = mapper.readTree(in);
			final Iterator<Entry<String, JsonNode>> iterator = obj.fields();
			while (iterator.hasNext()) {
				final Entry<String, JsonNode> entry = iterator.next();
				final JsonNode node = entry.getValue();
				final String subject = node.get("subject").asText();
				final String folder = node.get("folder").asText();
				final String file = node.get("file").asText();
				@Cleanup InputStream is2 = utils.getResource("templates/" + folder + "/" + file);
				final String content = IOUtils.toString(is2);
				final String newContent = StringUtils.replace(base, "@@CONTENT@@", content);
				final Map<String, String> res = new HashMap<>();
				res.put("subject", subject);
				res.put("content", newContent);
				templates.put(entry.getKey(), res);
			}
		} catch (IOException e) {
			log.error("Error while trying to access the email templates", e);
		}
	}

	/**
	 * Load in memory the base template.
	 *
	 * @return The base template.
	 */
	private String getBaseTemplate() {
		String base = null;
		try {
			final Charset charset = Charset.forName("UTF-8");
			@Cleanup InputStream is = utils.getResource("templates/base.html");
			@Cleanup Reader in = new InputStreamReader(is, charset);
			base = IOUtils.toString(in);
		} catch (IOException e) {
			log.error("Error while trying to access the base template", e);
		}
		return base;
	}

	/**
	 * Get a template from map
	 *
	 * @param type The type of template
	 * @return A map containing the data of the template
	 */
	public Map<String, String> getTemplate(final EmailType type) {
		final String typeStr = type.toString().toLowerCase();
		if (this.templates.containsKey(typeStr)) {
			return this.templates.get(typeStr);
		} else {
			return new HashMap<>();
		}
	}

	// Registration

	/**
	 * @param user The user.
	 * @param code The code.
	 * @return The map.
	 */
	public Map<String, String> convertToDataNewRegistrationUser(final User user, String code) {
		final Map<String, String> data = getData(user, null);
		data.put("@@FOLDER@@", properties.getFolder());
		data.put("@@CHECK@@", code);
		return data;
	}

	/**
	 * @param user The user.
	 * @return A map
	 */
	public Map<String, String> convertToDataNewRegistrationAdmins(final User user) {
		return getData(user, null);
	}

	// ORDER: case 1 - New order

	/**
	 * Convert data to order user
	 *
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataNewOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * Convert data to Order Caterer
	 *
	 * @param user      The user
	 * @param order     The order
	 * @param orderCode The order code
	 * @return A Map
	 */
	public Map<String, String> convertToDataNewOrderCaterer(final User user, final Order order, final String orderCode) {
		Map<String, String> data = getData(user, order);
		data.put("@@ORDER_CODE@@", orderCode);
		return data;
	}

	/**
	 * Convert data to Order admins
	 *
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataNewOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 2 - Caterer declined the order

	/**
	 * Convert data for decline order user.
	 *
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataDeclineOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * Convert data for decline order admins.
	 *
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataDeclineOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 3 - The order is a success

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataSuccessOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataSuccessOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataSuccessOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 4 - The order has been accepted but the payment failed

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataFailOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataFailOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataFailOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataCancelOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataCancelOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user  The user
	 * @param order The order
	 * @return A Map
	 */
	public Map<String, String> convertToDataCancelOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user      The user.
	 * @param resetCode The code.
	 * @return The map.
	 */
	public Map<String, String> convertToDataResetUserPassword(final User user, final String resetCode) {
		Map<String, String> data = getData(user, null);
		data.put("@@RESET_CODE@@", resetCode);
		return data;
	}

	private Map<String, String> getData(final User user, final Order order) {
		final Map<String, String> data = new HashMap<>();
		data.put("@@TEAM_NAME@@", properties.getName());
		data.put("@@WEB_ADDRESS@@", properties.getWebAddress());
		data.put("@@DELAY@@", properties.getOrderTimeLimit().toString());

		data.put("@@USER_ID@@", user.getId().toHexString());
		data.put("@@USER_EMAIL@@", user.getEmail());
		data.put("@@USER_NAME@@", user.getName());
		data.put("@@USER_FIRST@@", user.getFirst());
		data.put("@@USER_LAST@@", user.getLast());

		if (order != null) {
			data.put("@@DISH_NAME@@", order.getDish().getName());

			data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
			data.put("@@CATERER_PHONE@@", order.getDish().getCaterer().getPhone());

			data.put("@@ORDER_ID@@", order.getId().toHexString());
			data.put("@@READABLE_ORDER_ID@@", order.getReadableId());
			data.put("@@ORDER_QUANTITY@@", order.getQuantity().toString());
			data.put("@@ORDER_AMOUNT@@", String.valueOf((order.getAmount() / 100.0)));
			data.put("@@ORDER_CURRENCY@@", order.getCurrency().getSymbol());
			data.put("@@ORDER_PICKUP_DATE@@", order.getPickupdate());
		}

		return data;
	}
}
