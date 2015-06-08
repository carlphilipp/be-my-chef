package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.databind.DateDeserializer;
import com.epickur.api.entity.databind.DateSerializer;
import com.epickur.api.entity.databind.ObjectIdDeserializer;
import com.epickur.api.entity.databind.ObjectIdSerializer;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Order entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "userId", "description", "amount", "currency", "pickupdate", "cardToken", "chargeId", "paid", "dish", "createdBy", "createdAt", "updatedAt" })
public final class Order extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Order.class.getSimpleName());
	/** Id **/
	private ObjectId id;
	/** Description **/
	private String description;
	/** Amount **/
	private Integer amount;
	/** Currency **/
	private Currency currency;
	/** Pickupdate **/
	private String pickupdate;
	/** Dish **/
	private Dish dish;
	/** Stripe Card Token **/
	private String cardToken;
	/** ChargeId from Stripe **/
	private String chargeId;
	/** Indicate if paid **/
	private Boolean paid;
	/** Owner id **/
	private ObjectId createdBy;
	/** Created at **/
	private DateTime createdAt;
	/** Updated at **/
	private DateTime updatedAt;

	/** Constructor **/
	public Order() {
	}

	/**
	 * @return The ObjectId
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            The ObjectId
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setId(final ObjectId id) {
		this.id = id;
	}

	/**
	 * @return The Dish
	 */
	public Dish getDish() {
		return dish;
	}

	/**
	 * @param dish
	 *            The Dish
	 */
	public void setDish(final Dish dish) {
		this.dish = dish;
	}

	/**
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return The amount
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            The amount
	 */
	public void setAmount(final Integer amount) {
		this.amount = amount;
	}

	/**
	 * @return The currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            The currency
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return The pickupdate.
	 */
	public String getPickupdate() {
		return pickupdate;
	}
	

	/**
	 * @param pickupdate The pickupdate.
	 */
	public void setPickupdate(final String pickupdate) {
		this.pickupdate = pickupdate;
	}

	/**
	 * @return The charge id
	 */
	public String getChargeId() {
		return chargeId;
	}

	/**
	 * @param chargeId
	 *            The charge id
	 */
	public void setChargeId(final String chargeId) {
		this.chargeId = chargeId;
	}

	/**
	 * @return If is paid
	 */
	public Boolean getPaid() {
		return paid;
	}

	/**
	 * @param paid
	 *            If is paid
	 */
	public void setPaid(final Boolean paid) {
		this.paid = paid;
	}

	/**
	 * @return The creation date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public DateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 *            The creation date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public void setCreatedAt(final DateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return The updated date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public DateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt
	 *            The updated date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public void setUpdatedAt(final DateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @return The user id that created the object
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return The Document
	 * @throws EpickurParsingException
	 *             If a parsing exception occured
	 */
	@JsonIgnore
	public Document getUpdateDocument() throws EpickurParsingException {
		String apiView = toStringAPIView();
		Document found = Document.parse(apiView);
		Document resultSet = new Document();
		Document result = new Document().append("$set", resultSet);
		Set<Entry<String, Object>> set = found.entrySet();
		Iterator<Entry<String, Object>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> en = iterator.next();
			String key = en.getKey();
			if (!key.equals("id") && !key.equals("dish")) {
				resultSet.put(key, found.get(key));
			}
			if (key.equals("dish")) {
				Document dishDBObject = new Document();
				resultSet.put("dish", dishDBObject);
				Document dishStr = (Document) found.get("dish");
				Set<Entry<String, Object>> setDish = dishStr.entrySet();
				Iterator<Entry<String, Object>> iteratorDish = setDish.iterator();
				while (iteratorDish.hasNext()) {
					Entry<String, Object> entry = iteratorDish.next();
					String key2 = entry.getKey();
					if (key2.equals("id")) {
						dishDBObject.put("_id", dishStr.get("id"));
					} else if (key2.equals("caterer")) {
						Document catererDBObject = new Document();
						dishDBObject.put("caterer", catererDBObject);
						Document caterer = (Document) dishStr.get("caterer");
						Set<Entry<String, Object>> setCaterer = caterer.entrySet();
						Iterator<Entry<String, Object>> iteratorCaterer = setCaterer.iterator();
						while (iteratorCaterer.hasNext()) {
							Entry<String, Object> entry2 = iteratorCaterer.next();
							String key3 = entry2.getKey();
							if (key3.equals("id")) {
								catererDBObject.put("_id", caterer.get("id"));
							} else {
								catererDBObject.put(key3, caterer.get(key3));
							}
						}
					} else {
						dishDBObject.put(key2, dishStr.get(key2));
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param obj
	 *            The DBObject
	 * @return An Order
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Order getObject(final Document obj) throws EpickurParsingException {
		return Order.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json to convert
	 * @return An Order
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static Order getObject(final String json) throws EpickurParsingException {
		Order user = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			user = mapper.readValue(json, Order.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Order: " + json, e);
		}
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((chargeId == null) ? 0 : chargeId.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((dish == null) ? 0 : dish.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((paid == null) ? 0 : paid.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		Order other = (Order) obj;
		if (amount == null) {
			if (other.amount != null) {
				return false;
			}
		} else if (!amount.equals(other.amount)) {
			return false;
		}
		if (chargeId == null) {
			if (other.chargeId != null) {
				return false;
			}
		} else if (!chargeId.equals(other.chargeId)) {
			return false;
		}
		if (createdAt == null) {
			if (other.createdAt != null) {
				return false;
			}
		} else if (!createdAt.equals(other.createdAt)) {
			return false;
		}
		if (currency != other.currency) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (dish == null) {
			if (other.dish != null) {
				return false;
			}
		} else if (!dish.equals(other.dish)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (paid == null) {
			if (other.paid != null) {
				return false;
			}
		} else if (!paid.equals(other.paid)) {
			return false;
		}
		if (createdBy == null) {
			if (other.createdBy != null) {
				return false;
			}
		} else if (!createdBy.equals(other.createdBy)) {
			return false;
		}
		if (updatedAt == null) {
			if (other.updatedAt != null) {
				return false;
			}
		} else if (!updatedAt.equals(other.updatedAt)) {
			return false;
		}
		return true;
	}

	@Override
	public Order clone() {
		try {
			return (Order) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}

	public String getCardToken() {
		return cardToken;
	}

	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}
}
