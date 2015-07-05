package com.epickur.api.entity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
import com.epickur.api.entity.times.WorkingTimes;
import com.epickur.api.enumeration.View;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperAPI;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Caterer entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "description", "manager", "email", "phone", "location", "workingTimes", "createdBy", "createdAt",
		"updatedAt" })
public final class Caterer extends AbstractEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Caterer.class.getSimpleName());
	/** Id */
	private ObjectId id;
	/** Name */
	private String name;
	/** Description */
	private String description;
	/** Manager name */
	private String manager;
	/** Manager email */
	private String email;
	/** Phone */
	private String phone;
	/** Location */
	private Location location;
	/** Working times */
	private WorkingTimes workingTimes;
	/** Owner id */
	private ObjectId createdBy;
	/** Created at */
	private DateTime createdAt;
	/** Updated at */
	private DateTime updatedAt;

	/** Constructor */
	public Caterer() {
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
	 * @return The manager
	 */
	public String getManager() {
		return manager;
	}

	/**
	 * @param manager
	 *            The manager
	 */
	public void setManager(final String manager) {
		this.manager = manager;
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
	 * @return The phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            The phone
	 */
	public void setPhone(final String phone) {
		this.phone = phone;
	}

	/**
	 * @return The user id that created the object
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param ownerId
	 *            The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId ownerId) {
		this.createdBy = ownerId;
	}

	/**
	 * @return The Location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            The Location
	 */
	public void setLocation(final Location location) {
		this.location = location;
	}

	/**
	 * @return A Working Times
	 */
	public WorkingTimes getWorkingTimes() {
		return workingTimes;
	}

	/**
	 * @param workingTimes
	 *            The working times
	 */
	public void setWorkingTimes(final WorkingTimes workingTimes) {
		this.workingTimes = workingTimes;
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
	 * @param prefix
	 *            The prefix
	 * @return A map
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 * 
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) throws EpickurParsingException {
		String apiView = toStringAPIView();
		Document found = Document.parse(apiView);
		Map<String, Object> result = new HashMap<String, Object>();
		Set<Entry<String, Object>> entrySet = found.entrySet();
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> en = iterator.next();
			String key = en.getKey();
			if (!key.equals("id")) {
				if (key.equals("location")) {
					Location loc = Location.getObject((Document) found.get(key));
					Map<String, Object> locations = loc.getUpdateListBasicDBObject(prefix + ".location");
					for (Entry<String, Object> entry : locations.entrySet()) {
						result.put(entry.getKey(), entry.getValue());
					}
				} else if (key.equals("workingTimes")) {
					WorkingTimes wt = WorkingTimes.getObject((Document) found.get(key));
					Map<String, Object> workingTimesMap = wt.getUpdateMapObject(prefix + ".workingTimes");
					result.putAll(workingTimesMap);
				} else {
					result.put("caterer." + key, found.get(key).toString());
				}
			}
		}
		return result;
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
		Set<Entry<String, Object>> entrySet = found.entrySet();
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> temp = iterator.next();
			String key = temp.getKey();
			if (!key.equals("id")) {
				args.put(key, found.get(key));
			}
		}
		return result;
	}

	/**
	 * @param obj
	 *            The Document
	 * @param view
	 *            The View
	 * @return the Caterer
	 * @throws EpickurParsingException
	 *             If an EpickurParsingException exception occurred
	 */
	public static Caterer getObject(final Document obj, final View view) throws EpickurParsingException {
		return Caterer.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)), view);
	}

	/**
	 * @param json
	 *            The json string
	 * @param view
	 *            The View
	 * @return the Caterer
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static Caterer getObject(final String json, final View view) throws EpickurParsingException {
		Caterer caterer = null;
		try {
			ObjectMapper om = null;
			if (view == View.API) {
				om = ObjectMapperWrapperAPI.getInstance();
			} else {
				om = ObjectMapperWrapperDB.getInstance();
			}
			caterer = om.readValue(json, Caterer.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Caterer: " + json, e);
		}
		return caterer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((manager == null) ? 0 : manager.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
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
		if (!(obj instanceof Caterer)) {
			return false;
		}
		Caterer other = (Caterer) obj;
		/*
		 * if (createdAt == null) { if (other.createdAt != null) { return false; } } else if (!createdAt.equals(other.createdAt)) { return false; }
		 */
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
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
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (manager == null) {
			if (other.manager != null) {
				return false;
			}
		} else if (!manager.equals(other.manager)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!phone.equals(other.phone)) {
			return false;
		}
		if (updatedAt == null) {
			if (other.updatedAt != null) {
				return false;
			}
		} else if (!updatedAt.equals(other.updatedAt)) {
			return false;
		}
		return true;
	}

	@Override
	public Caterer clone() {
		try {
			return (Caterer) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
