/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.naming.NameNotFoundException;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMethod;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMetric;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreMonitoringMetric;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreMonitoringMetricGroup;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreURL;
import com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddress;
import com.avispl.symphony.dal.device.core510i.dto.DeviceIPAddressData;
import com.avispl.symphony.dal.device.core510i.dto.DeviceInfo;
import com.avispl.symphony.dal.device.core510i.dto.DeviceInfoData;
import com.avispl.symphony.dal.device.core510i.dto.GainControlInfo;
import com.avispl.symphony.dal.device.core510i.dto.LoginInfo;
import com.avispl.symphony.dal.device.core510i.dto.UpdateLocalExtStat;
import com.avispl.symphony.dal.device.core510i.dto.rpc.Rpc;
import com.avispl.symphony.dal.device.core510i.dto.rpc.request.ControlParam;
import com.avispl.symphony.dal.device.core510i.dto.rpc.request.ControlProperty;
import com.avispl.symphony.dal.device.core510i.dto.rpc.request.ControlRequest;
import com.avispl.symphony.dal.device.core510i.dto.rpc.request.GetComponentsRequest;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.Component;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.ComponentControl;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.ComponentProperty;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.GetComponentsResponse;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.GetControlResponse;
import com.avispl.symphony.dal.device.core510i.dto.rpc.response.SetControlResponse;
import com.avispl.symphony.dal.util.StringUtils;

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

	private String gain;
	private QRCCommunicator qrcCommunicator;
	private Map<String, String> failedMonitor;
	private LoginInfo loginInfo;
	private ObjectMapper objectMapper;
	private boolean isEmergencyDelivery = false;
	private ExtendedStatistics localExtStat = null;
	private final ReentrantLock reentrantLock = new ReentrantLock();
	private UpdateLocalExtStat updateLocalExtStatDto;

	/**
	 * Retrieves {@code {@link #gain}}
	 *
	 * @return value of {@link #gain}
	 */
	public String getGain() {
		return gain;
	}

	/**
	 * Sets {@code gain}
	 *
	 * @param gain the {@code java.lang.String} field
	 */
	public void setGain(String gain) {
		this.gain = gain;
	}

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
	public void controlProperty(ControllableProperty controllableProperty) throws IllegalAccessException {
		reentrantLock.lock();
		try {
			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("controlProperty property " + property);
				this.logger.debug("controlProperty value " + value);
			}

			String[] splitProperty = property.split(String.valueOf(QSYSCoreConstant.HASH));

			if (splitProperty.length != 2) {
				throw new IllegalArgumentException("Unexpected length of control property");
			}

			// Ex: Gain: Named Component#Gain Value Control
			// metricName = Gain Value Control
			// namedComponent = Named Component
			String metricName = splitProperty[1];
			String namedComponent = splitProperty[0].split(String.valueOf(QSYSCoreConstant.SPACE), 2)[1];

			QSYSCoreControllingMetric controllingMetric = QSYSCoreControllingMetric.getByMetric(QSYSCoreConstant.HASH + metricName);

			ControlRequest request = buildSendControlCommand(namedComponent, QSYSCoreControllingMethod.COMPONENT_SET, Double.valueOf(value), controllingMetric.getProperty());
			SetControlResponse response = (SetControlResponse) getResponsesFromDevice(request, SetControlResponse.class);

			if (response == null || !response.getResult()) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Error: cannot set gain value of component " + namedComponent);
				}
				throw new IllegalAccessException("Cannot set " + controllingMetric.getProperty() + " value of component \"" + namedComponent + "\"");
			}

			// if success
			if (localExtStat != null) {
				updateLocalExtStatDto = new UpdateLocalExtStat(property, value, namedComponent, controllingMetric);
				isEmergencyDelivery = true;
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to control a list of properties
	 *
	 * @param controllableProperties This is the list of properties to be controlled
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws IllegalAccessException {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("QSYSCoreCommunicator: Controllable properties cannot be null or empty");
		}

		for (ControllableProperty controllableProperty : controllableProperties) {
			controlProperty(controllableProperty);
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is recalled by Symphony to get the list of statistics to be displayed
	 *
	 * @return List<Statistics> This return the list of statistics.
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		ExtendedStatistics extendedStatistics = new ExtendedStatistics();
		// This is to make sure if the statistics is being fetched before/after any set of control operations
		reentrantLock.lock();
		try {
			if (qrcCommunicator == null) {
				initQRCCommunicator();
			}

			if (isEmergencyDelivery && localExtStat != null) {
				isEmergencyDelivery = false;
			} else {
				Map<String, String> stats = new HashMap<>();
				List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();

				failedMonitor = new HashMap<>();
				loginInfo = initLoginInfo();

				populateQSYSMonitoringMetrics(stats);
				populateGainControllingMetrics(stats, controllableProperties);

				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(controllableProperties);
				localExtStat = extendedStatistics;
			}

			if (updateLocalExtStatDto != null) {
				updateLocalExtStat(updateLocalExtStatDto);
				updateLocalExtStatDto = null;
			}
		} finally {
			reentrantLock.unlock();
		}

		return Collections.singletonList(localExtStat);
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
	 * Init instance of QRCCommunicator
	 *
	 * @throws Exception if init fail
	 */
	public void initQRCCommunicator() throws Exception {
		qrcCommunicator = new QRCCommunicator();
		qrcCommunicator.setHost(this.host);
		qrcCommunicator.setPort(QSYSCoreConstant.QRC_PORT);
		qrcCommunicator.init();
	}

	/**
	 * This method used to update local extended statistics when control a property or in monitoring cycle
	 *
	 * @param updateLocalExtStatDto is the dto to update local extended statistic
	 */
	private void updateLocalExtStat(UpdateLocalExtStat updateLocalExtStatDto) {
		if (localExtStat.getStatistics() == null || localExtStat.getControllableProperties() == null) {
			return;
		}

		String gainString = updateLocalExtStatDto.getValue();
		float gainValue = Float.parseFloat(updateLocalExtStatDto.getValue());

		// ex: 9.0 -> 9.00, -2.0 -> -2.00 (Only Gain)
		if (updateLocalExtStatDto.getControllingMetric() == QSYSCoreControllingMetric.GAIN_VALUE_CONTROL) {
			if (Math.abs(gainValue / 10) < 1) {
				gainString = gainString + QSYSCoreConstant.ZERO;
			}
			gainString += QSYSCoreConstant.GAIN_UNIT;
			localExtStat.getStatistics().put(QSYSCoreConstant.GAIN_LABEL + updateLocalExtStatDto.getNamedComponent() + QSYSCoreControllingMetric.CURRENT_GAIN_VALUE.getMetric(), gainString);
		}

		// Gain or Mute
		localExtStat.getStatistics().put(updateLocalExtStatDto.getProperty(), "");
		AdvancedControllableProperty advancedControllableProperty = localExtStat.getControllableProperties().stream()
				.filter(item -> Objects.equals(item.getName(), updateLocalExtStatDto.getProperty()))
				.findAny()
				.orElse(null);

		int index = localExtStat.getControllableProperties().indexOf(advancedControllableProperty);

		assert advancedControllableProperty != null;
		advancedControllableProperty.setValue(gainValue);
		localExtStat.getControllableProperties().set(index, advancedControllableProperty);
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
						throw new ResourceNotReachableException(QSYSCoreConstant.GETTING_TOKEN_ERR);
					}
				}else {
					throw new ResourceNotReachableException(QSYSCoreConstant.GETTING_TOKEN_ERR);
				}
			}
		} catch (Exception e) {
			this.loginInfo.setToken(null);
			throw new ResourceNotReachableException (QSYSCoreConstant.GETTING_TOKEN_ERR);
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
					objectMapper = new ObjectMapper();
					DeviceInfo deviceInfo = objectMapper.readValue(response.toString(), DeviceInfo.class);
					DeviceInfoData deviceInfoData = deviceInfo.getDeviceInfoData();

					stats.put(QSYSCoreMonitoringMetric.DEVICE_ID.getName(), checkForNullData(deviceInfoData.getDeviceId()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_NAME.getName(), checkForNullData(deviceInfoData.getDeviceName()));
					stats.put(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName(), checkForNullData(deviceInfoData.getDeviceModel()));
					stats.put(QSYSCoreMonitoringMetric.FIRMWARE_VERSION.getName(), checkForNullData(deviceInfoData.getFirmwareVersion().getBuildName()));
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
					objectMapper = new ObjectMapper();
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
	 * @throws ResourceNotReachableException when failedMonitor said all device monitoring data are failed to get
	 */
	private void populateQSYSMonitoringMetrics(Map<String, String> stats) {
		Objects.requireNonNull(stats);
		if (!StringUtils.isNullOrEmpty(getPassword()) && !StringUtils.isNullOrEmpty(getLogin())) {
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

	/**
	 * This method is used for populate Q-SYS gain controlling metrics
	 *
	 * @param stats is the map that store all statistics
	 * @param controllableProperties is the list that store all controllable properties
	 */
	private void populateGainControllingMetrics(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties) {
		// If user doesn't input anything, just break out the method
		if (gain == null) {
			return;
		}

		String[] namedGainComponents = handleGainInputFromUser();
		GetComponentsResponse namedComponents = (GetComponentsResponse) getResponsesFromDevice(new GetComponentsRequest(), GetComponentsResponse.class);

		// If there is no component in the design file -> just return and do nothing
		if (namedComponents == null) {
			return;
		}

		for (String namedGainComponent : namedGainComponents) {
			GainControlInfo controlInfo;
			String errorMessage = null;

			try {
				controlInfo = getControlInfoFromComponent(namedComponents, namedGainComponent);

				if (controlInfo == null) {
					errorMessage = QSYSCoreConstant.GETTING_DEVICE_INFO_ERR;

					if (this.logger.isDebugEnabled()) {
						this.logger.debug(errorMessage);
					}

					populateUnavailableGainControllingGroup(stats, namedGainComponent, errorMessage);
				} else {
					double minGain = Double.parseDouble(controlInfo.getMinGain());
					double maxGain = Double.parseDouble(controlInfo.getMaxGain());

					// swap 2 value then set back to control info
					if (minGain > maxGain) {
						maxGain = maxGain - minGain;
						minGain = maxGain + minGain;
						maxGain = minGain - maxGain;
					}

					controlInfo.setMinGain(String.valueOf(minGain));
					controlInfo.setMaxGain(String.valueOf(maxGain));
					controlInfo.setName(namedGainComponent);

					populateAvailableGainControllingGroup(stats, controllableProperties, controlInfo, minGain != maxGain);
				}
			} catch (Exception e) {
				if (e instanceof NumberFormatException) {
					errorMessage = QSYSCoreConstant.GETTING_MIN_MAX_ERR;
				} else if (e instanceof NameNotFoundException) {
					errorMessage = "Component \"" + namedGainComponent + "\" does not exist";
				}

				if (this.logger.isDebugEnabled()) {
					this.logger.debug(errorMessage);
				}

				populateUnavailableGainControllingGroup(stats, namedGainComponent, errorMessage);
			}
		}
	}

	/**
	 * This method is used to handle gain input from adapter properties and convert it to String array of named gain components for control
	 *
	 * @return String[] is the String array of named gain components return from Gain input
	 */
	public String[] handleGainInputFromUser() {
		String[] namedGainComponents = gain.split(String.valueOf(QSYSCoreConstant.COMMA));
		StringBuilder errorMessages = new StringBuilder();

		// Remove start and end spaces of each gain
		for (int i = 0; i < namedGainComponents.length; ++i) {
			namedGainComponents[i] = namedGainComponents[i].trim();

			if (namedGainComponents[i].matches(QSYSCoreConstant.SPECIAL_CHARS_PATTERN)) {
				errorMessages.append("Component ").append(namedGainComponents[i]).append(" contains 1 of these special characters: ~ ! @ # $ % ^ & \\ ' or contains <? ");
			}
		}

		// Has error message
		if (errorMessages.length() > 0) {
			throw new IllegalArgumentException(errorMessages.toString());
		}

		// Remove duplicate
		namedGainComponents = Arrays.stream(namedGainComponents).distinct().toArray(String[]::new);

		return namedGainComponents;
	}

	/**
	 * This method is used to get gain control information from all named components
	 *
	 * @param namedComponents is all named components in the system get from device
	 * @param namedGainComponent is gain component want to get from named components
	 * @return GainControlInfo is the gain control info get from namedGainComponent
	 * @throws NameNotFoundException when namedGainComponent not found in namedComponents
	 */
	private GainControlInfo getControlInfoFromComponent(GetComponentsResponse namedComponents, String namedGainComponent) throws NameNotFoundException {
		GainControlInfo controlInfo = null;

		for (Component namedComponent : namedComponents.getComponents()) {

			if (Objects.equals(namedComponent.getType(), QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getProperty()) &&
					Objects.equals(namedComponent.getName(), namedGainComponent)) {
				controlInfo = new GainControlInfo();

				// Get current gain value of gain namedComponent
				if (!getCurrentGainControlValue(controlInfo, namedGainComponent, QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getProperty())) {
					return null;
				}

				// Get current mute value of gain namedComponent
				if (!getCurrentGainControlValue(controlInfo, namedGainComponent, QSYSCoreControllingMetric.MUTE_CONTROL.getProperty())) {
					return null;
				}

				// Get min and max gain from namedComponent
				getMinMaxGainControlValue(controlInfo, namedComponent);

				// Min mute and max mute is static
				// Min mute: 0
				// Max mute: 1
			}
		}

		// If control info equal to null, it means that not found gain component
		if (controlInfo == null) {
			throw new NameNotFoundException();
		}

		return controlInfo;
	}

	/**
	 * This method is used to get current gain control value
	 *
	 * @param controlInfo is control information DTO for mapping
	 * @param namedGainComponent is gain component want to get control info
	 * @param controlProperty is control property want to control
	 * @return true if we can get successfully and vice versa
	 */
	private boolean getCurrentGainControlValue(GainControlInfo controlInfo, String namedGainComponent, String controlProperty) {
		ControlRequest request = buildSendControlCommand(namedGainComponent, QSYSCoreControllingMethod.COMPONENT_GET, null, controlProperty);
		GetControlResponse getControlResponse = (GetControlResponse) getResponsesFromDevice(request, GetControlResponse.class);

		// If cannot get response -> return false
		if (getControlResponse == null) {
			return false;
		}

		Component controlComponent = getControlResponse.getComponent();

		for (ComponentControl control : controlComponent.getControls()) {
			if (Objects.equals(control.getName(), QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getProperty())) {
				// Get string value of gain, ex: "10dB"
				controlInfo.setGainString(control.getString());
				controlInfo.setGainValue(Double.parseDouble(control.getString().replace(QSYSCoreConstant.GAIN_UNIT, "")));
			} else if (Objects.equals(control.getName(), QSYSCoreControllingMetric.MUTE_CONTROL.getProperty())) {
				// Get mute position in slider (0 and 1)
				controlInfo.setMutePosition(control.getPosition());
			}
		}

		return true;
	}

	/**
	 * This method is used to get min gain and max gain value from namedComponent
	 *
	 * @param controlInfo used to store gain control information
	 */
	private void getMinMaxGainControlValue(GainControlInfo controlInfo, Component namedComponent) {
		for (ComponentProperty componentProperty : namedComponent.getProperties()) {
			if (Objects.equals(componentProperty.getName(), QSYSCoreConstant.MIN_GAIN)) {
				controlInfo.setMinGain(componentProperty.getValue());
			} else if (Objects.equals(componentProperty.getName(), QSYSCoreConstant.MAX_GAIN)) {
				controlInfo.setMaxGain(componentProperty.getValue());
			}
		}
	}

	/**
	 * This method is used to populate available gain controlling group:
	 * <li>Current gain value</li>
	 * <li>Gain value control</li>
	 * <li>Mute control</li>
	 *
	 * @param stats is the map that store all statistics
	 * @param controllableProperties is the list that store all controllable properties
	 * @param controlInfo is used to get control information
	 * @param canControlGain is used to check if we can control gain or not
	 */
	private void populateAvailableGainControllingGroup(Map<String, String> stats, List<AdvancedControllableProperty> controllableProperties, GainControlInfo controlInfo, boolean canControlGain) {
		stats.put(QSYSCoreConstant.GAIN_LABEL + controlInfo.getName() + QSYSCoreControllingMetric.CURRENT_GAIN_VALUE.getMetric(), controlInfo.getGainString());

		if (canControlGain) {
			stats.put(QSYSCoreConstant.GAIN_LABEL + controlInfo.getName() + QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getMetric(), "");
			controllableProperties.add(createSlider(QSYSCoreConstant.GAIN_LABEL + controlInfo.getName() + QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getMetric(),
					controlInfo.getMinGain(), controlInfo.getMaxGain(), Float.parseFloat(controlInfo.getMinGain()), Float.parseFloat(controlInfo.getMaxGain()),
					(float) controlInfo.getGainValue()));
		}

		stats.put(QSYSCoreConstant.GAIN_LABEL + controlInfo.getName() + QSYSCoreControllingMetric.MUTE_CONTROL.getMetric(), "");
		controllableProperties.add(
				createSwitch(QSYSCoreConstant.GAIN_LABEL + controlInfo.getName() + QSYSCoreControllingMetric.MUTE_CONTROL.getMetric(), (int) controlInfo.getMutePosition()));
	}

	/**
	 * This method used to populate unavailable gain controlling group
	 *
	 * @param stats is the map that store all statistics
	 * @param namedGainComponent is an unavailable gain component
	 * @param errorMessage is an error message want to populate
	 */
	private void populateUnavailableGainControllingGroup(Map<String, String> stats, String namedGainComponent, String errorMessage) {
		stats.put(QSYSCoreConstant.GAIN_LABEL + namedGainComponent + QSYSCoreControllingMetric.ERROR_MESSAGE.getMetric(), errorMessage);
	}

	/**
	 * This method used to build a control command to send to device
	 *
	 * @param namedComponent is named component want to send
	 * @param method is the control method (get and set)
	 * @param value is the value want to set if control method is set
	 * @param property is the property want to control (gain, mute,...)
	 * @return ControlRequest is the request to send to device
	 */
	public ControlRequest buildSendControlCommand(String namedComponent, QSYSCoreControllingMethod method, Double value, String property) {
		ControlProperty controlProperty = new ControlProperty(property);

		if (value != null) {
			controlProperty.setValue(value);
		}

		ControlParam params = new ControlParam(namedComponent);
		params.add(controlProperty);
		return new ControlRequest(method.getName(), params);
	}

	/**
	 * This method is used to get responses from device
	 *
	 * @param request is the RPC request to be sent
	 * @param responseClass is the response class for mapping
	 * @return Rpc is the json rpc of response
	 */
	public Rpc getResponsesFromDevice(Rpc request, Class<? extends Rpc> responseClass) {
		try {
			objectMapper = new ObjectMapper();
			String requestString = objectMapper.writeValueAsString(request) + QSYSCoreConstant.NULL_TERMINATED;

			String[] responses = qrcCommunicator.send(requestString);
			verifyResponse(responses);

			return objectMapper.readValue(responses[1], responseClass);
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error("error during get response from device", e);
			}
		}
		return null;
	}

	/**
	 * This method used to verify responses from device
	 *
	 * @param responses is String array contain all responses to verify
	 */
	private void verifyResponse(String[] responses) {
		if (responses.length != qrcCommunicator.getNumOfResponses()) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error("error: Unexpected length of responses: " + this.host + " port: " + QSYSCoreConstant.QRC_PORT);
			}
			throw new IllegalStateException("Unexpected length of responses");
		}
	}

	/**
	 * Create a switch controllable property
	 *
	 * @param name name of the switch
	 * @param status initial switch state (0|1)
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(QSYSCoreConstant.OFF);
		toggle.setLabelOn(QSYSCoreConstant.ON);

		return new AdvancedControllableProperty(name, new Date(), toggle, status);
	}

	/***
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
		slider.setLabelStart(labelStart);
		slider.setLabelEnd(labelEnd);
		slider.setRangeStart(rangeStart);
		slider.setRangeEnd(rangeEnd);

		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
	}
}
