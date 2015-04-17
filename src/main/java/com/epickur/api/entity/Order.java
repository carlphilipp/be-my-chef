package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

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
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * Order entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "userId", "description", "amount", "currency", "chargeId", "paid", "dish", "createdBy", "createdAt", "updatedAt" })
public final class Order extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Order.class.getSimpleName());
	/** Id **/
	private ObjectId id;
	/** User id **/
	private String userId;
	/** Description **/
	private String description;
	/** Amount **/
	private Integer amount;
	/** Currency **/
	private Currency currency;
	/** Dish **/
	private Dish dish;
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
	 * @return The User id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            The User id
	 */
	public void setUserId(final String userId) {
		this.userId = userId;
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
	 * @param ownerId
	 *            The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	@JsonIgnore
	public DBObject getUpdateBasicDBObject() throws EpickurParsingException {
		String str = toStringAPIView();
		DBObject found = (DBObject) JSON.parse(str);
		DBObject orderDBObject = BasicDBObjectBuilder.start().get();
		DBObject res = BasicDBObjectBuilder.start("$set", orderDBObject).get();
		Set<String> set = found.keySet();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (!key.equals("id") && !key.equals("dish")) {
				orderDBObject.put(key, found.get(key));
			}
			if (key.equals("dish")) {
				DBObject dishDBObject = BasicDBObjectBuilder.start().get();
				orderDBObject.put("dish", dishDBObject);

				String dishStr = found.get("dish").toString();
				DBObject dishFound = (DBObject) JSON.parse(dishStr);
				Set<String> setDish = dishFound.keySet();
				Iterator<String> iteratorDish = setDish.iterator();
				while (iteratorDish.hasNext()) {
					String key2 = iteratorDish.next();
					if (key2.equals("id")) {
						dishDBObject.put("_id", dishFound.get("id"));
					} else if (key2.equals("caterer")) {
						DBObject catererDBObject = BasicDBObjectBuilder.start().get();
						dishDBObject.put("caterer", catererDBObject);

						String caterer = dishFound.get("caterer").toString();
						DBObject catererFound = (DBObject) JSON.parse(caterer);
						Set<String> setCaterer = catererFound.keySet();
						Iterator<String> iteratorCaterer = setCaterer.iterator();
						while (iteratorCaterer.hasNext()) {
							String key3 = iteratorCaterer.next();
							if (key3.equals("id")) {
								catererDBObject.put("_id", catererFound.get("id"));
							} else {
								catererDBObject.put(key3, catererFound.get(key3));
							}
						}
					} else {
						dishDBObject.put(key2, dishFound.get(key2));
					}
				}
			}
		}
		return res;
	}

	/**
	 * @param obj
	 *            The DBObject
	 * @return An Order
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Order getObject(final DBObject obj) throws EpickurParsingException {
		return Order.getDBObject(obj.toString());
	}

	/**
	 * @param json
	 *            The json to convert
	 * @return An Order
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Order getDBObject(final String json) throws EpickurParsingException {
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
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
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
}
