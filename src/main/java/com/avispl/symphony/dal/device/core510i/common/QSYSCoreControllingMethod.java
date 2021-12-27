/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

import java.util.Objects;

/**
 * Set of controlling methods
 *
 * @author Harry
 * @since 1.0.0
 */
public enum QSYSCoreControllingMethod {

	COMPONENT_GET("Component.Get"),
	COMPONENT_SET("Component.Set"),
	COMPONENT_GET_COMPONENTS("Component.GetComponents");

	private final String name;

	/**
	 * QSYSCoreControllingMethod with arg constructor
	 *
	 * @param name name of the metric
	 */
	QSYSCoreControllingMethod(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get name of method from QSYSCoreControllingMethod
	 *
	 * @param name name of metric
	 * @return Enum of QSYSCoreControllingMetric
	 */
	public static QSYSCoreControllingMethod getByName(String name) {
		for (QSYSCoreControllingMethod method : QSYSCoreControllingMethod.values()) {
			if (Objects.equals(method.getName(), name)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Cannot find the enum with name: " + name);
	}
}
