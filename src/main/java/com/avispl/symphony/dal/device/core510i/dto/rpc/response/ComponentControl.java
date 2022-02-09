/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component Control DTO contain all control info of Component
 *
 * @author Harry
 * @since 1.0.0
 */
public class ComponentControl {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("String")
	private String string;

	@JsonProperty("Value")
	private Double value;

	@JsonProperty("Position")
	private Double position;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #string}}
	 *
	 * @return value of {@link #string}
	 */
	public String getString() {
		return string;
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
	 * Retrieves {@code {@link #position}}
	 *
	 * @return value of {@link #position}
	 */
	public Double getPosition() {
		return position;
	}
}
