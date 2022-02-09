/*
 * Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.device.core510i;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.avispl.symphony.api.dal.dto.control.ConnectionState;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.dal.BaseDevice;
import com.avispl.symphony.dal.communicator.Communicator;
import com.avispl.symphony.dal.communicator.ConnectionStatus;

/**
 * An implementation of QRCCommunicator to provide communication and interaction with QSys-Core
 *
 * @author Harry
 * @since 1.0
 */
public class QRCCommunicator extends BaseDevice implements Communicator {

	private static final String ERROR_MESSAGE_CHANGE_PROPERTIES_AFTER_INIT = "Cannot change properties after init() was called";
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final ConnectionStatus status = new ConnectionStatus();
	protected String login;
	protected String password;
	protected int numOfResponses = 2;
	private int socketTimeout = 4000;
	private Socket socket;
	private int port = 1710;

	/**
	 * This method returns the device UPD port
	 *
	 * @return int This returns the current UDP port.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * This method is used set the device UDP port
	 *
	 * @param port This is the UDP port to set
	 */
	public void setPort(int port) {
		if (this.isInitialized()) {
			throw new IllegalStateException(ERROR_MESSAGE_CHANGE_PROPERTIES_AFTER_INIT);
		} else {
			this.port = port;
		}
	}

	/**
	 * This method returns the device TCP timeout
	 *
	 * @return int This returns the current TCP timeout.
	 */
	public int getSocketTimeout() {
		return this.socketTimeout;
	}

	/**
	 * This method is used set the device TCP timeout
	 *
	 * @param timeout This is the TCP timeout to set
	 */
	public void setSocketTimeout(int timeout) {
		if (this.isInitialized()) {
			throw new IllegalStateException(ERROR_MESSAGE_CHANGE_PROPERTIES_AFTER_INIT);
		} else {
			this.socketTimeout = timeout;
		}
	}

	/**
	 * This method returns the login info
	 *
	 * @return String This return the current login info
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * This method is used set the login info
	 *
	 * @param login This is current login info to set
	 */
	public void setLogin(String login) {
		if (this.isInitialized()) {
			throw new IllegalStateException(ERROR_MESSAGE_CHANGE_PROPERTIES_AFTER_INIT);
		} else {
			this.login = login;
		}
	}

	/**
	 * This method returns the password info
	 *
	 * @return String This return the current password info
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * This method is used set the password info
	 *
	 * @param password This is current password info to set
	 */
	public void setPassword(String password) {
		if (this.isInitialized()) {
			throw new IllegalStateException(ERROR_MESSAGE_CHANGE_PROPERTIES_AFTER_INIT);
		} else {
			this.password = password;
		}
	}

	/**
	 * This method returns number of responses
	 *
	 * @return String This return the number of responses
	 */
	public int getNumOfResponses() {
		return numOfResponses;
	}

	/**
	 * This method is used set the number of responses
	 *
	 * @param numOfResponses This is current number of responses
	 */
	public void setNumOfResponses(int numOfResponses) {
		this.numOfResponses = numOfResponses;
	}

	/**
	 * {@inheritDoc}
	 * This method is used to get current connection status from the device
	 */
	@Override
	public ConnectionStatus getConnectionStatus() {
		Lock readLock = this.lock.readLock();
		readLock.lock();

		ConnectionStatus currentStatus;
		try {
			currentStatus = this.status.copyOf();
		} finally {
			readLock.unlock();
		}

		return currentStatus;
	}

	/**
	 * This method is used to check if a channel is connected or not
	 */
	private boolean isChannelConnected() {
		Socket client = this.socket;
		return null != client && client.isConnected();
	}

	/**
	 * {@inheritDoc}
	 * This method is used to create a connection actually create a TCP socket channel
	 */
	@Override
	public void connect() throws Exception {
		if (!this.isInitialized()) {
			throw new IllegalStateException("QRCCommunicator cannot be used before init() is called");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Connecting to: ".concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
		}

		Lock writeLock = this.lock.writeLock();
		writeLock.lock();

		try {
			if (!this.isChannelConnected()) {
				this.createChannel();
				this.status.setLastTimestamp(System.currentTimeMillis());
				this.status.setConnectionState(ConnectionState.Connected);
				this.status.setLastError(null);
			}
		} catch (Exception exception) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error connecting to: ".concat(this.host).concat(" port: ").concat(String.valueOf(this.port)), exception);
			}

			this.status.setLastError(exception);
			this.status.setConnectionState(ConnectionState.Failed);
			this.destroyChannel();
			throw exception;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 * This method is used to disconnect from the device actually destroy the TCP socket channel
	 */
	public void disconnect() {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Disconnecting from: ".concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
		}

		Lock writeLock = this.lock.writeLock();
		writeLock.lock();

		try {
			this.destroyChannel();
			this.status.setConnectionState(ConnectionState.Disconnected);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * This method is used to create a channel actually create a socket
	 */
	private void createChannel() throws IOException {
		if (this.socket == null || this.socket.isClosed() || !this.socket.isConnected()) {
			this.socket = new Socket(this.host, this.port);
			this.socket.setTcpNoDelay(true);
			this.socket.setKeepAlive(true);
			this.socket.setOOBInline(true);
			this.socket.setSoTimeout(socketTimeout);
		}
	}

	/**
	 * This method is used to destroy a channel actually destroy a socket
	 */
	public void destroyChannel() {
		if (null != this.socket) {
			try {
				if (this.socket.isConnected()) {
					this.socket.close();
				}
			} catch (Exception var2) {
				if (this.logger.isWarnEnabled()) {
					this.logger.warn("error seen on destroyChannel", var2);
				}
			}

			this.socket = null;
		}
	}

	/**
	 * This method is used to send a JSON RPC to a device
	 *
	 * @param data This is the data to be sent
	 * @return String[] This returns the reply received from the device.
	 */
	protected String[] send(String data) throws Exception {
		if (!this.isInitialized()) {
			throw new IllegalStateException("QRCCommunicator cannot be used before init() is called");
		}

		if (null == data) {
			throw new IllegalArgumentException("Send data is null");
		}

		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Sending command: ".concat(data).concat(" to: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
		}

		Lock writeLock = this.lock.writeLock();
		writeLock.lock();

		String[] response;
		try {
			response = this.send(data, true);
		} finally {
			this.destroyChannel();
			writeLock.unlock();
		}

		return response;
	}

	/**
	 * This method is used to send a JSON RPC to a device
	 *
	 * @param data This is the data to be sent
	 * @param retryOnError This is the flag to retry sending data to device when error
	 * @return String[] This returns the reply received from the device.
	 */
	private String[] send(String data, boolean retryOnError) throws Exception {
		try {
			if (!this.isChannelConnected()) {
				this.createChannel();
				this.status.setLastTimestamp(System.currentTimeMillis());
				this.status.setConnectionState(ConnectionState.Connected);
				this.status.setLastError(null);
			}

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Sending: ".concat(data).concat(" to: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
			}

			String[] response = this.internalSend(data);

			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Received response: ".concat(Arrays.toString(response)).concat(" from: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
			}

			if (this.logger.isTraceEnabled()) {
				this.logger.trace("Received response: ".concat(Arrays.toString(response)).concat(" from: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
			}

			this.status.setLastTimestamp(System.currentTimeMillis());
			return response;
		} catch (CommandFailureException ex1) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error(
						"Command failed ".concat(data).concat(" to: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)).concat(" connection state: ").concat(
								String.valueOf(this.status.getConnectionState())),
						ex1);
			}

			this.status.setLastTimestamp(System.currentTimeMillis());
			throw ex1;
		} catch (SocketTimeoutException ex2) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug(
						"Time out while sending command: ".concat(data).concat(" to: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)).concat(" connection state: ").concat(
								String.valueOf(this.status.getConnectionState())).concat(" error: "), ex2);
			}

			this.status.setLastError(ex2);
			this.status.setConnectionState(ConnectionState.Unknown);
			this.destroyChannel();

			if (retryOnError) {
				return this.send(data, false);
			} else {
				throw ex2;
			}
		} catch (Exception ex3) {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Error sending command: ".concat(data).concat(" to: ").concat(this.host).concat(" port: ").concat(String.valueOf(this.port)).concat(" connection state: ").concat(
						String.valueOf(this.status.getConnectionState())).concat(" error: "), ex3);
			}

			this.status.setLastError(ex3);
			this.status.setConnectionState(ConnectionState.Failed);
			this.destroyChannel();

			if (retryOnError) {
				return this.send(data, false);
			} else {
				throw ex3;
			}
		}
	}

	/**
	 * This method used to send and receive response from device
	 *
	 * @param outputData is the data to be sent
	 * @return String array is the response from device
	 * @throws IOException if read or write fail
	 */
	private String[] internalSend(String outputData) throws IOException {
		this.write(outputData);
		return this.read(outputData, this.socket.getInputStream());
	}

	/**
	 * This method used to send data to the device
	 *
	 * @param outputData is the data to be sent
	 * @throws IOException if write to stream fail
	 */
	private void write(String outputData) throws IOException {
		if (this.socket == null) {
			throw new IllegalStateException("Socket connection was not established. Please check target host availability and credentials.");
		} else {
			OutputStream os = this.socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(outputData);
			osw.flush();
		}
	}

	/**
	 * This method used to read response return from device
	 *
	 * @param command is the command sent to device
	 * @param in is the input stream to read response
	 * @return String array is the response from device
	 * @throws IOException if read fail
	 */
	private String[] read(String command, InputStream in) throws IOException {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("DEBUG - Socket Communicator reading after command text \"".concat(command).concat("\" was sent to host ").concat(this.host));
		}

		// Count number of responses
		int countResponses = 0;
		InputStreamReader inputStreamReader = new InputStreamReader(in);
		StringBuilder stringBuilder = new StringBuilder();

		do {
			int x = inputStreamReader.read();
			stringBuilder.append((char) x);

			// Char '\00' has int value is 0
			// It is the symbol represents for the end of one response
			if (x == 0) {
				countResponses++;
			}
		} while (countResponses != this.numOfResponses);

		String response = stringBuilder.toString();
		return response.split("\00");
	}

	/**
	 * {@inheritDoc}
	 * This method is used to destroy base device
	 */
	@Override
	protected void internalDestroy() {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("Destroying communication channel to: ".concat(this.host).concat(" port: ").concat(String.valueOf(this.port)));
		}

		this.destroyChannel();
		this.status.setConnectionState(ConnectionState.Disconnected);
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 * This method is used to init the base device
	 */
	@Override
	protected void internalInit() throws Exception {
		super.internalInit();
		if (null != this.socket) {
			this.destroyChannel();
		}

		if (this.port <= 0) {
			throw new IllegalStateException("Invalid port property: ".concat(String.valueOf(this.port)).concat(" (must be positive number)"));
		}
	}
}

