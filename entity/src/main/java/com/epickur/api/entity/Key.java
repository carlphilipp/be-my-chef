package com.epickur.api.entity;

import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.deserialize.RoleDeserializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.entity.serialize.RoleSerializer;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
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

import java.io.IOException;

/**
 * Key entity
 *
 * @author cph
 * @version 1.0
 */
@Log4j2
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "key", "userId", "role", "createdAt", "updatedAt" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Key extends AbstractMainDBEntity {

	/**
	 * Key
	 */
	private String key;
	/**
	 * User id
	 */
	private ObjectId userId;
	/**
	 * The role
	 */
	private Role role;

	/**
	 * @return The User id
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getUserId() {
		return userId;
	}

	/**
	 * @param userId The User id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setUserId(final ObjectId userId) {
		this.userId = userId;
	}

	/**
	 * @return The User role
	 */
	@JsonSerialize(using = RoleSerializer.class)
	public Role getRole() {
		return role;
	}

	/**
	 * @param role The User role
	 */
	@JsonDeserialize(using = RoleDeserializer.class)
	public void setRole(final Role role) {
		this.role = role;
	}

	/**
	 * @param obj The Document
	 * @return The Key
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	public static Key getDocumentAsKey(final Document obj) throws EpickurParsingException {
		return Key.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json The json String
	 * @return The Key
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	private static Key getObject(final String json) throws EpickurParsingException {
		Key key;
		try {
			final ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			key = mapper.readValue(json, Key.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Key: " + json, e);
		}
		return key;
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public Key clone() {
		return (Key) super.clone();
	}
}
