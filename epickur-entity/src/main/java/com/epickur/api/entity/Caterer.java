package com.epickur.api.entity;

import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Caterer entity
 *
 * @author cph
 * @version 1.0
 */
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "description", "manager", "email", "phone", "location", "workingTimes", "createdBy", "createdAt",
		"updatedAt" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Caterer extends AbstractMainDBEntity {

	/**
	 * Name
	 */
	private String name;
	/**
	 * Description
	 */
	private String description;
	/**
	 * Manager name
	 */
	private String manager;
	/**
	 * Manager email
	 */
	private String email;
	/**
	 * Phone
	 */
	private String phone;
	/**
	 * Location
	 */
	private Location location;
	/**
	 * Working times
	 */
	private WorkingTimes workingTimes;
	/**
	 * Owner id
	 */
	private ObjectId createdBy;

	/**
	 * @return The user id that created the object
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param ownerId The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId ownerId) {
		this.createdBy = ownerId;
	}

	/**
	 * @param prefix The prefix
	 * @return A map
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	@JsonIgnore
	public Map<String, Object> getUpdateMap(final String prefix) throws EpickurParsingException {
		final String apiView = toStringAPIView();
		final Document found = Document.parse(apiView);
		final Map<String, Object> result = new HashMap<>();
		final Set<Entry<String, Object>> entrySet = found.entrySet();
		for (Entry<String, Object> en : entrySet) {
			final String key = en.getKey();
			if (!key.equals("id")) {
				if (key.equals("location")) {
					final Location loc = Location.getObject((Document) found.get(key));
					final Map<String, Object> locations = loc.getUpdateMap(prefix + ".location");
					result.putAll(locations);
				} else if (key.equals("workingTimes")) {
					final WorkingTimes wt = WorkingTimes.getObject((Document) found.get(key));
					final Map<String, Object> workingTimesMap = wt.getUpdateMapObject(prefix + ".workingTimes");
					result.putAll(workingTimesMap);
				} else {
					result.put("caterer." + key, found.get(key).toString());
				}
			}
		}
		return result;
	}

	/**
	 * @param obj The Document
	 * @return the Caterer
	 * @throws EpickurParsingException If an EpickurParsingException exception occurred
	 */
	public static Caterer getDocumentAsCatererDBView(final Document obj) throws EpickurParsingException {
		return Caterer.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)), View.DB);
	}

	/**
	 * @param obj The Document
	 * @return the Caterer
	 * @throws EpickurParsingException If an EpickurParsingException exception occurred
	 */
	public static Caterer getDocumentAsCatererAPIView(final Document obj) throws EpickurParsingException {
		return Caterer.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)), View.API);
	}

	/**
	 * @param json The json string
	 * @param view The View
	 * @return the Caterer
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	private static Caterer getObject(final String json, final View view) throws EpickurParsingException {
		Caterer caterer;
		try {
			ObjectMapper om;
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
	@SneakyThrows(CloneNotSupportedException.class)
	public Caterer clone() {
		return (Caterer) super.clone();
	}
}
