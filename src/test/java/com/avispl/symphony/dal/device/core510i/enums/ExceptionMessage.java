/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.enums;

/**
 * This class used to define all values of exception messages
 *
 * @author Harry
 * @since 1.0
 */
public enum ExceptionMessage {

	GETTING_DEVICE_INFO_ERR("failed to get device info"),
	GETTING_DEVICE_IP_ERR("failed to get device IP Address");

	private final String message;

	/**
	 * Parameterized constructor
	 *
	 * @param message Exception message
	 */
	ExceptionMessage(String message) {
		this.message = message;
	}

	/**
	 * retrieve {@code {@link #message }}
	 *
	 * @return value of {@link #message}
	 */
	public String getMessage() {
		return this.message;
	}
}
