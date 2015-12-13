package com.epickur.api.entity;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Log entity.
 * 
 * @author cph
 * @version 1.0
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "time", "url", "args", "method", "protocol", "remoteAddr", "userAgent" })
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Log extends AbstractMainDBEntity {

	/** Date */
	private DateTime time;
	/** Url */
	private String url;
	/** Url arguments */
	private Map<String, String> args;
	/** Method */
	private String method;
	/** Protocol */
	private String protocol;
	/** Remote address */
	private String remoteAddr;
	/** User agent */
	private String userAgent;

	/**
	 * @return The date
	 */
	@JsonSerialize(using = DateSerializer.class)
	public DateTime getTime() {
		return time;
	}

	/**
	 * @param time The time
	 */
	@JsonDeserialize(using = DateDeserializer.class)
	public void setTime(final DateTime time) {
		this.time = time;
	}

	/**
	 * @return The arguments
	 */
	public Map<String, String> getArgs() {
		if (this.args == null) {
			this.args = new HashMap<>();
		}
		return args;
	}
}
