/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

/**
 * Set of monitoring Metrics keys
 *
 * @author Harry
 * @since 1.0
 */
public enum QSYSCoreMonitoringMetric {
	
	DEVICE_ID("DeviceInfo#DeviceID"),
	DEVICE_MODEL("DeviceInfo#DeviceModel"),
	DEVICE_NAME("DeviceInfo#DeviceName"),
	FIRMWARE_VERSION("DeviceInfo#FirmwareVersion"),
	SERIAL_NUMBER("DeviceInfo#SerialNumber");

	private final String name;

	/**
	 *Parameterized constructor
	 *
	 * @param name Name of QSYS Core monitoring metric
	 */
	QSYSCoreMonitoringMetric(String name) {
		this.name = name;
	}

	/**
	 * retrieve {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}
}
