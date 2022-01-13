/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

import java.util.Objects;

/**
 * Set of controlling Metrics keys
 *
 * @author Harry
 * @since 1.0.0
 */
public enum QSYSCoreControllingMetric {

	CURRENT_GAIN_VALUE("#Gain Current Value", null),
	GAIN_VALUE_CONTROL("#Gain Value Control", "gain"),
	MUTE_CONTROL("#Mute", "mute"),
	ERROR_MESSAGE("#Error Message", null);

	private final String metric;
	private final String property;

	/**
	 * QSYSCoreControllingMetric with arg constructor
	 *
	 * @param metric name of the metric
	 * @param property of control
	 */
	QSYSCoreControllingMetric(String metric, String property) {
		this.metric = metric;
		this.property = property;
	}

	/**
	 * Retrieves {@code {@link #metric }}
	 *
	 * @return value of {@link #metric}
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * Retrieves {@code {@link #property}}
	 *
	 * @return value of {@link #property}
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Get metric of metric from QSYSCoreControllingMetric
	 *
	 * @param metric metric of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static QSYSCoreControllingMetric getByMetric(String metric) {
		for (QSYSCoreControllingMetric controllingMetric : QSYSCoreControllingMetric.values()) {
			if (Objects.equals(controllingMetric.getMetric(), metric)) {
				return controllingMetric;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with metric: " + metric);
	}
}
