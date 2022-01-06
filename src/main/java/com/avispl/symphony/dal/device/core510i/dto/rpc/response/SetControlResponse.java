/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.dto.rpc.Rpc;

/**
 * Response DTO For Set Control Request
 *
 * @author Harry
 * @since 1.0.0
 */
public class SetControlResponse extends Rpc {

	@JsonProperty("result")
	private boolean result;

	/**
	 * Retrieves {@code {@link #result}}
	 *
	 * @return value of {@link #result}
	 */
	public boolean getResult() {
		return result;
	}
}
