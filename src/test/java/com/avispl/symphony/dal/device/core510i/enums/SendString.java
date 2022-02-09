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
public enum SendString {

	STATUS_GET("{\"jsonrpc\":\"2.0\",\"method\":\"StatusGet\",\"id\":1234,\"params\":0}\00"),
	WRONG_FORMAT("wrong format");

	private final String jsonRpc;

	SendString(String jsonRpc) {
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
