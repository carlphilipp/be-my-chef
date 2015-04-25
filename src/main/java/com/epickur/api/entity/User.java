package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * User entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "password", "email", "allow", "key", "allow", "createdAt", "updatedAt" })
public final class User extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(User.class.getSimpleName());
	/** Id **/
	private ObjectId id;
	/** Name **/
	private String name;
	/** Password **/
	private String password;
	/** Email **/
	private String email;
	/** Indicate if allowed to login **/
	private Integer allow;
	/** Created at **/
	private DateTime createdAt;
	/** Updated at **/
	private DateTime updatedAt;
	/** Code generated the first time on create **/
	@JsonIgnore
	private String code;
	/** API key generated **/
	private String key;
	/** New password to check **/
	@JsonIgnore
	private String newPassword;
	/** Role. Not exposed to User **/
	private Role role;

	/** Constructor **/
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
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return The passwors
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            The password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return 1 if allowed, 0 if not
	 */
	public Integer getAllow() {
		return allow;
	}

	/**
	 * @param allow
	 *            1 if allowed, 0 if not
	 */
	public void setAllow(final Integer allow) {
		this.allow = allow;
	}

	/**
	 * @return The email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            The email
	 */
	public void setEmail(final String email) {
		this.email = email;
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
	 * @return The new password
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            The new password
	 */
	public void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return The code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            The code
	 */
	public void setCode(final String code) {
		this.code = code;
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
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static User getDBObject(final Document obj) throws EpickurParsingException {
		return User.getDBObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json strng
	 * @return The User
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static User getDBObject(final String json) throws EpickurParsingException {
		User user = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			user = mapper.readValue(json, User.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to User: " + json, e);
		}
		return user;
	}

	/**
	 * @return a Document
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	@JsonIgnore
	public Document getUpdateBasicDBObject() throws EpickurParsingException {
		String str = toStringAPIView();
		Document found = Document.parse(str);
		Document arg = new Document();
		Document res = new Document().append("$set", arg);
		Set<String> set = found.keySet();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			if (!k.equals("id")) {
				arg.put(k, found.get(k));
			}
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allow == null) ? 0 : allow.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((newPassword == null) ? 0 : newPassword.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
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
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		if (allow == null) {
			if (other.allow != null) {
				return false;
			}
		} else if (!allow.equals(other.allow)) {
			return false;
		}
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
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
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
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (newPassword == null) {
			if (other.newPassword != null) {
				return false;
			}
		} else if (!newPassword.equals(other.newPassword)) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (updatedAt == null) {
			if (other.updatedAt != null) {
				return false;
			}
		} else if (!updatedAt.equals(other.updatedAt)) {
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
	public User clone() {
		try {
			return (User) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
