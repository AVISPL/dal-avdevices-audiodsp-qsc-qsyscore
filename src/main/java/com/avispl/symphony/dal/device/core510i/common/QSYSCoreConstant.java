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
	public static final char TILDE = '~';
	public static final char COMMA = ',';
	public static final char SPACE = ' ';
	public static final char NEXT_LINE = '\n';
	public static final char NULL_TERMINATED = '\00';
	public static final String HTTP = "http://";
	public static final String NONE = "None";
	public static final String AUTHORIZED = "Authorized";
	public static final String GETTING_DEVICE_INFO_ERR = "failed to get device info";
	public static final String GETTING_DEVICE_IP_ERR = "failed to get device IP Address";
	public static final String GETTING_MIN_MAX_ERR = "Min or max value has wrong number format";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String DATA = "data";
	public static final String TOKEN = "token";
	public static final String TEST = "test";
	public static final String GAIN_LABEL = "Gain: ";
	public static final String MAX_GAIN = "max_gain";
	public static final String MIN_GAIN = "min_gain";
	public static final String OFF = "Off";
	public static final String ON = "On";
	public static final String GAIN_UNIT = "dB";
	public static final String SPECIAL_CHARS_PATTERN = ".*[!@%^&\\'].*";
	public static final String DEFAULT_RPC_VERSION = "2.0";
	public static final int DEFAULT_ID = 1234;
	public static final int QRC_PORT = 1710;

	/**
	 * Token timeout is 1 hour ( 60 minutes), as this case reserve 5 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 55;

}
