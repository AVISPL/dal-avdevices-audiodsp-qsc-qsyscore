/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.beans.binding.When;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreControllingMetric;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreMonitoringMetric;
import com.avispl.symphony.dal.device.core510i.dto.LoginInfo;
import com.avispl.symphony.dal.device.core510i.enums.ExceptionMessage;
import com.avispl.symphony.dal.device.core510i.enums.MonitoringData;

/**
 * Unit test for QSYSCoreCommunicator
 *
 * Send and retrieve data success
 * Failed retrieve data
 * Existing Extended Statistics
 *
 * @author Harry
 * @version 1.0
 * @since 1.0
 */
class QSYSCoreCommunicatorTest {

	private final QSYSCoreCommunicator qSYSCoreCommunicator = new QSYSCoreCommunicator();
	private final static String availableGain = "PGM01";
	private final static String unavailableGain = "PGM00";

	@BeforeEach()
	public void setUp() throws Exception {
		qSYSCoreCommunicator.setHost("***REMOVED***");
		qSYSCoreCommunicator.setLogin("root");
		qSYSCoreCommunicator.setPassword("12345678");
		qSYSCoreCommunicator.init();
		qSYSCoreCommunicator.connect();
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@AfterEach()
	public void destroy() throws Exception {
		qSYSCoreCommunicator.disconnect();
	}

	/**
	 * Test QSYSCoreCommunicator.getMultipleStatistics successful with valid username password
	 * Expected retrieve valid device monitoring data
	 */
	@Tag("RealDevice")
	@Test
	void testQSysCoreCommunicatorDeviceHaveData() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		Assertions.assertEquals(MonitoringData.DEVICE_ID.getData(), stats.get(QSYSCoreMonitoringMetric.DEVICE_ID.getName()));
		Assertions.assertEquals(MonitoringData.DEVICE_MODEL.getData(), stats.get(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName()));
		Assertions.assertEquals(MonitoringData.SERIAL_NUMBER.getData(), stats.get(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName()));
	}

	/**
	 * Test QSYSCoreCommunicator.getMultipleStatistics will not retrieve token when username and password are empty
	 * Expected retrieve valid device monitoring data
	 */
	@Tag("Mock")
	@Test
	void testQSysCoreCommunicatorDeviceHaveDataWithAccessControlDisable() {

		QSYSCoreCommunicator qsysCoreCommunicatorSpy = Mockito.spy(QSYSCoreCommunicator.class);
		qsysCoreCommunicatorSpy.setHost("***REMOVED***");
		Mockito.when(qsysCoreCommunicatorSpy.getLogin()).thenReturn("");
		Mockito.when(qsysCoreCommunicatorSpy.getPassword()).thenReturn("");
		try {
			qsysCoreCommunicatorSpy.getMultipleStatistics();
		}catch (Exception e){
			Assertions.assertEquals( ExceptionMessage.GETTING_DEVICE_INFO_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE + ExceptionMessage.GETTING_DEVICE_IP_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE,
					e.getMessage());
		}
	}

	/**
	 * Test QSYSCoreCommunicator.getMultipleStatistics failed
	 * Expected retrieve valid device monitoring data
	 */
	@Tag("Mock")
	@Test()
	void testQSysCoreCommunicatorDeviceEmptyResponse() {
		thrown.expect(ResourceNotReachableException.class);
		thrown.expectMessage(ExceptionMessage.GETTING_DEVICE_INFO_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE + ExceptionMessage.GETTING_DEVICE_IP_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE);
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setToken("Authorized");
		loginInfo.setLoginDateTime(System.currentTimeMillis());
		QSYSCoreCommunicator qSYSCoreCommunicatorSpy = Mockito.spy(new QSYSCoreCommunicator());
		doReturn(loginInfo).when(qSYSCoreCommunicatorSpy).initLoginInfo();
	}

	/**
	 * Test QSYSCoreCommunicator.handleGainInputFromUser successful with input gain from user
	 * Expected handle input gain from user success
	 */
	@Tag("Mock")
	@Test
	void testHandleGainInputFromUser() {
		String inputGain = "  test  ,  test  test   , test ,test";
		qSYSCoreCommunicator.setGain(inputGain);

		String[] expectedNamedGainComponents = new String[] { "test", "test  test" };
		String[] actualNamedGainComponents = qSYSCoreCommunicator.handleGainInputFromUser();

		Assertions.assertArrayEquals(expectedNamedGainComponents, actualNamedGainComponents);
	}

	/**
	 * Test QSYSCoreCommunicator.populateAvailableGainControllingGroup success
	 * Expected retrieve current value from slider
	 */
	@Tag("RealDevice")
	@Test()
	void testQSysCoreCommunicatorAvailableGainProperty() throws Exception {
		// Set input gain from user
		qSYSCoreCommunicator.setGain(availableGain);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllableProperties = extendedStatistics.getControllableProperties();
		Map<String, String> stats = extendedStatistics.getStatistics();

		String currentGain = null;
		for (String key : stats.keySet()) {
			if (Objects.equals(key, QSYSCoreConstant.GAIN_LABEL + availableGain + QSYSCoreControllingMetric.CURRENT_GAIN_VALUE.getMetric())) {
				currentGain = stats.get(key);
			}
		}

		for (AdvancedControllableProperty property : advancedControllableProperties) {
			if (property.getName().equalsIgnoreCase(QSYSCoreConstant.GAIN_LABEL + availableGain + QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getMetric())) {
				assert currentGain != null;
				Assertions.assertEquals(Double.parseDouble(currentGain.replace(QSYSCoreConstant.GAIN_UNIT, "")), (float) property.getValue());
				return;
			}
		}
	}

	/**
	 * Test QSYSCoreCommunicator.populateUnavailableGainControllingGroup success
	 * Expected retrieve valid error message
	 */
	@Tag("RealDevice")
	@Test()
	void testQSysCoreCommunicatorNotAvailableGainProperty() throws Exception {
		// Set input gain from user
		qSYSCoreCommunicator.setGain(unavailableGain);
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		String expectedMessage = "Component \"" + unavailableGain + "\" does not exist";
		String actualMessage = stats.get(QSYSCoreConstant.GAIN_LABEL + unavailableGain + QSYSCoreControllingMetric.ERROR_MESSAGE.getMetric());
		Assertions.assertEquals(expectedMessage, actualMessage);
	}

	/**
	 * Test QSYSCoreCommunicator.controlProperty fail
	 * Expected throw exception when cannot control
	 */
	@Tag("Mock")
	@Test
	void testSetGainValueFail() {
		ControllableProperty controllableProperty = new ControllableProperty();
		controllableProperty.setProperty(QSYSCoreConstant.GAIN_LABEL + unavailableGain + QSYSCoreControllingMetric.GAIN_VALUE_CONTROL.getMetric());
		controllableProperty.setValue("1");

		Exception exception = assertThrows(IllegalAccessException.class, () -> qSYSCoreCommunicator.controlProperty(controllableProperty));

		String expectedMessage = "Cannot set gain value of component \"" + unavailableGain + "\"";
		String actualMessage = exception.getMessage();

		Assertions.assertTrue(actualMessage.contains(expectedMessage));
	}
}
