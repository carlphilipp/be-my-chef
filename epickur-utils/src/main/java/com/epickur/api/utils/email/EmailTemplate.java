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

	static {
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
	 * @param user
	 *            The user.
	 * @param code
	 *            The code.
	 * @return The map.
	 */
	public static Map<String, String> convertToDataNewRegistrationUser(final User user, String code) {
		Map<String, String> data = getData(user, null);
		data.put("@@FOLDER@@", Info.FOLDER);
		data.put("@@CHECK@@", code);
		return data;
	}

	/**
	 * @param user
	 *            The user.
	 * @return A map
	 */
	public static Map<String, String> convertToDataNewRegistrationAdmins(final User user) {
		return getData(user, null);
	}

	// ORDER: case 1 - New order
	/**
	 * Convert data to order user
	 * 
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataNewOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * Convert data to Order Caterer
	 * 
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @param orderCode
	 *            The order code
	 * @return A Map
	 */
	public static Map<String, String> convertToDataNewOrderCaterer(final User user, final Order order, final String orderCode) {
		Map<String, String> data = getData(user, order);
		data.put("@@ORDER_CODE@@", orderCode);
		return data;
	}

	/**
	 * Convert data to Order admins
	 * 
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataNewOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 2 - Caterer declined the order
	/**
	 * Convert data for decline order user.
	 * 
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataDeclineOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * Convert data for decline order admins.
	 * 
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataDeclineOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 3 - The order is a success
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataSuccessOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataSuccessOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataSuccessOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 4 - The order has been accepted but the payment failed
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataFailOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataFailOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataFailOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	// ORDER: case 5 - The order has been received by the Caterer, but he did not answer it on time.
	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataCancelOrderUser(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataCancelOrderCaterer(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user
	 * @param order
	 *            The order
	 * @return A Map
	 */
	public static Map<String, String> convertToDataCancelOrderAdmins(final User user, final Order order) {
		return getData(user, order);
	}

	/**
	 * @param user
	 *            The user.
	 * @param resetCode
	 *            The code.
	 * @return The map.
	 */
	public static Map<String, String> convertToDataResetUserPassword(final User user, final String resetCode) {
		Map<String, String> data = getData(user, null);
		data.put("@@RESET_CODE@@", resetCode);
		return data;
	}

	private static Map<String, String> getData(final User user, final Order order) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("@@TEAM_NAME@@", Info.NAME);
		data.put("@@WEB_ADDRESS@@", Info.WEB_ADDRESS);
		data.put("@@DELAY@@", delay);

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
