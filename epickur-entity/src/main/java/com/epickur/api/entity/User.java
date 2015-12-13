package com.epickur.api.entity;

import com.epickur.api.entity.deserialize.PhoneNumberDeserializer;
import com.epickur.api.entity.deserialize.RoleDeserializer;
import com.epickur.api.entity.serialize.PhoneNumberSerializer;
import com.epickur.api.entity.serialize.RoleSerializer;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.epickur.api.validator.annotation.ChangePasswordValidate;
import com.epickur.api.validator.annotation.PhoneNumberValidate;
import com.epickur.api.validator.operation.Create;
import com.epickur.api.validator.operation.Update;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.hibernate.validator.constraints.NotBlank;

import java.io.IOException;

/**
 * User entity
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@ChangePasswordValidate(groups = { Update.class })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "first", "last", "password", "email", "role", "phoneNumber", "zipcode", "state", "country", "allow",
		"key", "allow", "createdAt", "updatedAt" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractMainDBEntity {

	/**
	 * Name
	 */
	@NotBlank(message = "{user.name.null}", groups = { Create.class })
	private String name;
	/**
	 * First
	 */
	private String first;
	/**
	 * Last
	 */
	private String last;
	/**
	 * Password
	 */
	@NotBlank(message = "{user.password.null}", groups = { Create.class })
	private String password;
	/**
	 * Email
	 */
	@NotBlank(message = "{user.email.null}", groups = { Create.class })
	private String email;
	/**
	 * Phone number
	 */
	@PhoneNumberValidate(groups = { Create.class, Update.class })
	private PhoneNumber phoneNumber;
	/**
	 * Zip code
	 */
	@NotBlank(message = "{user.zipcode.null}", groups = { Create.class })
	private String zipcode;
	/**
	 * State
	 */
	@NotBlank(message = "{user.state.null}", groups = { Create.class })
	private String state;
	/**
	 * Country
	 */
	@NotBlank(message = "{user.country.null}", groups = { Create.class })
	private String country;

	/**
	 * Indicate if allowed to login
	 */
	private Integer allow;
	/**
	 * Code generated the first time on create
	 */
	@JsonIgnore
	private String code;
	/**
	 * API key generated
	 */
	private String key;
	/**
	 * New password to check
	 */
	private String newPassword;
	/**
	 * Role. Not exposed to User
	 */
	private Role role;

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
	 * @return The User phone number
	 */
	@JsonSerialize(using = PhoneNumberSerializer.class)
	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber The User phone number
	 */
	@JsonDeserialize(using = PhoneNumberDeserializer.class)
	public void setPhoneNumber(final PhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public void prepareForInsertionIntoDB() {
		super.prepareForInsertionIntoDB();
		this.setKey(null);
		this.setRole(Role.USER);
	}

	@Override
	public void prepareForUpdateIntoDB() {
		super.prepareForUpdateIntoDB();
		this.setKey(null);
	}

	/**
	 * @param obj The Document
	 * @return The User
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	public static User getDocumentAsUser(final Document obj) throws EpickurParsingException {
		return User.getJsonStringAsUser(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json The json string
	 * @return The User
	 * @throws EpickurParsingException If an Epickur exception occurred
	 */
	private static User getJsonStringAsUser(final String json) throws EpickurParsingException {
		try {
			final ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			return mapper.readValue(json, User.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to User: " + json, e);
		}
	}

	@Override
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			log.error("Error while cloning: {}", e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
