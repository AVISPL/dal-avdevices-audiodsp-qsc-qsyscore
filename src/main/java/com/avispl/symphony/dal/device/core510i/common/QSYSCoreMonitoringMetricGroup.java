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

	/**
	 * Parameterized Constructor
	 *
	 * @param name Name of QSYS Core monitoring metric group
	 * @param isFailedMonitor Group is failed to monitor
	 */
	QSYSCoreMonitoringMetricGroup(String name, boolean isFailedMonitor) {
		this.name = name;
		this.isFailedMonitorCheck = isFailedMonitor;
	}

	/**
	 * retrieve {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @return isFailedMonitorCheck
	 */
	public boolean isFailedMonitorCheck() {
		return isFailedMonitorCheck;
	}
}
