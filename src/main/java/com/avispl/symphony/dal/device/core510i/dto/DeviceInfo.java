/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * All Data provided by the Core Manager to retrieve device info.
 *
 * @author Harry
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {

	@JsonAlias("data")
	private DeviceInfoData deviceInfoData;

	/**
	 * Retrieves {@code {@link #deviceInfoData}}
	 *
	 * @return value of {@link #deviceInfoData}
	 */
	public DeviceInfoData getDeviceInfoData() {
		return deviceInfoData;
	}

	/**
	 * Sets {@code deviceInfoData}
	 *
	 * @param deviceInfoData the {@code com.avispl.symphony.dal.device.core510i.dto.DeviceInfoData} field
	 */
	public void setDeviceInfoData(DeviceInfoData deviceInfoData) {
		this.deviceInfoData = deviceInfoData;
	}
}

