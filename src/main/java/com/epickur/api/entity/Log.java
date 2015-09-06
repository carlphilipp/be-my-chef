package com.epickur.api.entity;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import com.epickur.api.entity.deserialize.DateDeserializer;
import com.epickur.api.entity.deserialize.ObjectIdDeserializer;
import com.epickur.api.entity.serialize.DateSerializer;
import com.epickur.api.entity.serialize.ObjectIdSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Log entity.
 * 
 * @author cph
 * @version 1.0
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = { "id", "time", "url", "args", "method", "protocol", "remoteAddr", "userAgent" })
public final class Log extends AbstractEntity {
	/** Id */
	private ObjectId id;
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

	@JsonSerialize(using = ObjectIdSerializer.class)
	public ObjectId getId() {
		return id;
	}

	@JsonDeserialize(using = ObjectIdDeserializer.class)
	public void setId(ObjectId id) {
		this.id = id;
	}

	@JsonSerialize(using = DateSerializer.class)
	public DateTime getTime() {
		return time;
	}

	@JsonDeserialize(using = DateDeserializer.class)
	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(final String userAgent) {
		this.userAgent = userAgent;
	}

	public Map<String, String> getArgs() {
		if (this.args == null) {
			this.args = new HashMap<String, String>();
		}
		return args;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((remoteAddr == null) ? 0 : remoteAddr.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((userAgent == null) ? 0 : userAgent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Log)) {
			return false;
		}
		Log other = (Log) obj;
		if (args == null) {
			if (other.args != null) {
				return false;
			}
		} else if (!args.equals(other.args)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equals(other.method)) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		if (remoteAddr == null) {
			if (other.remoteAddr != null) {
				return false;
			}
		} else if (!remoteAddr.equals(other.remoteAddr)) {
			return false;
		}
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		if (userAgent == null) {
			if (other.userAgent != null) {
				return false;
			}
		} else if (!userAgent.equals(other.userAgent)) {
			return false;
		}
		return true;
	}
}
