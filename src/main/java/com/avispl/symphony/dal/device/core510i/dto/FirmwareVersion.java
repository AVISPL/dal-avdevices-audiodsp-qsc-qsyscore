/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.device.core510i.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Device Firmware version provided by the Core Manager.
 * <li>build name</li>
 *
 * @author Harry
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirmwareVersion {

	@JsonAlias("buildName")
	private String buildName;

	/**
	 * Retrieves {@code {@link #buildName}}
	 *
	 * @return value of {@link #buildName}
	 */
	public String getBuildName() {
		return buildName;
	}

	/**
	 * Sets {@code buildName}
	 *
	 * @param buildName the {@code java.lang.String} field
	 */
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}
}
