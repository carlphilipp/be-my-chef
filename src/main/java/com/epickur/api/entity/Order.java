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

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.deserialize.OrderStatusDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.entity.serialize.OrderStatusSerializer;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.enumeration.OrderMode;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.voucher.DiscountType;
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
@JsonPropertyOrder(value = { "id", "readableId", "userId", "description", "amount", "status", "currency", "pickupdate", "cardToken", "chargeId",
		"paid", "dish", "voucher", "createdBy", "createdAt", "updatedAt" })
public final class Order extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Order.class.getSimpleName());
	/** Id */
	private ObjectId id;
	/** Readable Id */
	private String readableId;
	/** Description */
	private String description;
	/** Amount */
	private Integer amount;
	/** Amount */
	private OrderStatus status;
	/** Currency */
	private Currency currency;
	/** Pickupdate */
	private String pickupdate;
	/** Dish */
	private Dish dish;
	/** Voucher */
	private Voucher voucher;
	/** Stripe Card Token */
	private String cardToken;
	/** ChargeId from Stripe */
	private String chargeId;
	/** Indicate if paid */
	private Boolean paid;
	/** Order mode */
	private OrderMode mode;
	/** Owner id */
	private ObjectId createdBy;
	/** Created at */
	private DateTime createdAt;
	/** Updated at */
	private DateTime updatedAt;

	/** Constructor */
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
	 * @return
	 */
	public Voucher getVoucher() {
		return voucher;
	}

	/**
	 * @param voucher
	 */
	public void setVoucher(final Voucher voucher) {
		this.voucher = voucher;
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
	 * @return The order status
	 */
	@JsonSerialize(using = OrderStatusSerializer.class)
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The order status
	 */
	@JsonDeserialize(using = OrderStatusDeserializer.class)
	public void setStatus(final OrderStatus status) {
		this.status = status;
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
	 * @param pickupdate
	 *            The pickupdate.
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

	public OrderMode getMode() {
		return mode;
	}

	public void setMode(OrderMode mode) {
		this.mode = mode;
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
	 * @return A card token
	 */
	public String getCardToken() {
		return cardToken;
	}

	/**
	 * @param cardToken
	 *            A card token
	 */
	public void setCardToken(final String cardToken) {
		this.cardToken = cardToken;
	}

	/**
	 * @return A readable id
	 */
	public String getReadableId() {
		return readableId;
	}

	/**
	 * @param readableId
	 *            A readable id
	 */
	public void setReadableId(final String readableId) {
		this.readableId = readableId;
	}

	public Integer calculateTotalAmount() {
		Integer totalAmout = Integer.valueOf(0);
		if (this.getVoucher() != null) {
			Voucher v = this.getVoucher();
			if (v.getDiscountType() == DiscountType.AMOUNT) {
				totalAmout = getAmount() - v.getDiscount();
			} else {
				totalAmout = getAmount() - (getAmount() * v.getDiscount() / 100);
			}
		} else {
			totalAmout = getAmount();
		}
		if (getMode() != null && getMode() == OrderMode.CHEF) {
			totalAmout += 100;
		}
		return totalAmout;
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
				Document dishDoc = new Document();
				resultSet.put("dish", dishDoc);
				Document dishStr = (Document) found.get("dish");
				Set<Entry<String, Object>> setDish = dishStr.entrySet();
				Iterator<Entry<String, Object>> iteratorDish = setDish.iterator();
				while (iteratorDish.hasNext()) {
					Entry<String, Object> entry = iteratorDish.next();
					String key2 = entry.getKey();
					if (key2.equals("id")) {
						dishDoc.put("_id", dishStr.get("id"));
					} else if (key2.equals("caterer")) {
						Document catererDocument = new Document();
						dishDoc.put("caterer", catererDocument);
						Document caterer = (Document) dishStr.get("caterer");
						Set<Entry<String, Object>> setCaterer = caterer.entrySet();
						Iterator<Entry<String, Object>> iteratorCaterer = setCaterer.iterator();
						while (iteratorCaterer.hasNext()) {
							Entry<String, Object> entry2 = iteratorCaterer.next();
							String key3 = entry2.getKey();
							if (key3.equals("id")) {
								catererDocument.put("_id", caterer.get("id"));
							} else {
								catererDocument.put(key3, caterer.get(key3));
							}
						}
					} else {
						dishDoc.put(key2, dishStr.get(key2));
					}
				}
			}
			if (key.equals("voucher")) {
				Document v = new Document();
				resultSet.put("voucher", v);
				Document voucherDoc = (Document) found.get("voucher");
				Set<Entry<String, Object>> setVoucher = voucherDoc.entrySet();
				Iterator<Entry<String, Object>> iteratorVoucher = setVoucher.iterator();
				while (iteratorVoucher.hasNext()) {
					Entry<String, Object> entry = iteratorVoucher.next();
					String key2 = entry.getKey();
					if (key2.equals("id")) {
						v.put("_id", voucherDoc.get("id"));
					} else {
						v.put(key2, voucherDoc.get(key2));
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param obj
	 *            The Document
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
		result = prime * result + ((cardToken == null) ? 0 : cardToken.hashCode());
		result = prime * result + ((chargeId == null) ? 0 : chargeId.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((dish == null) ? 0 : dish.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((paid == null) ? 0 : paid.hashCode());
		result = prime * result + ((pickupdate == null) ? 0 : pickupdate.hashCode());
		result = prime * result + ((readableId == null) ? 0 : readableId.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((voucher == null) ? 0 : voucher.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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
		if (cardToken == null) {
			if (other.cardToken != null) {
				return false;
			}
		} else if (!cardToken.equals(other.cardToken)) {
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
		if (createdBy == null) {
			if (other.createdBy != null) {
				return false;
			}
		} else if (!createdBy.equals(other.createdBy)) {
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
		if (mode != other.mode) {
			return false;
		}
		if (paid == null) {
			if (other.paid != null) {
				return false;
			}
		} else if (!paid.equals(other.paid)) {
			return false;
		}
		if (pickupdate == null) {
			if (other.pickupdate != null) {
				return false;
			}
		} else if (!pickupdate.equals(other.pickupdate)) {
			return false;
		}
		if (readableId == null) {
			if (other.readableId != null) {
				return false;
			}
		} else if (!readableId.equals(other.readableId)) {
			return false;
		}
		if (status != other.status) {
			return false;
		}
		if (updatedAt == null) {
			if (other.updatedAt != null) {
				return false;
			}
		} else if (!updatedAt.equals(other.updatedAt)) {
			return false;
		}
		if (voucher == null) {
			if (other.voucher != null) {
				return false;
			}
		} else if (!voucher.equals(other.voucher)) {
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
