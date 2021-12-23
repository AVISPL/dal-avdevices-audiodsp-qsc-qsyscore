/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

/**
 * All URL which will be accessed
 *
 * @author Harry
 * @since 1.0
 */
public class QSYSCoreURL {

	public static final String BASE_URI = "/api/v0/";
	public static final String TOKEN = "logon";
	public static final String DEVICE_INFO = "cores/self?meta=permissions";
	public static final String DEVICE_IP_ADDRESS = "cores/self/config/network?meta=permissions&include=autoDns";

}
