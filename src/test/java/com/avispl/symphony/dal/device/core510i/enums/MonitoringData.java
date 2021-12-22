/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.enums;

/**
 * This class used to define all values of device info monitoring metrics
 *
 * @author Harry
 * @since 1.0
 */
public enum MonitoringData {
	DEVICE_ID("3-440F59FA6034C59670FF3C0928929607"),
	DEVICE_MODEL("Core 110f"),
	DEVICE_NAME("CeeSalt-Core110f"),
	SERIAL_NUMBER("C121902G4"),
	FIRMWARE_VERSION("9.2.1-2110.001"),
	IP_ADDRESS_NAME_1("LAN A"),
	IP_ADDRESS_NAME_2("LAN B"),
	IP_ADDRESS_1("169.254.232.117"),
	IP_ADDRESS_2("10.8.50.160");

	private final String name;

	MonitoringData(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
