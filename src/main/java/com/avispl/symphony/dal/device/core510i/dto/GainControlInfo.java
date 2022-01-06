/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

/**
 * Gain Control Info DTO for control gain component
 *
 * @author Harry
 * @since 1.0
 */
public class GainControlInfo {

	private String name;
	private String gainString;
	private double gainValue;
	private double mutePosition;
	private String minGain;
	private String maxGain;

	/**
	 * Retrieves {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
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
	 * Retrieves {@code {@link #gainString}}
	 *
	 * @return value of {@link #gainString}
	 */
	public String getGainString() {
		return gainString;
	}

	/**
	 * Sets {@code gainString}
	 *
	 * @param gainString the {@code java.lang.String} field
	 */
	public void setGainString(String gainString) {
		this.gainString = gainString;
	}

	/**
	 * Retrieves {@code {@link #gainValue }}
	 *
	 * @return value of {@link #gainValue}
	 */
	public double getGainValue() {
		return gainValue;
	}

	/**
	 * Sets {@code gainPosition}
	 *
	 * @param gainValue the {@code double} field
	 */
	public void setGainValue(double gainValue) {
		this.gainValue = gainValue;
	}

	/**
	 * Retrieves {@code {@link #mutePosition}}
	 *
	 * @return value of {@link #mutePosition}
	 */
	public double getMutePosition() {
		return mutePosition;
	}

	/**
	 * Sets {@code mutePosition}
	 *
	 * @param mutePosition the {@code double} field
	 */
	public void setMutePosition(double mutePosition) {
		this.mutePosition = mutePosition;
	}

	/**
	 * Retrieves {@code {@link #minGain}}
	 *
	 * @return value of {@link #minGain}
	 */
	public String getMinGain() {
		return minGain;
	}

	/**
	 * Sets {@code minGain}
	 *
	 * @param minGain the {@code java.lang.String} field
	 */
	public void setMinGain(String minGain) {
		this.minGain = minGain;
	}

	/**
	 * Retrieves {@code {@link #maxGain}}
	 *
	 * @return value of {@link #maxGain}
	 */
	public String getMaxGain() {
		return maxGain;
	}

	/**
	 * Sets {@code maxGain}
	 *
	 * @param maxGain the {@code java.lang.String} field
	 */
	public void setMaxGain(String maxGain) {
		this.maxGain = maxGain;
	}
}
