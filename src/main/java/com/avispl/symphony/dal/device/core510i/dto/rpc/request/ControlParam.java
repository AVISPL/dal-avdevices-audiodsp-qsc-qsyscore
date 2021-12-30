/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Control Params DTO contains name list of control property
 *
 * @author Harry
 * @since 1.0.0
 */
public class ControlParam {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Controls")
	private List<ControlProperty> controls = new ArrayList<>();

	public ControlParam(String name) {
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
	 * Retrieves {@code {@link #controls}}
	 *
	 * @return value of {@link #controls}
	 */
	public List<ControlProperty> getControls() {
		return controls;
	}

	/**
	 * Sets {@code controls}
	 *
	 * @param controls the {@code java.util.List<com.avispl.symphony.dal.device.core510i.dto.rpc.request.GainControls>} field
	 */
	public void setControls(List<ControlProperty> controls) {
		this.controls = controls;
	}

	/**
	 * This method used to add control property to control device
	 *
	 * @param controlProperty is the property want to add
	 */
	public void add(ControlProperty controlProperty) {
		this.controls.add(controlProperty);
	}
}
