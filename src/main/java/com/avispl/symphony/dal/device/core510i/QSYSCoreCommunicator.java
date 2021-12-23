/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreMonitoringMetric;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreMonitoringMetricGroup;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreURL;
import com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddress;
import com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddressData;
import com.avispl.symphony.dal.device.core510i.dto.DeviceInfo;
import com.avispl.symphony.dal.device.core510i.dto.DeviceInfoData;
import com.avispl.symphony.dal.device.core510i.dto.LoginInfo;

/**
 * QSC Q-SYS Core 510i Device Adapter
 *
 * Monitored Statistics:
 * <li>
 * Device Name
 * Device ID
 * Device Model
 * Firmware
 * Serial Number
 * Set of IP Address
 * </li>
 *
 * Controlled Statistics:
 * <li>Level control</li>
 *
 * @author Harry
 * @since 1.0
 */
public class QSYSCoreCommunicator extends RestCommunicator implements Monitorable, Controller {

	private Map<String, String> failedMonitor;
	private LoginInfo loginInfo;

	@Override
	protected void authenticate() {
		// The device has its own authentication behavior, do not use the common one
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control specific property
	 *
	 * @param controllableProperty This is the property to be controlled
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) {
		// I am writing
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) {
		// I am writing
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() {
		Map<String, String> stats = new HashMap<>();
		failedMonitor = new HashMap<>();
		loginInfo = initLoginInfo();
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();

		populateQSYSMonitoringMetrics(stats);
		extendedStatistics.setStatistics(stats);

		return Collections.singletonList(extendedStatistics);
	}

	/**
	 * @return HttpHeaders contain Bearer token for authorization
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		headers.set("Content-Type", "text/xml");
		headers.set("Content-Type", "application/json");

		String token = loginInfo.getToken();
		if (token != null && !token.equals(QSYSCoreConstant.AUTHORIZED)) {
			headers.setBearerAuth(loginInfo.getToken());
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * Init instance of LoginInfo
	 *
	 * @return LoginInfo
	 */
	protected LoginInfo initLoginInfo() {
		return new LoginInfo();
	}

	/**
	 * @param path url of the request
	 * @return String full path of the device
	 */
	private String buildDeviceFullPath(String path) {
		Objects.requireNonNull(path);

		return QSYSCoreConstant.HTTP
				+ getHost()
				+ path;
	}

	/**
	 * This method is used to retrieve device login information (Token) by send POST request to https://***REMOVED***/api/v0/logon
	 *
	 * When there is no token data or having an Exception, The Token of login information is going to set with null value
	 */
	private void retrieveTokenFromCore() {
		String login = getLogin();
		String password = getPassword();

		ObjectNode request = JsonNodeFactory.instance.objectNode();
		request.put(QSYSCoreConstant.USERNAME, login);
		request.put(QSYSCoreConstant.PASSWORD, password);

		try {
			if (this.loginInfo.isTimeout() || this.loginInfo.getToken() == null) {
				JsonNode responseData = doPost(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.TOKEN), request, JsonNode.class);

				if (responseData != null) {
					String token = responseData.get(QSYSCoreConstant.TOKEN).asText();
					if (token != null) {
						this.loginInfo.setToken(token);
						this.loginInfo.setLoginDateTime(System.currentTimeMillis());
					} else {
						this.loginInfo.setToken(null);
					}
				}
			}
		} catch (Exception e) {
			this.loginInfo.setToken(null);
		}
	}

	/**
	 * @param value value of device info
	 * @return String (none/value)
	 */
	private String checkForNullData(String value) {
		return value.equals("") ? QSYSCoreConstant.NONE : value;
	}

	/**
	 * Value of list statistics property of device info is NONE
	 *
	 * @param stats list statistics property
	 */
	private void contributeNoneValueForDeviceStatistics(Map<String, String> stats) {
		stats.put(QSYSCoreMonitoringMetric.DEVICE_ID.getName(), QSYSCoreConstant.NONE);
		stats.put(QSYSCoreMonitoringMetric.DEVICE_NAME.getName(), QSYSCoreConstant.NONE);
		stats.put(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName(), QSYSCoreConstant.NONE);
		stats.put(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName(), QSYSCoreConstant.NONE);
		stats.put(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName(), QSYSCoreConstant.NONE);
	}

	/**
	 * Update failedMonitor with Getting device info error message
	 *
	 * @param failedMonitor list statistics property
	 */
	private void updateDeviceInfoFailedMonitor(Map<String, String> failedMonitor) {
		failedMonitor.put(QSYSCoreMonitoringMetricGroup.DEVICE_INFO.getName(), QSYSCoreConstant.GETTING_DEVICE_INFO_ERR);
	}

	/**
	 * This method is used to retrieve device information by send GET request to http://***REMOVED***/api/v0/cores/self?meta=permissions
	 * <li>Device ID</li>
	 * <li>Device name</li>
	 * <li>Device model</li>
	 * <li>Firmware version</li>
	 * <li>Serial number</li>
	 *
	 * @param stats list statistics property
	 *
	 * When token is null, the stats is going to contribute with NONE value and the failedMonitor is going to update
	 * When there is no response data, the stats is going to contribute with none value and the failedMonitor is going to update
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveDeviceInfo(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				JsonNode response = doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_INFO), JsonNode.class);

				if (!response.get(QSYSCoreConstant.DATA).isEmpty()) {
					ObjectMapper objectMapper = new ObjectMapper();
					DeviceInfo deviceInfo = objectMapper.readValue(response.toString(), DeviceInfo.class);
					DeviceInfoData deviceInfoData = deviceInfo.getDeviceInfoData();

					stats.put(QSYSCoreMonitoringMetric.DEVICE_ID.getName(), checkForNullData(deviceInfoData.getDeviceId()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_NAME.getName(), checkForNullData(deviceInfoData.getDeviceName()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName(), checkForNullData(deviceInfoData.getDeviceModel()));
					stats.put(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName(), checkForNullData(deviceInfoData.getFirmwareVersion()));
					stats.put(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName(), checkForNullData(deviceInfoData.getSerialNumber()));
				} else {
					contributeNoneValueForDeviceStatistics(stats);
					updateDeviceInfoFailedMonitor(failedMonitor);
				}
			} else {
				contributeNoneValueForDeviceStatistics(stats);
				updateDeviceInfoFailedMonitor(failedMonitor);
			}
		} catch (Exception e) {
			contributeNoneValueForDeviceStatistics(stats);
			updateDeviceInfoFailedMonitor(failedMonitor);
		}
	}

	/**
	 * Update failedMonitor with Getting device IP address error message
	 *
	 * @param failedMonitor list statistics property
	 */
	private void updateDeviceIPFailedMonitor(Map<String, String> failedMonitor) {
		failedMonitor.put(QSYSCoreMonitoringMetricGroup.IP_ADDRESS.getName(), QSYSCoreConstant.GETTING_DEVICE_IP_ERR);
	}

	/**
	 * This method is used to retrieve Set of device IP address by send GET request to http://***REMOVED***/api/v0/cores/self/config/network?meta=permissions&include=autoDns
	 *
	 * @param stats list statistics property
	 *
	 * When token is null, the failedMonitor is going to update
	 * When there is no response data, the failedMonitor is going to update
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveDeviceIPAddress(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				JsonNode response = doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_IP_ADDRESS), JsonNode.class);

				if (!response.get(QSYSCoreConstant.DATA).isEmpty()) {
					ObjectMapper objectMapper = new ObjectMapper();
					DeviceIPAddress deviceIPAddress = objectMapper.readValue(response.toString(), DeviceIPAddress.class);
					Set<DeviceIPAddressData> deviceIPAddressDataList = deviceIPAddress.getDeviceInfoData().getListIPAddress();

					for (DeviceIPAddressData ipAddress : deviceIPAddressDataList) {
						stats.put(QSYSCoreMonitoringMetricGroup.DEVICE_INFO.getName() + QSYSCoreConstant.HASH + ipAddress.getName(), checkForNullData(ipAddress.getIpAddress()));
					}
				} else {
					updateDeviceIPFailedMonitor(failedMonitor);
				}
			} else {
				updateDeviceIPFailedMonitor(failedMonitor);
			}
		} catch (Exception e) {
			updateDeviceIPFailedMonitor(failedMonitor);
		}
	}

	/**
	 * Counting metric group is failed to monitor
	 *
	 * @return number failed monitoring metric group in the metric
	 */
	private int getNoOfFailedMonitorMetricGroup() {
		int noOfFailedMonitorMetric = 0;
		for (QSYSCoreMonitoringMetricGroup metric : QSYSCoreMonitoringMetricGroup.values()) {
			if (metric.isFailedMonitorCheck()) {
				noOfFailedMonitorMetric++;
			}
		}
		return noOfFailedMonitorMetric;
	}

	/**
	 * This method is used to populate all monitoring properties:
	 * <li>Device ID</li>
	 * <li>Device name</li>
	 * <li>Device model</li>
	 * <li>Firmware version</li>
	 * <li>Serial number</li>
	 * <li>IP Address</li>
	 *
	 * @param stats list statistic property
	 *
	 * @throws ResourceNotReachableException when failedMonitor said all device monitoring data are failed to get
	 */
	private void populateQSYSMonitoringMetrics(Map<String, String> stats) {
		Objects.requireNonNull(stats);
		if (getLogin() != null && getPassword() != null) {
			retrieveTokenFromCore();
		} else {
			this.loginInfo.setToken(QSYSCoreConstant.AUTHORIZED);
		}
		retrieveDeviceInfo(stats);
		retrieveDeviceIPAddress(stats);

		if (failedMonitor.size() == getNoOfFailedMonitorMetricGroup()) {
			StringBuilder errBuilder = new StringBuilder();
			for (Map.Entry<String, String> failedMetric : failedMonitor.entrySet()) {
				errBuilder.append(failedMetric.getValue());
				errBuilder.append(QSYSCoreConstant.NEXT_LINE);
			}
			throw new ResourceNotReachableException(errBuilder.toString());
		}
	}
}
