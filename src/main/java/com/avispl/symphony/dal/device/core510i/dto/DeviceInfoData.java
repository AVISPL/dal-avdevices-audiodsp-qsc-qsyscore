/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * All properties provided by the Core Manager.
 * <li>List IP Address</li>
 * <li>Device ID</li>
 * <li>Device model</li>
 * <li>Device name</li>
 * <li>Firmware</li>
 * <li>Serial number</li>
 *
 * @author Harry
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfoData {

	@JsonAlias("interfaces")
	private Set<DeviceIPAddressData> listIPAddress = new HashSet<>();

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
	public Set<DeviceIPAddressData> getListIPAddress() {
		return listIPAddress;
	}

	/**
	 * Sets {@code listIPAddress}
	 *
	 * @param listIPAddress the {@code java.util.Set<com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddressData>} field
	 */
	public void setListIPAddress(Set<DeviceIPAddressData> listIPAddress) {
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
