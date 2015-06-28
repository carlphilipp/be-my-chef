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
	 * @param userName
	 *            The user name
	 * @param code
	 *            The code
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewRegistrationUser(final String userName, final String code) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@NAME@@", userName);
		data.put("@@ADDRESS@@", Info.WEB_ADDRESS);
		data.put("@@FOLDER@@", Info.FOLDER);
		data.put("@@CHECK@@", code);
		return data;
	}

	/**
	 * Convert data to registration admins
	 * 
	 * @param userName
	 *            The user name
	 * @param email
	 *            The email
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewRegistrationAdmins(final String userName, final String email) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@NAME@@", userName);
		data.put("@@EMAIL@@", email);
		data.put("@@TEAM_NAME@@", Info.NAME);
		return data;
	}

	// ORDER: case 1 - New order
	/**
	 * Convert data to order user
	 * 
	 * @param userName
	 *            The User name
	 * @param orderId
	 *            The Order id
	 * @param dishName
	 *            The Dish name
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderUser(final String userName, final String orderId, final String dishName) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@NAME@@", userName);
		data.put("@@ORDER_ID@@", orderId);
		data.put("@@DISH_NAME@@", dishName);
		return data;
	}

	/**
	 * Convert data to Order Caterer
	 * 
	 * @param userName
	 *            The user name
	 * @param orderId
	 *            The Order Id
	 * @param dishName
	 *            The Dish name
	 * @param catererName
	 *            The Caterer Name
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderCaterer(final String userName, final String userId, final String orderId, final String dishName,
			final String catererName, final String orderCode) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@USER_NAME@@", userName);
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
	 * @param userName
	 *            The user name
	 * @param orderId
	 *            The Order id
	 * @param dishName
	 *            The Dish name
	 * @param catererName
	 *            The Caterer name
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewOrderAdmins(final String userName, final String orderId, final String dishName,
			final String catererName) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@USER_NAME@@", userName);
		data.put("@@ORDER_ID@@", orderId);
		data.put("@@DISH_NAME@@", dishName);
		data.put("@@CATERER_NAME@@", catererName);
		return data;
	}

	// ORDER: case 2 - Caterer declined the order
	/**
	 * Convert data for decline order user.
	 * 
	 * @return A map.
	 */
	public static Map<String, String> convertToDataDeclineOrderUser(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}

	/**
	 * Convert data for decline order admins.
	 * 
	 * @return A map.
	 */
	public static Map<String, String> convertToDataDeclineOrderAdmins(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}

	// ORDER: case 3 - The order is a success
	public static Map<String, String> convertToDataSuccessOrderUser(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataSuccessOrderCaterer(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataSuccessOrderAdmins(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	public static Map<String, String> convertToDataFailOrderUser(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataFailOrderCaterer(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataFailOrderAdmins(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	public static Map<String, String> convertToDataCancelOrderUser(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataCancelOrderCaterer(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
	public static Map<String, String> convertToDataCancelOrderAdmins(final String orderId) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@ORDER_ID@@", orderId);
		return data;
	}
	
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
