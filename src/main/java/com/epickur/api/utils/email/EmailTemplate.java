package com.epickur.api.utils.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.epickur.api.entity.Order;
import com.epickur.api.entity.User;
import com.epickur.api.utils.Info;
import com.epickur.api.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author cph
 *
 */
public final class EmailTemplate {

	/** The logger */
	private static final Logger LOG = LogManager.getLogger(EmailTemplate.class.getSimpleName());
	/** The templates */
	private Map<String, Map<String, String>> templates;
	/** The templates? */
	private static EmailTemplate emailTemplate = null;
	/** Delay max for a caterer to answer */
	private static String delay;
	
	static{
		delay = Utils.getEpickurProperties().getProperty("cron.order.timelimit");
	}

	/** The constructor */
	private EmailTemplate() {
		this.templates = new HashMap<String, Map<String, String>>();
		loadTemplates();
	}

	/**
	 * @return An EmailTemplate
	 */
	public static EmailTemplate getInstance() {
		if (emailTemplate == null) {
			emailTemplate = new EmailTemplate();
		}
		return emailTemplate;
	}

	/**
	 * Load template into HashMap
	 */
	private void loadTemplates() {
		String base = getBaseTemplate();
		InputStream is = null;
		InputStream is2 = null;
		Reader in = null;
		try {
			Charset charset = Charset.forName("UTF-8");
			is = Utils.getResource("email-template.json");
			in = new InputStreamReader(is, charset);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode obj = mapper.readTree(in);
			Iterator<Entry<String, JsonNode>> iterator = obj.fields();
			while (iterator.hasNext()) {
				Entry<String, JsonNode> entry = iterator.next();
				JsonNode node = entry.getValue();
				String subject = node.get("subject").asText();
				String folder = node.get("folder").asText();
				String file = node.get("file").asText();
				is2 = Utils.getResource("templates/" + folder + "/" + file);
				String content = IOUtils.toString(is2);
				String newContent = StringUtils.replace(base, "@@CONTENT@@", content);
				Map<String, String> res = new HashMap<String, String>();
				res.put("subject", subject);
				res.put("content", newContent);
				this.templates.put(entry.getKey(), res);
			}
		} catch (IOException e) {
			LOG.error("Error while trying to access the email templates", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(is2);
		}
	}

	/**
	 * Load in memory the base template.
	 * 
	 * @return The base template.
	 */
	private String getBaseTemplate() {
		String base = null;
		InputStream is = null;
		Reader in = null;
		try {
			Charset charset = Charset.forName("UTF-8");
			is = Utils.getResource("templates/base.html");
			in = new InputStreamReader(is, charset);
			base = IOUtils.toString(in);
		} catch (IOException e) {
			LOG.error("Error while trying to access the base template", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(in);
		}
		return base;
	}

	/**
	 * Get a template from map
	 * 
	 * @param type
	 *            The type of template
	 * @return A map containing the data of the template
	 */
	public Map<String, String> getTemplate(final EmailType type) {
		String typeStr = type.toString().toLowerCase();
		if (this.templates.containsKey(typeStr)) {
			return this.templates.get(typeStr);
		} else {
			return new HashMap<String, String>();
		}
	}

	// Registration
	/**
	 * Convert data to registration
	 * 
	 * @param name
	 *            The user name
	 * @param email
	 *            The user email
	 * @param code
	 *            The code
	 * @return The map
	 */
	public static Map<String, String> convertToDataNewRegistrationUser(final String name, final String first, final String email, final String code) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@NAME@@", name);
		data.put("@@FIRST@@", first);
		data.put("@@EMAIL@@", email);
		data.put("@@ADDRESS@@", Info.WEB_ADDRESS);
		data.put("@@FOLDER@@", Info.FOLDER);
		data.put("@@CHECK@@", code);
		return data;
	}

	/**
	 * Convert data to registration admins
	 * 
	 * @param name
	 *            The user name
	 * @param email
	 *            The email
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewRegistrationAdmins(final String name, final String email) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@NAME@@", name);
		data.put("@@EMAIL@@", email);
		data.put("@@TEAM_NAME@@", Info.NAME);
		return data;
	}

	// ORDER: case 1 - New order
	/**
	 * Convert data to order user
	 * 
	 * @param name
	 *            The User name
	 * @param orderId
	 *            The Order id
	 * @param dishName
	 *            The Dish name
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderUser(final String name, final String orderId, final String dishName) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@NAME@@", name);
		data.put("@@ORDER_ID@@", orderId);
		data.put("@@DISH_NAME@@", dishName);
		return data;
	}

	/**
	 * Convert data to Order Caterer
	 * 
	 * @param name
	 *            The user name
	 * @param userId
	 *            The user id
	 * @param orderId
	 *            The Order Id
	 * @param dishName
	 *            The Dish name
	 * @param catererName
	 *            The Caterer Name
	 * @param orderCode
	 *            the Order code
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderCaterer(final String name, final String userId, final String orderId,
			final String dishName, final String catererName, final String orderCode) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@USER_NAME@@", name);
		data.put("@@USER_ID@@", userId);
		data.put("@@ORDER_ID@@", orderId);
		data.put("@@DISH_NAME@@", dishName);
		data.put("@@CATERER_NAME@@", catererName);
		data.put("@@WEB_ADDRESS@@", Info.WEB_ADDRESS);
		data.put("@@ORDER_CODE@@", orderCode);
		return data;
	}

	/**
	 * Convert data to Order admins
	 * 
	 * @param name
	 *            The user name
	 * @param orderId
	 *            The Order id
	 * @param dishName
	 *            The Dish name
	 * @param catererName
	 *            The Caterer name
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderAdmins(final String name, final String orderId, final String dishName,
			final String catererName) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@USER_NAME@@", name);
		data.put("@@ORDER_ID@@", orderId);
		data.put("@@DISH_NAME@@", dishName);
		data.put("@@CATERER_NAME@@", catererName);
		return data;
	}

	// ORDER: case 2 - Caterer declined the order
	/**
	 * Convert data for decline order user.
	 * 
	 * @param orderId
	 *            The order id
	 * @return A map.
	 */
	public static Map<String, String> convertToDataDeclineOrderUser(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@FIRST@@", user.getFirst());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	/**
	 * Convert data for decline order admins.
	 * 
	 * @param orderId
	 *            The order id
	 * @return A map.
	 */
	public static Map<String, String> convertToDataDeclineOrderAdmins(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@USER_EMAIL@@", user.getEmail());
		data.put("@@ORDER_ID@@", order.getReadableId());
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		return data;
	}

	// ORDER: case 3 - The order is a success
	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataSuccessOrderUser(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@FIRST@@", user.getFirst());
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataSuccessOrderCaterer(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataSuccessOrderAdmins(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataFailOrderUser(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", order.getReadableId());
		data.put("@@USER_EMAIL@@", user.getEmail());
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataFailOrderCaterer(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", order.getReadableId());
		data.put("@@USER_EMAIL@@", user.getEmail());
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataFailOrderAdmins(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", order.getReadableId());
		data.put("@@USER_EMAIL@@", user.getEmail());
		return data;
	}

	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataCancelOrderUser(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@USER_EMAIL@@", user.getEmail());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataCancelOrderCaterer(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@USER_EMAIL@@", user.getEmail());
		data.put("@@ORDER_ID@@", order.getReadableId());
		data.put("@@DELAY@@", delay);
		return data;
	}

	/**
	 * @param orderId
	 *            The order id
	 * @return A map
	 */
	public static Map<String, String> convertToDataCancelOrderAdmins(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@CATERER_NAME@@", order.getDish().getCaterer().getName());
		data.put("@@USER_EMAIL@@", user.getEmail());
		data.put("@@ORDER_ID@@", order.getReadableId());
		return data;
	}

	/**
	 * @param email
	 *            The email
	 * @param userId
	 *            The user id
	 * @param resetCode
	 *            The reset code
	 * @return A map
	 */
	public static Map<String, String> convertToDataResetUserPassword(final String email, final String userId, final String resetCode) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@USER_EMAIL@@", email);
		data.put("@@RESET_CODE@@", resetCode);
		data.put("@@USER_ID@@", userId);
		data.put("@@WEB_ADDRESS@@", Info.WEB_ADDRESS);
		return data;
	}
}
