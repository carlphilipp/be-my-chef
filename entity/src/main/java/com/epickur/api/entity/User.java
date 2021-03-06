package com.epickur.api.entity;

import com.epickur.api.annotation.ChangePasswordValidate;
import com.epickur.api.annotation.PhoneNumberValidate;
import com.epickur.api.entity.deserialize.PhoneNumberDeserializer;
import com.epickur.api.entity.deserialize.RoleDeserializer;
import com.epickur.api.entity.serialize.PhoneNumberSerializer;
import com.epickur.api.entity.serialize.RoleSerializer;
import com.epickur.api.enumeration.Role;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.operation.Create;
import com.epickur.api.operation.Update;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@ChangePasswordValidate(groups = { Update.class })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "first", "last", "password", "email", "role", "phoneNumber", "zipcode", "state", "country", "allow",
		"key", "allow", "createdAt", "updatedAt" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractMainDBEntity {

	public interface PublicView {
	}

	public interface PrivateView extends PublicView {
	}

	@JsonView(PublicView.class)
	@NotBlank(message = "{user.name.null}", groups = { Create.class })
	private String name;

	@JsonView(PublicView.class)
	private String first;

	@JsonView(PublicView.class)
	private String last;

	@JsonView(PrivateView.class)
	@NotBlank(message = "{user.password.null}", groups = { Create.class })
	private String password;

	@JsonView(PublicView.class)
	@NotBlank(message = "{user.email.null}", groups = { Create.class })
	private String email;

	@JsonView(PublicView.class)
	@PhoneNumberValidate(groups = { Create.class, Update.class })
	private PhoneNumber phoneNumber;

	@JsonView(PublicView.class)
	@NotBlank(message = "{user.zipcode.null}", groups = { Create.class })
	private String zipcode;

	@JsonView(PublicView.class)
	@NotBlank(message = "{user.state.null}", groups = { Create.class })
	private String state;

	@JsonView(PublicView.class)
	@NotBlank(message = "{user.country.null}", groups = { Create.class })
	private String country;

	@JsonView(PublicView.class)
	private Integer allow;

	@JsonIgnore
	private String code;

	@JsonView(PublicView.class)
	private String key;

	@JsonView(PrivateView.class)
	private String newPassword;

	@JsonView(PrivateView.class)
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
		} catch (final IOException e) {
			throw new EpickurParsingException("Can not convert string to User: " + json, e);
		}
	}

	@Override
	@SneakyThrows(CloneNotSupportedException.class)
	public User clone() {
		return (User) super.clone();
	}
}
