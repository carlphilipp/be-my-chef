package com.epickur.api.entity;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractMainDBEntity extends AbstractEntity {

	/** Id */
	private ObjectId id;
	/** Created at */
	private DateTime createdAt;
	/** Updated at */
	private DateTime updatedAt;

	/**
	 * @return An ObjectId
	 */
	@JsonSerialize(using = ObjectIdSerializer.class)
	public final ObjectId getId() {
		return id;
	}

	/**
	 * @param id
	 *            An ObjectId
	 */
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public final void setId(final ObjectId id) {
		this.id = id;
	}

	/**
	 * @return The creation date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public final DateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 *            The creation date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public final void setCreatedAt(final DateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return The updated date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public final DateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt
	 *            The updated date
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public final void setUpdatedAt(final DateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * Prepare the entity to be inserted into DB.
	 */
	public void prepareForInsertionIntoDB() {
		DateTime time = new DateTime();
		setCreatedAt(time);
		setUpdatedAt(time);

		setId(null);
	}

	/**
	 * Prepare the entity to be updated into DB.
	 */
	public void prepareForUpdateIntoDB() {
		DateTime time = new DateTime();
		this.setCreatedAt(null);
		this.setUpdatedAt(time);
	}
}
