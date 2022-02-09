/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Control Property DTO contains name and value of control
 *
 * @author Harry
 * @since 1.0.0
 */
@JsonInclude(Include.NON_NULL)
public class ControlProperty {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Value")
	private Double value;

	public ControlProperty(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Sets {@code value}
	 *
	 * @param value the {@code java.lang.Double} field
	 */
	public void setValue(Double value) {
		this.value = value;
	}
}
