package com.epickur.api.entity;

import com.epickur.api.annotation.PickupdateValidate;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.deserialize.OrderStatusDeserializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.entity.serialize.OrderStatusSerializer;
import com.epickur.api.enumeration.Currency;
import com.epickur.api.enumeration.OrderMode;
import com.epickur.api.enumeration.OrderStatus;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.operation.Create;
import com.epickur.api.operation.Update;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map.Entry;

/**
 * Order entity
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@PickupdateValidate(groups = { Create.class, Update.class })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "readableId", "userId", "description", "quantity", "amount", "status", "currency", "pickupdate", "cardToken",
		"chargeId", "paid", "dish", "voucher", "createdBy", "createdAt", "updatedAt" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractMainDBEntity {

	/**
	 * Readable Id
	 */
	private String readableId;
	/**
	 * Description
	 */
	@NotBlank(message = "{order.description.null}", groups = { Create.class })
	private String description;
	/**
	 * Quantity
	 */
	@NotNull(message = "{order.quantity.null}", groups = { Create.class })
	private Integer quantity;
	/**
	 * Amount
	 */
	@NotNull(message = "{order.amount.null}", groups = { Create.class })
	private Integer amount;
	/**
	 * Amount
	 */
	private OrderStatus status;
	/**
	 * Currency
	 */
	@NotNull(message = "{order.currency.null}", groups = { Create.class })
	private Currency currency;
	/**
	 * Pickupdate
	 */
	@NotBlank(message = "{order.pickupdate.null}", groups = { Create.class })
	private String pickupdate;
	/**
	 * Dish
	 */
	private Dish dish;
	/**
	 * Voucher
	 */
	private Voucher voucher;
	/**
	 * Stripe Card Token
	 */
	@NotBlank(message = "{order.cardToken.null}", groups = { Create.class })
	private String cardToken;
	/**
	 * ChargeId from Stripe
	 */
	private String chargeId;
	/**
	 * Indicate if paid
	 */
	private Boolean paid;
	/**
	 * Order mode
	 */
	private OrderMode mode;
	/**
	 * Owner id
	 */
	private ObjectId createdBy;

	/**
	 * @return The order status
	 */
	@JsonSerialize(using = OrderStatusSerializer.class)
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * @param status The order status
	 */
	@JsonDeserialize(using = OrderStatusDeserializer.class)
	public void setStatus(final OrderStatus status) {
		this.status = status;
	}

	/**
	 * @return The user id that created the object
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public void prepareForInsertionIntoDB() {
		super.prepareForInsertionIntoDB();
		setStatus(OrderStatus.PENDING);
	}

	@Override
	public void prepareForUpdateIntoDB() {
		super.prepareForUpdateIntoDB();
		this.setReadableId(null);
	}

	/**
	 * @return An integer.
	 */
	public Integer calculateTotalAmount() {
		Integer totalAmout;
		if (this.getVoucher() != null) {
			final Voucher v = this.getVoucher();
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
	 * @throws EpickurParsingException If a parsing exception occured
	 */
	@JsonIgnore
	@Override
	public Document getUpdateQuery() throws EpickurParsingException {
		final String apiView = toStringAPIView();
		final Document found = Document.parse(apiView);
		final Document resultSet = new Document();
		final Document result = new Document().append("$set", resultSet);
		for (final Entry<String, Object> en : found.entrySet()) {
			final String key = en.getKey();
			if (!key.equals("id") && !key.equals("dish")) {
				resultSet.put(key, found.get(key));
			}
			if (key.equals("dish")) {
				final Document dishDoc = new Document();
				resultSet.put("dish", dishDoc);
				final Document dishStr = (Document) found.get("dish");
				for (final Entry<String, Object> entry : dishStr.entrySet()) {
					String key2 = entry.getKey();
					if (key2.equals("id")) {
						dishDoc.put("_id", dishStr.get("id"));
					} else if (key2.equals("caterer")) {
						final Document catererDocument = new Document();
						dishDoc.put("caterer", catererDocument);
						final Document caterer = (Document) dishStr.get("caterer");
						for (final Entry<String, Object> entry2 : caterer.entrySet()) {
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
				final Document v = new Document();
				resultSet.put("voucher", v);
				final Document voucherDoc = (Document) found.get("voucher");
				for (final Entry<String, Object> entry : voucherDoc.entrySet()) {
					final String key2 = entry.getKey();
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
	 * @param obj The Document
	 * @return An Order
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	public static Order getDocumentAsOrder(final Document obj) throws EpickurParsingException {
		return Order.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json The json to convert
	 * @return An Order
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	private static Order getObject(final String json) throws EpickurParsingException {
		Order user;
		try {
			final ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			user = mapper.readValue(json, Order.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Order: " + json, e);
		}
		return user;
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Order clone() {
		return (Order) super.clone();
	}
}
