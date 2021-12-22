/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Device IP Address provided by the Core Manager.
 *
 * @author Harry
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceIPAddress {

	@JsonAlias("name")
	private String name;

	@JsonAlias("ipAddress")
	private String ipAddress;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets {@code name}
	 *
	 * @param name the {@code java.lang.String} field
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #ipAddress}}
	 *
	 * @return value of {@link #ipAddress}
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets {@code ipAddress}
	 *
	 * @param ipAddress the {@code java.lang.String} field
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
