/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.device.core510i;

import static com.avispl.symphony.dal.device.core510i.enums.ResponseString.ENGINE_STATUS;
import static com.avispl.symphony.dal.device.core510i.enums.ResponseString.STATUS_RESPONSE;
import static com.avispl.symphony.dal.device.core510i.enums.SendString.STATUS_GET;
import static com.avispl.symphony.dal.device.core510i.enums.SendString.WRONG_FORMAT;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.SocketTimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit test for QSys Core Communicator
 *
 * Send and retrieve data success
 *
 * @author Harry
 * @since 1.0
 */
class QRCCommunicatorTest {
	QRCCommunicator qrcCommunicator = new QRCCommunicator();

	@BeforeEach()
	public void setUp() throws Exception {
		qrcCommunicator.setHost("***REMOVED***");
		qrcCommunicator.setPort(1710);
		qrcCommunicator.init();
		qrcCommunicator.connect();
	}

	@AfterEach()
	public void destroy() {
		qrcCommunicator.disconnect();
	}

	/**
	 * Test QRCCommunicator#send success
	 * Expect send JsonRPC command and receive successfully from TCP server
	 */
	@Tag("RealDevice")
	@Test()
	void testSendStatusGet() throws Exception {
		String request = STATUS_GET.getJsonRpc();
		String[] response = qrcCommunicator.send(request);

		Assertions.assertEquals(response[0], ENGINE_STATUS.getJsonRpc());
		Assertions.assertEquals(response[1], STATUS_RESPONSE.getJsonRpc());
	}

	/**
	 * Test QRCCommunicator#send fail
	 * Expect throw exception when request is null
	 */
	@Tag("RealDevice")
	@Test()
	void testSendNullData() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> qrcCommunicator.send(null));

		String expectedMessage = "Send data is null";
		String actualMessage = exception.getMessage();

		Assertions.assertTrue(actualMessage.contains(expectedMessage));
	}

	/**
	 * Test QRCCommunicator#send fail
	 * Expect throw exception when request is wrong format
	 */
	@Tag("RealDevice")
	@Test()
	void testSendWrongFormat() {
		Exception exception = assertThrows(SocketTimeoutException.class, () -> qrcCommunicator.send(WRONG_FORMAT.getJsonRpc()));

		String expectedMessage = "Read timed out";
		String actualMessage = exception.getMessage();

		Assertions.assertTrue(actualMessage.contains(expectedMessage));
	}
}
