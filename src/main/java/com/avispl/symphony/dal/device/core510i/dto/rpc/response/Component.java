/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Component DTO contains all info of a component
 *
 * @author Harry
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Component {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("Properties")
	private List<ComponentProperty> properties = new ArrayList<>();

	@JsonProperty("Controls")
	private List<ComponentControl> controls = new ArrayList<>();

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@code {@link #type}}
	 *
	 * @return value of {@link #type}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Retrieves {@code {@link #properties}}
	 *
	 * @return value of {@link #properties}
	 */
	public List<ComponentProperty> getProperties() {
		return properties;
	}

	/**
	 * Retrieves {@code {@link #controls}}
	 *
	 * @return value of {@link #controls}
	 */
	public List<ComponentControl> getControls() {
		return controls;
	}
}
