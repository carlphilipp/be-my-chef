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
import org.joda.time.DateTime;

import com.epickur.api.entity.databind.DateDeserializer;
import com.epickur.api.entity.databind.DateSerializer;
import com.epickur.api.entity.databind.DishTypeDeserializer;
import com.epickur.api.entity.databind.DishTypeSerializer;
import com.epickur.api.entity.databind.ObjectIdDeserializer;
import com.epickur.api.entity.databind.ObjectIdSerializer;
import com.epickur.api.enumeration.DishType;
import com.epickur.api.enumeration.View;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.utils.ObjectMapperWrapperDB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Dish entity
 * 
 * @author cph
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "name", "description", "type", "price", "cookingTime", "difficultyLevel", "videoUrl", "nutritionFacts",
		"ingredients", "steps", "caterer", "createdAt", "updatedAt" })
public final class Dish extends AbstractEntity {

	/** Logger **/
	private static final Logger LOG = LogManager.getLogger(Dish.class.getSimpleName());
	/** Id **/
	private ObjectId id;
	/** Name **/
	private String name;
	/** Description **/
	private String description;
	/** Type **/
	private DishType type;
	/** Price **/
	private Integer price;
	/** Cooking time **/
	private Integer cookingTime;
	/** Difficulty level **/
	private Integer difficultyLevel;
	/** Image After URL **/
	private String imageAfterUrl;
	/** Video URL **/
	private String videoUrl;
	/** Nutrition facts **/
	private List<NutritionFact> nutritionFacts;
	/** Ingredients **/
	private List<Ingredient> ingredients;
	/** Steps **/
	private List<String> steps;
	/** Caterer **/
	private Caterer caterer;
	/** Owner id **/
	private ObjectId createdBy;
	/** Created at **/
	private DateTime createdAt;
	/** Updated at **/
	private DateTime updatedAt;

	/** Constructor **/
	public Dish() {
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
	 * @return The price
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            The price
	 */
	public void setPrice(final Integer price) {
		this.price = price;
	}

	/**
	 * @return The list of Ingredient
	 */
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	/**
	 * @param ingredients
	 *            The list of Ingredient
	 */
	public void setIngredients(final List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	/**
	 * @return The Caterer
	 */
	public Caterer getCaterer() {
		return caterer;
	}

	/**
	 * @param caterer
	 *            The Caterer
	 */
	public void setCaterer(final Caterer caterer) {
		this.caterer = caterer;
	}

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
	 * @return The cooking time
	 */
	public Integer getCookingTime() {
		return cookingTime;
	}

	/**
	 * @param cookingTime
	 *            The cooking time
	 */
	public void setCookingTime(final Integer cookingTime) {
		this.cookingTime = cookingTime;
	}

	/**
	 * @return The difficulty level
	 */
	public Integer getDifficultyLevel() {
		return difficultyLevel;
	}

	/**
	 * @param difficultyLevel
	 *            The difficulty level
	 */
	public void setDifficultyLevel(final Integer difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	/**
	 * @return The list of steps
	 */
	public List<String> getSteps() {
		return steps;
	}

	/**
	 * @param steps
	 *            The list of steps
	 */
	public void setSteps(final List<String> steps) {
		this.steps = steps;
	}

	/**
	 * @return The list of NutritionFact
	 */
	public List<NutritionFact> getNutritionFacts() {
		return nutritionFacts;
	}

	/**
	 * @param nutritionFacts
	 *            The list of NutritionFact
	 */
	public void setNutritionFacts(final List<NutritionFact> nutritionFacts) {
		this.nutritionFacts = nutritionFacts;
	}

	/**
	 * @return The image after url
	 */
	public String getImageAfterUrl() {
		return imageAfterUrl;
	}

	/**
	 * @param videoUrl
	 *            The image after url
	 */
	public void setImageAfterUrl(final String imageAfterUrl) {
		this.imageAfterUrl = imageAfterUrl;
	}
	
	/**
	 * @return The video url
	 */
	public String getVideoUrl() {
		return videoUrl;
	}

	/**
	 * @param videoUrl
	 *            The video url
	 */
	public void setVideoUrl(final String videoUrl) {
		this.videoUrl = videoUrl;
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
					Caterer cat = Caterer.getObject((Document) found.get(key), View.API);
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
	public static Dish getObject(final Document obj) throws EpickurParsingException {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caterer == null) ? 0 : caterer.hashCode());
		result = prime * result + ((cookingTime == null) ? 0 : cookingTime.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((difficultyLevel == null) ? 0 : difficultyLevel.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ingredients == null) ? 0 : ingredients.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nutritionFacts == null) ? 0 : nutritionFacts.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((steps == null) ? 0 : steps.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((videoUrl == null) ? 0 : videoUrl.hashCode());
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
		if (!(obj instanceof Dish)) {
			return false;
		}
		Dish other = (Dish) obj;
		if (caterer == null) {
			if (other.caterer != null) {
				return false;
			}
		} else if (!caterer.equals(other.caterer)) {
			return false;
		}
		if (cookingTime == null) {
			if (other.cookingTime != null) {
				return false;
			}
		} else if (!cookingTime.equals(other.cookingTime)) {
			return false;
		}
		if (createdAt == null) {
			if (other.createdAt != null) {
				return false;
			}
		} else if (!createdAt.equals(other.createdAt)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (difficultyLevel == null) {
			if (other.difficultyLevel != null) {
				return false;
			}
		} else if (!difficultyLevel.equals(other.difficultyLevel)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (ingredients == null) {
			if (other.ingredients != null) {
				return false;
			}
		} else if (!ingredients.equals(other.ingredients)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nutritionFacts == null) {
			if (other.nutritionFacts != null) {
				return false;
			}
		} else if (!nutritionFacts.equals(other.nutritionFacts)) {
			return false;
		}
		if (price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!price.equals(other.price)) {
			return false;
		}
		if (steps == null) {
			if (other.steps != null) {
				return false;
			}
		} else if (!steps.equals(other.steps)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (updatedAt == null) {
			if (other.updatedAt != null) {
				return false;
			}
		} else if (!updatedAt.equals(other.updatedAt)) {
			return false;
		}
		if (videoUrl == null) {
			if (other.videoUrl != null) {
				return false;
			}
		} else if (!videoUrl.equals(other.videoUrl)) {
			return false;
		}
		return true;
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
