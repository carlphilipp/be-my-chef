package com.epickur.api.entity;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.epickur.api.exception.EpickurParsingException;
import com.epickur.api.validator.operation.Update;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Map.Entry;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractMainDBEntity extends AbstractEntity {

	/**
	 * Id
	 */
	@JsonView(User.PublicView.class)
	@NotNull(message = "{id.null}", groups = { Update.class })
	private ObjectId id;
	/**
	 * Created at
	 */
	private DateTime createdAt;
	/**
	 * Updated at
	 */
	private DateTime updatedAt;

	/**
	 * @return An ObjectId
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public final ObjectId getId() {
		return id;
	}

	/**
	 * @param id An ObjectId
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public final void setId(final ObjectId id) {
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
	 * @param createdAt The creation date
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
	 * @param updatedAt The updated date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public void setUpdatedAt(final DateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * Prepare the entity to be inserted into DB.
	 */
	public void prepareForInsertionIntoDB() {
		final DateTime time = new DateTime();
		setCreatedAt(time);
		setUpdatedAt(time);

		setId(null);
	}

	/**
	 * Prepare the entity to be updated into DB.
	 */
	public void prepareForUpdateIntoDB() {
		final DateTime time = new DateTime();
		this.setCreatedAt(null);
		this.setUpdatedAt(time);
	}

	/**
	 * @return a Document
	 * @throws EpickurParsingException If an epickur exception occurred
	 */
	@JsonIgnore
	public Document getUpdateQuery() throws EpickurParsingException {
		final String apiView = toStringAPIView();
		final Document found = Document.parse(apiView);
		final Document args = new Document();
		final Document result = new Document().append("$set", args);
		final Set<Entry<String, Object>> set = found.entrySet();
		for (Entry<String, Object> entry : set) {
			String k = entry.getKey();
			if (!k.equals("id")) {
				args.put(k, found.get(k));
			}
		}
		return result;
	}
}
