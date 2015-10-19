package com.epickur.api.entity;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

import com.epickur.api.entity.deserialize.DishTypeDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.serialize.DishTypeSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.enumeration.DishType;
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
 * Dish entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "description", "type", "price", "cookingTime", "difficultyLevel", "videoUrl", "nutritionFacts",
		"ingredients", "condiments", "steps", "utensils", "caterer", "createdAt", "updatedAt" })
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class Dish extends AbstractMainDBEntity {

	/** Logger */
	private static final Logger LOG = LogManager.getLogger(Dish.class.getSimpleName());
	/** Name */
	private String name;
	/** Description */
	private String description;
	/** Type */
	private DishType type;
	/** Price */
	private Integer price;
	/** Cooking time */
	private Integer cookingTime;
	/** Difficulty level */
	private Integer difficultyLevel;
	/** Image After URL */
	private String imageAfterUrl;
	/** Video URL */
	private String videoUrl;
	/** Nutrition facts */
	private List<NutritionFact> nutritionFacts;
	/** Ingredients */
	private List<Ingredient> ingredients;
	/** Steps */
	private List<String> steps;
	/** Condiments */
	private List<String> condiments;
	/** Utensils */
	private List<String> utensils;
	/** Caterer */
	private Caterer caterer;
	/** Owner id */
	private ObjectId createdBy;

	/**
	 * @return The type
	 */
	@JsonSerialize(using = DishTypeSerializer.class)
	public DishType getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type
	 */
	@JsonDeserialize(using = DishTypeDeserializer.class)
	public void setType(final DishType type) {
		this.type = type;
	}

	/**
	 * @return The user id that created the object
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            The user id
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setCreatedBy(final ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return a Document
	 * @throws EpickurParsingException
	 *             If a parsing exception happened
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
			Entry<String, Object> en = iterator.next();
			String key = en.getKey();
			if (!key.equals("id")) {
				if (key.equals("caterer")) {
					Caterer cat = Caterer.getDocumentAsCatererAPIView((Document) found.get(key));
					Map<String, Object> caterers = cat.getUpdateMap("caterer");
					for (Entry<String, Object> entry : caterers.entrySet()) {
						args.put(entry.getKey(), entry.getValue());
					}
				} else {
					args.put(key, found.get(key));
				}
			}
		}
		return result;
	}

	/**
	 * @param obj
	 *            The Document
	 * @return The Dish
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	public static Dish getDocumentAsDish(final Document obj) throws EpickurParsingException {
		return Dish.getObject(obj.toJson(new JsonWriterSettings(JsonMode.STRICT)));
	}

	/**
	 * @param json
	 *            The json string
	 * @return The Dish
	 * @throws EpickurParsingException
	 *             If an epickur exception occurred
	 */
	private static Dish getObject(final String json) throws EpickurParsingException {
		Dish dish = null;
		try {
			ObjectMapper mapper = ObjectMapperWrapperDB.getInstance();
			dish = mapper.readValue(json, Dish.class);
		} catch (IOException e) {
			throw new EpickurParsingException("Can not convert string to Dish: " + json, e);
		}
		return dish;
	}

	@Override
	public Dish clone() {
		try {
			return (Dish) super.clone();
		} catch (CloneNotSupportedException e) {
			LOG.error("Error while cloning: " + e.getMessage(), e);
			throw new RuntimeException();
		}
	}
}
