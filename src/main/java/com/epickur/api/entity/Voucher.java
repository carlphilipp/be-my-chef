package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.DiscountTypeDeserializer;
import com.epickur.api.entity.deserialize.ExpirationTypeDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.deserialize.StatusDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.DiscountTypeSerializer;
import com.epickur.api.entity.serialize.ExpirationTypeSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
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
public final class Voucher extends AbstractEntity {

	/** Id */
	private ObjectId id;
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
	/** Created at */
	private DateTime createdAt;
	/** Updated at */
	private DateTime updatedAt;

	/**
	 * @return The Id
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            The Id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setId(final ObjectId id) {
		this.id = id;
	}

	/**
	 * @return The Code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            The Code
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return The discount
	 */
	public Integer getDiscount() {
		return discount;
	}

	/**
	 * @param discount
	 *            The discount
	 */
	public void setDiscount(final Integer discount) {
		this.discount = discount;
	}

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
	 * @param expirationDate
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
	 * @return Used count
	 */
	public Integer getUsedCount() {
		return usedCount;
	}

	/**
	 * @param usedCount
	 *            Used count
	 */
	public void setUsedCount(final Integer usedCount) {
		this.usedCount = usedCount;
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
	 * @param obj
	 *            The DBObject
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((discountType == null) ? 0 : discountType.hashCode());
		result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
		result = prime * result + ((expirationType == null) ? 0 : expirationType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((usedCount == null) ? 0 : usedCount.hashCode());
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
		if (!(obj instanceof Voucher)) {
			return false;
		}
		Voucher other = (Voucher) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (createdAt == null) {
			if (other.createdAt != null) {
				return false;
			}
		} else if (!createdAt.equals(other.createdAt)) {
			return false;
		}
		if (discount == null) {
			if (other.discount != null) {
				return false;
			}
		} else if (!discount.equals(other.discount)) {
			return false;
		}
		if (discountType != other.discountType) {
			return false;
		}
		if (expiration == null) {
			if (other.expiration != null) {
				return false;
			}
		} else if (!expiration.equals(other.expiration)) {
			return false;
		}
		if (expirationType != other.expirationType) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
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
		if (usedCount != other.usedCount) {
			return false;
		}
		return true;
	}
}
