/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * All properties provided by the Core Manager.
 *
 * @author Harry
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {

	@JsonAlias("interfaces")
	private List<DeviceIPAddress> listIPAddress = new LinkedList<>();

	@JsonAlias("hardwareId")
	private String deviceId;

	@JsonAlias("model")
	private String deviceModel;

	@JsonAlias("name")
	private String deviceName;

	@JsonAlias("firmware")
	private String firmwareVersion;

	@JsonAlias("serialNo")
	private String serialNumber;

	/**
	 * Retrieves {@code {@link #listIPAddress}}
	 *
	 * @return value of {@link #listIPAddress}
	 */
	public List<DeviceIPAddress> getListIPAddress() {
		return listIPAddress;
	}

	/**
	 * Sets {@code listIPAddress}
	 *
	 * @param listIPAddress the {@code java.util.List<com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddress>} field
	 */
	public void setListIPAddress(List<DeviceIPAddress> listIPAddress) {
		this.listIPAddress = listIPAddress;
	}

	/**
	 * Retrieves {@code {@link #deviceId}}
	 *
	 * @return value of {@link #deviceId}
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets {@code deviceId}
	 *
	 * @param deviceId the {@code java.lang.String} field
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Retrieves {@code {@link #deviceModel}}
	 *
	 * @return value of {@link #deviceModel}
	 */
	public String getDeviceModel() {
		return deviceModel;
	}

	/**
	 * Sets {@code deviceModel}
	 *
	 * @param deviceModel the {@code java.lang.String} field
	 */
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	/**
	 * Retrieves {@code {@link #deviceName}}
	 *
	 * @return value of {@link #deviceName}
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * Sets {@code deviceName}
	 *
	 * @param deviceName the {@code java.lang.String} field
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * Retrieves {@code {@link #firmwareVersion}}
	 *
	 * @return value of {@link #firmwareVersion}
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * Sets {@code firmwareVersion}
	 *
	 * @param firmwareVersion the {@code java.lang.String} field
	 */
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	
	/**
	 * Retrieves {@code {@link #serialNumber}}
	 *
	 * @return value of {@link #serialNumber}
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets {@code serialNumber}
	 *
	 * @param serialNumber the {@code java.lang.String} field
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
