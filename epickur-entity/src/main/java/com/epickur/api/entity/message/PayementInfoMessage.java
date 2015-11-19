package com.epickur.api.entity.message;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PayementInfoMessage {
	private String id;
	private String name;
	private Integer amount;
	private String start;
	private String end;
	private String format;
	private List<String> orders;
}
