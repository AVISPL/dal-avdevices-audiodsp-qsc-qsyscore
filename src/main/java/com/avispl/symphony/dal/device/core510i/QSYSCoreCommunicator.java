/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.avispl.symphony.dal.device.core510i.dto.DeviceInfo;
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
	 * Retrieve device login information
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
	private String checkNoneData(String value) {
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
	 * Value of list statistics property of device info is NONE
	 *
	 * @param failedMonitor list statistics property
	 */
	private void updateDeviceInfoFailedMonitor(Map<String, String> failedMonitor) {
		failedMonitor.put(QSYSCoreMonitoringMetricGroup.DEVICE_INFO.getName(), QSYSCoreConstant.GETTING_DEVICE_INFO_ERR);
	}

	/**
	 * Retrieve device information
	 *
	 * @param stats list statistics property
	 */
	private void retrieveDeviceInfo(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				JsonNode response = doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_INFO), JsonNode.class);

				if (!response.get(QSYSCoreConstant.DATA).isEmpty()) {
					ObjectMapper objectMapper = new ObjectMapper();
					DeviceInfo deviceInfo = objectMapper.readValue(response.get(QSYSCoreConstant.DATA).toString(), DeviceInfo.class);

					stats.put(QSYSCoreMonitoringMetric.DEVICE_ID.getName(), checkNoneData(deviceInfo.getDeviceId()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_NAME.getName(), checkNoneData(deviceInfo.getDeviceName()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName(), checkNoneData(deviceInfo.getDeviceModel()));
					stats.put(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName(), checkNoneData(deviceInfo.getFirmwareVersion()));
					stats.put(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName(), checkNoneData(deviceInfo.getSerialNumber()));
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
	 * Value of list statistics property of device info is NONE
	 *
	 * @param failedMonitor list statistics property
	 */
	private void updateDeviceIPFailedMonitor(Map<String, String> failedMonitor) {
		failedMonitor.put(QSYSCoreMonitoringMetricGroup.IP_ADDRESS.getName(), QSYSCoreConstant.GETTING_DEVICE_IP_ERR);
	}

	/**
	 * Retrieve device IP address
	 *
	 * @param stats list statistics property
	 */
	private void retrieveDeviceIPAddress(Map<String, String> stats) {
		try {
			if (this.loginInfo.getToken() != null) {
				JsonNode response = doGet(buildDeviceFullPath(QSYSCoreURL.BASE_URI + QSYSCoreURL.DEVICE_IP_ADDRESS), JsonNode.class);

				if (!response.get(QSYSCoreConstant.DATA).isEmpty()) {
					ObjectMapper objectMapper = new ObjectMapper();
					DeviceInfo deviceInfo = objectMapper.readValue(response.get(QSYSCoreConstant.DATA).toString(), DeviceInfo.class);

					for (DeviceIPAddress ipAddress : deviceInfo.getListIPAddress()) {
						stats.put(QSYSCoreMonitoringMetricGroup.DEVICE_INFO.getName() + QSYSCoreConstant.HASH + ipAddress.getName(), checkNoneData(ipAddress.getIpAddress()));
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
	 * Count metric historical in the metrics
	 *
	 * @return number historical in the metric
	 */
	private int getNoOfFailedMonitorMetric() {
		int noOfFailedMonitorMetric = 0;
		for (QSYSCoreMonitoringMetricGroup metric : QSYSCoreMonitoringMetricGroup.values()) {
			if (metric.isFailedMonitorCheck()) {
				noOfFailedMonitorMetric++;
			}
		}
		return noOfFailedMonitorMetric;
	}

	/**
	 * Retrieve data and add to stats
	 *
	 * @param stats list statistic property
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

		if (failedMonitor.size() == getNoOfFailedMonitorMetric()) {
			StringBuilder errBuilder = new StringBuilder();
			for (Map.Entry<String, String> failedMetric : failedMonitor.entrySet()) {
				errBuilder.append(failedMetric.getValue());
				errBuilder.append(QSYSCoreConstant.NEXT_LINE);
			}
			throw new ResourceNotReachableException(errBuilder.toString());
		}
	}
}
