/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto.rpc.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMethod;

/**
 * Get Components Request DTO for getting all components in system
 *
 * @author Harry
 * @since 1.0.0
 */
public class GetComponentsRequest extends RpcRequest {

	@JsonProperty("params")
	private final String params = QSYSCoreConstant.TEST;

	public GetComponentsRequest() {
		this.method = QSYSCoreControllingMethod.COMPONENT_GET_COMPONENTS.getName();
	}
}
