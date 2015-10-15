package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.joda.time.DateTime;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.DiscountTypeDeserializer;
import com.epickur.api.entity.deserialize.ExpirationTypeDeserializer;
import com.epickur.api.entity.deserialize.StatusDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.DiscountTypeSerializer;
import com.epickur.api.entity.serialize.ExpirationTypeSerializer;
import com.epickur.api.entity.serialize.StatusSerializer;
import com.epickur.api.enumeration.voucher.DiscountType;
import com.epickur.api.enumeration.voucher.ExpirationType;
import com.epickur.api.enumeration.voucher.Status;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Voucher entity.
 * 
 * @author cph
 * @version 1.0
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "code", "discount", "discountType", "expirationType", "expiration", "status", "usedCount", "createdAt",
		"updatedAt" })
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class Voucher extends AbstractMainDBEntity {

	/** Code */
	private String code;
	/** Discount */
	private Integer discount;
	/** Discount type */
	private DiscountType discountType;
	/** Expiration type */
	private ExpirationType expirationType;
	/** Expiration Date */
	private DateTime expiration;
	/** Status */
	private Status status;
	/** Used count */
	private Integer usedCount;

	/**
	 * @return The discount type
	 */
	@JsonSerialize(using = DiscountTypeSerializer.class)
	public DiscountType getDiscountType() {
		return discountType;
	}

	/**
	 * @param discountType
	 *            The discount type
	 */
	@JsonDeserialize(using = DiscountTypeDeserializer.class)
	public void setDiscountType(final DiscountType discountType) {
		this.discountType = discountType;
	}

	/**
	 * @return The expiration type
	 */
	@JsonSerialize(using = ExpirationTypeSerializer.class)
	public ExpirationType getExpirationType() {
		return expirationType;
	}

	/**
	 * @param expirationType
	 *            The expiration type
	 */
	@JsonDeserialize(using = ExpirationTypeDeserializer.class)
	public void setExpirationType(final ExpirationType expirationType) {
		this.expirationType = expirationType;
	}

	/**
	 * @return The expiration date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public DateTime getExpiration() {
		return this.expiration;
	}

	/**
	 * @param expiration
	 *            The expiration date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public void setExpiration(final DateTime expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return The status
	 */
	@JsonSerialize(using = StatusSerializer.class)
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            The status
	 */
	@JsonDeserialize(using = StatusDeserializer.class)
	public void setStatus(final Status status) {
		this.status = status;
	}

	/**
	 * @param obj
	 *            The Document
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Voucher getObject(final Document obj) throws EpickurParsingException {
		return Voucher.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json strng
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static Voucher getObject(final String json) throws EpickurParsingException {
		Voucher user = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			user = mapper.readValue(json, Voucher.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Voucher: " + json, e);
		}
		return user;
	}

	/**
	 * @return a Document
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	@JsonIgnore
	public Document getUpdateDocument() throws EpickurParsingException {
		String apiView = toStringAPIView();
		Document found = Document.parse(apiView);
		Document args = new Document();
		Document result = new Document().append("$set", args);
		Set<Entry<String, Object>> set = found.entrySet();
		Iterator<Entry<String, Object>> iterator = set.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			String k = entry.getKey();
			if (!k.equals("id")) {
				args.put(k, found.get(k));
			}
		}
		return result;
	}
}
