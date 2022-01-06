/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMetric;

/**
 * DTO for update local extended statistic
 *
 * @author Harry
 * @since 1.0
 */
public class UpdateLocalExtStat {

	private String property;
	private String value;
	private String namedComponent;
	private QSYSCoreControllingMetric controllingMetric;

	/**
	 * Parameterized constructor
	 */
	public UpdateLocalExtStat(String property, String value, String namedComponent, QSYSCoreControllingMetric controllingMetric) {
		this.property = property;
		this.value = value;
		this.namedComponent = namedComponent;
		this.controllingMetric = controllingMetric;
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
	 * Sets {@code property}
	 *
	 * @param property the {@code java.lang.String} field
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Retrieves {@code {@link #value}}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets {@code value}
	 *
	 * @param value the {@code java.lang.String} field
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@code {@link #namedComponent}}
	 *
	 * @return value of {@link #namedComponent}
	 */
	public String getNamedComponent() {
		return namedComponent;
	}

	/**
	 * Sets {@code namedComponent}
	 *
	 * @param namedComponent the {@code java.lang.String} field
	 */
	public void setNamedComponent(String namedComponent) {
		this.namedComponent = namedComponent;
	}

	/**
	 * Retrieves {@code {@link #controllingMetric}}
	 *
	 * @return value of {@link #controllingMetric}
	 */
	public QSYSCoreControllingMetric getControllingMetric() {
		return controllingMetric;
	}

	/**
	 * Sets {@code controllingMetric}
	 *
	 * @param controllingMetric the {@code com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMetric} field
	 */
	public void setControllingMetric(QSYSCoreControllingMetric controllingMetric) {
		this.controllingMetric = controllingMetric;
	}
}
