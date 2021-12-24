/*
 * Copyright (c) 2021 AVI-SPL Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.device.core510i.enums;

/**
 * This class used to define all send string
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
public enum ResponseString {
	
	ENGINE_STATUS(
			"{\"jsonrpc\":\"2.0\",\"method\":\"EngineStatus\",\"params\":{\"Platform\":\"Core 110f\",\"State\":\"Active\",\"DesignName\":\"CeeSalt_TestCore_v3.1\",\"DesignCode\":\"joqw53I2hUnY\",\"IsRedundant\":false,\"IsEmulator\":false,\"Status\":{\"Code\":0,\"String\":\"OK - 11 OK\"}}}"),
	STATUS_RESPONSE(
			"{\"jsonrpc\":\"2.0\",\"result\":{\"Platform\":\"Core 110f\",\"State\":\"Active\",\"DesignName\":\"CeeSalt_TestCore_v3.1\",\"DesignCode\":\"joqw53I2hUnY\",\"IsRedundant\":false,\"IsEmulator\":false,\"Status\":{\"Code\":0,\"String\":\"OK - 11 OK\"}},\"id\":1234}");

	private final String jsonRpc;

	ResponseString(String jsonRpc) {
		this.jsonRpc = jsonRpc;
	}

	/**
	 * Retrieves {@code {@link #jsonRpc}}
	 *
	 * @return value of {@link #jsonRpc}
	 */
	public String getJsonRpc() {
		return jsonRpc;
	}
}
