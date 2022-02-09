/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.device.core510i.dto;

import com.avispl.symphony.dal.device.core510i.common.QSYSCoreConstant;

/**
 * Login information
 *
 * @author Harry
 * @since 1.0
 */
public class LoginInfo {

	private long loginDateTime = 0;
	private String token;

	/**
	 * Retrieves {@code {@link #loginDateTime}}
	 *
	 * @return value of {@link #loginDateTime}
	 */
	public long getLoginDateTime() {
		return loginDateTime;
	}

	/**
	 * Sets {@code loginDateTime}
	 *
	 * @param loginDateTime the {@code long} field
	 */
	public void setLoginDateTime(long loginDateTime) {
		this.loginDateTime = loginDateTime;
	}

	/**
	 * Retrieves {@code {@link #token}}
	 *
	 * @return value of {@link #token}
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets {@code token}
	 *
	 * @param token the {@code java.lang.String} field
	 */
	public void setToken(String token) {
		this.token = token;
	}


	/**
	 * Check token expiry time
	 * Token is timeout when elapsed > 55 min
	 *
	 * @return boolean
	 */
	public boolean isTimeout() {
		long elapsed = (System.currentTimeMillis() - loginDateTime) / 60000;
		return elapsed > QSYSCoreConstant.TIMEOUT;
	}

}
