package com.epickur.api.entity;

import java.io.IOException;

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
import com.epickur.api.entity.databind.RoleDeserializer;
import com.epickur.api.entity.databind.RoleSerializer;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Key entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "key", "userEmail", "role", "createdAt", "updatedAt" })
public final class Key extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Key.class.getSimpleName());
	/** Id **/
	private ObjectId id;
	/** Key **/
	private String key;
	/** User id **/
	private ObjectId userId;
	/** The role **/
	private Role role;
	/** Created at **/
	private DateTime createdAt;
	/** Updated at **/
	private DateTime updatedAt;

	/** Constructor **/
	public Key() {
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
	 * @return The User id
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            The User id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setUserId(final ObjectId userId) {
		this.userId = userId;
	}

	/**
	 * @return The Key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            The Key
	 */
	public void setKey(final String key) {
		this.key = key;
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
	 * @return The User role
	 */
	@JsonSerialize(using = RoleSerializer.class)
	public Role getRole() {
		return role;
	}

	/**
	 * @param role
	 *            The User role
	 */
	@JsonDeserialize(using = RoleDeserializer.class)
	public void setRole(final Role role) {
		this.role = role;
	}

	/**
	 * @param obj
	 *            The DBObject
	 * @return The Key
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Key getObject(final Document obj) throws EpickurParsingException {
		return Key.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json String
	 * @return The Key
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static Key getObject(final String json) throws EpickurParsingException {
		Key key = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			key = mapper.readValue(json, Key.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Key: " + json, e);
		}
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		if (!(obj instanceof Key)) {
			return false;
		}
		Key other = (Key) obj;
		if (createdAt == null) {
			if (other.createdAt != null) {
				return false;
			}
		} else if (!createdAt.equals(other.createdAt)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
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
		if (role == null) {
			if (other.role != null) {
				return false;
			}
		} else if (!role.equals(other.role)) {
			return false;
		}
		return true;
	}

	@Override
	public Key clone() {
		try {
			return (Key) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
