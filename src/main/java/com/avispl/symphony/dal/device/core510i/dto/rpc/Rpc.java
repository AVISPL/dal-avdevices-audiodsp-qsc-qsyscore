/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;

/**
 * Rpc DTO use for send and receive response
 *
 * @author Harry
 * @since 1.0.0
 */
public class Rpc {

	@JsonProperty("jsonrpc")
	protected final String jsonrpc = QSYSCoreConstant.DEFAULT_RPC_VERSION;

	@JsonProperty("id")
	protected final int id = QSYSCoreConstant.DEFAULT_ID;
}

