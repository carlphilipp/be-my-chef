package com.epickur.api.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DeletedMessage {
	private String id;
	private Boolean deleted;
}
