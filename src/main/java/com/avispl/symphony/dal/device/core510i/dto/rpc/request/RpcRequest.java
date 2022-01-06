/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.dto.rpc.Rpc;

/**
 * Rpc Request DTO for sending request to device
 *
 * @author Harry
 * @since 1.0.0
 */
public class RpcRequest extends Rpc {

	@JsonProperty("method")
	protected String method;
}
