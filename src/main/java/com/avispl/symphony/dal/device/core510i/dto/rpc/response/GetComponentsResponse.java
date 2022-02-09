/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.dto.rpc.Rpc;

/**
 * Response DTO For Get Components Request
 *
 * @author Harry
 * @since 1.0.0
 */
public class GetComponentsResponse extends Rpc {

	@JsonProperty("result")
	private List<Component> components = new ArrayList<>();

	/**
	 * Retrieves {@code {@link #components}}
	 *
	 * @return value of {@link #components}
	 */
	public List<Component> getComponents() {
		return components;
	}
}
