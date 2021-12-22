/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.common;

/**
 * Set of constants
 *
 * @author Harry
 * @since 1.0
 */
public class QSYSCoreConstant {

	public static final char HASH = '#';
	public static final char NEXT_LINE = '\n';
	public static final String HTTP = "http://";
	public static final String NONE = "None";
	public static final String AUTHORIZED = "Authorized";
	public static final String GETTING_DEVICE_INFO_ERR = "failed to get device info";
	public static final String GETTING_DEVICE_IP_ERR = "failed to get device IP Address";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String DATA = "data";
	public static final String TOKEN = "token";

	/**
	 * Token timeout is 1 hour ( 60 minutes), as this case reserve 5 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 55;

}
