/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

/**
 * Set of monitoring Metric Groups keys
 *
 * @author Harry
 * @since 1.0
 */
public enum QSYSCoreMonitoringMetricGroup {

	DEVICE_INFO("DeviceInfo", true),
	IP_ADDRESS("IPAddress", true);

	private final String name;
	private final boolean isFailedMonitorCheck;

	QSYSCoreMonitoringMetricGroup(String name, boolean isFailedMonitor) {
		this.name = name;
		this.isFailedMonitorCheck = isFailedMonitor;
	}

	public String getName() {
		return this.name;
	}

	public boolean isFailedMonitorCheck() {
		return isFailedMonitorCheck;
	}
}
