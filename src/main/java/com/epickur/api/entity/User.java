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
import com.epickur.api.entity.deserialize.PhoneNumberDeserializer;
import com.epickur.api.entity.deserialize.RoleDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.entity.serialize.PhoneNumberSerializer;
import com.epickur.api.entity.serialize.RoleSerializer;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * User entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "first", "last", "password", "email", "role", "phoneNumber", "zipcode", "state", "country", "allow",
		"key", "allow", "createdAt", "updatedAt" })
@Getter 
@Setter
@ToString
@EqualsAndHashCode(callSuper=false)
public final class User extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(User.class.getSimpleName());
	/** Id */
	private ObjectId id;
	/** Name */
	private String name;
	/** First */
	private String first;
	/** Last */
	private String last;
	/** Password */
	private String password;
	/** Email */
	private String email;
	/** Phone number */
	private PhoneNumber phoneNumber;
	/** Zip code */
	private String zipcode;
	/** State */
	private String state;
	/** Country */
	private String country;
	/** Indicate if allowed to login */
	private Integer allow;
	/** Created at */
	private DateTime createdAt;
	/** Updated at */
	private DateTime updatedAt;
	/** Code generated the first time on create */
	@JsonIgnore
	private String code;
	/** API key generated */
	private String key;
	/** New password to check */
	private String newPassword;
	/** Role. Not exposed to User */
	private Role role;

	/** Constructor */
	public User() {
	}

	/**
	 * @return An ObjectId
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            An ObjectId
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setId(final ObjectId id) {
		this.id = id;
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
	 * @return The User phone number
	 */
	@JsonSerialize(using = PhoneNumberSerializer.class)
	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            The User phone number
	 */
	@JsonDeserialize(using = PhoneNumberDeserializer.class)
	public void setPhoneNumber(final PhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Prepare user to be inserted into DB.
	 * Set create date and update to current time.
	 * Reset id and key to null.
	 */
	public void prepareUserToInsertIntoDB(){
		DateTime time = new DateTime();
		this.setCreatedAt(time);
		this.setUpdatedAt(time);
	
		this.setId(null);
		this.setKey(null);
	}
	
	/**
	 * Prepare user to be updated into DB.
	 * Set created date to null and update date to current time.
	 * Reset key to null
	 */
	public void prepareUserToBeUpdatedIntoDB(){
		DateTime time = new DateTime();
		this.setCreatedAt(null);
		this.setUpdatedAt(time);
		
		this.setKey(null);
	}
	
	/**
	 * @param obj
	 *            The Document
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static User getDocumentAsUser(final Document obj) throws EpickurParsingException {
		return User.getJsonStringAsUser(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json strng
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static User getJsonStringAsUser(final String json) throws EpickurParsingException {
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			User user = mapper.readValue(json, User.class);
			return user;
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to User: " + json, e);
		}
	}

	/**
	 * @return a Document
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	@JsonIgnore
	public Document getUserUpdateQuery() throws EpickurParsingException {
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
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
