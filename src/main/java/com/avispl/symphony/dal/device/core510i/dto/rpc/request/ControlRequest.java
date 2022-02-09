/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Control Request DTO for request control device
 *
 * @author Harry
 * @since 1.0.0
 */
public class ControlRequest extends RpcRequest {

	@JsonProperty("params")
	private ControlParam params;

	public ControlRequest(String method, ControlParam params) {
		this.method = method;
		this.params = params;
	}

	/**
	 * Retrieves {@code {@link #params}}
	 *
	 * @return value of {@link #params}
	 */
	public ControlParam getParams() {
		return params;
	}

	/**
	 * Sets {@code params}
	 *
	 * @param params the {@code com.avispl.symphony.dal.device.core510i.dto.rpc.request.GetGainControlParams} field
	 */
	public void setParams(ControlParam params) {
		this.params = params;
	}
}