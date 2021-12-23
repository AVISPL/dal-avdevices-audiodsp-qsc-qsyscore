/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i;

import static org.mockito.Mockito.doReturn;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;
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
public class QSYSCoreCommunicatorTest {
	private final QSYSCoreCommunicator qSYSCoreCommunicator = new QSYSCoreCommunicator();

	@Before
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
	public void testQSysCoreCommunicatorDeviceHaveData() {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

		Assertions.assertEquals(MonitoringData.DEVICE_ID.getData(), stats.get(QSYSCoreMonitoringMetric.DEVICE_ID.getName()));
		Assertions.assertEquals(MonitoringData.DEVICE_MODEL.getData(), stats.get(QSYSCoreMonitoringMetric.DEVICE_MODEL.getName()));
		Assertions.assertEquals(MonitoringData.SERIAL_NUMBER.getData(), stats.get(QSYSCoreMonitoringMetric.SERIAL_NUMBER.getName()));
	}

	/**
	 * Test QSYSCoreCommunicator.getMultipleStatistics failed
	 * Expected retrieve valid device monitoring data
	 */
	@Tag("RealDevice")
	@Test
	public void testQSysCoreCommunicatorDeviceEmptyResponse() throws Exception {
		thrown.expect(ResourceNotReachableException.class);
		thrown.expectMessage(ExceptionMessage.GETTING_DEVICE_INFO_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE + ExceptionMessage.GETTING_DEVICE_IP_ERR.getMessage() + QSYSCoreConstant.NEXT_LINE);
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setToken("Authorized");
		loginInfo.setLoginDateTime(System.currentTimeMillis());
		QSYSCoreCommunicator qSYSCoreCommunicatorSpy = Mockito.spy(new QSYSCoreCommunicator());
		doReturn(loginInfo).when(qSYSCoreCommunicatorSpy).initLoginInfo();

		ExtendedStatistics extendedStatistics = (ExtendedStatistics) qSYSCoreCommunicatorSpy.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();

	}
}
