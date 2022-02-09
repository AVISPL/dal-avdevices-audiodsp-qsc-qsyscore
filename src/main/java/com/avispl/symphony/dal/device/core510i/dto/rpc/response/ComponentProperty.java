/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component Property DTO contain all control property of Component
 *
 * @author Harry
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class ComponentProperty {
	@JsonProperty("Name")
	private String name;

	@JsonProperty("Value")
	private String value;

	@JsonProperty("PrettyName")
	private String prettyName;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Retrieves {@code {@link #prettyName}}
	 *
	 * @return value of {@link #prettyName}
	 */
	public String getPrettyName() {
		return prettyName;
	}
}
