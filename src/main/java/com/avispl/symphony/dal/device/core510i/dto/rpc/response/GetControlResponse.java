/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.dto.rpc.Rpc;

/**
 * Response DTO For Get Control Request
 *
 * @author Harry
 * @since 1.0.0
 */
public class GetControlResponse extends Rpc {

	@JsonProperty("result")
	private Component component;

	/**
	 * Retrieves {@code {@link #component}}
	 *
	 * @return value of {@link #component}
	 */
	public Component getComponent() {
		return component;
	}
}
