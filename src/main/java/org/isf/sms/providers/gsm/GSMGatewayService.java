/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.sms.providers.gsm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.SmsSenderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * @author Mwithi 03/feb/2014
 */
@Component
public class GSMGatewayService implements SmsSenderInterface, SerialPortDataListener {

	public static final String SERVICE_NAME = "gsm-gateway-service";
	private static final Logger LOGGER = LoggerFactory.getLogger(GSMGatewayService.class);
	private static final String EOF = "\r";

	private SerialPort serialPort;
	private boolean connected;
	private OutputStream outputStream;
	private InputStream inputStream;

	private boolean sent = true;

	public GSMGatewayService() {
		LOGGER.info("SMS Sender GSM started...");
		GSMParameters.getGSMParameters();
	}

	/**
	 * Method that closes the serial port
	 */
	@Override
	public boolean terminate() {
		serialPort.closePort();
		return true;
	}

	/**
	 * Method that looks for the port specified
	 * 
	 * @return {@code true} if the COM port is ready to be used, {@code false} otherwise.
	 */
	@Override
	public boolean initialize() {
		LOGGER.debug("Initialize...");
		connected = false;
		SerialPort[] portList = SerialPort.getCommPorts();
		String port = GSMParameters.PORT;
		for (SerialPort comPort : portList) {

			if (comPort.getSystemPortName().equals(port)) {

				LOGGER.debug("COM PORT found ({})", port);
				serialPort = comPort;
				break;

			}
		}

		if (serialPort != null) {
			try {
				serialPort.openPort();
				serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				serialPort.addDataListener(this);

				outputStream = serialPort.getOutputStream();
				if (outputStream != null) {
					inputStream = serialPort.getInputStream();

					LOGGER.debug("Output stream OK");
					connected = true;

				} else {
					LOGGER.debug("A problem occured on output stream");
				}

			} catch (PortInUseException e) {
				LOGGER.error("Port in use.");
			} catch (Exception e) {
				LOGGER.error("Failed to open port {}", serialPort.getSystemPortName(), e);
			}
		} else {
			LOGGER.error("COM PORT not found ({})!!!", port);
		}
		return connected;
	}

	@Override
	public boolean sendSMS(Sms sms) {
		return this.sendSMS(sms, false);
	}

	public boolean sendSMS(Sms sms, boolean debug) {
		if (connected) {
			LOGGER.debug("Sending SMS ({}) to: {}", sms.getSmsId(), sms.getSmsNumber());
			LOGGER.debug("Sending text: {}", sms.getSmsText());

			StringBuilder buildCMGS = new StringBuilder(GSMParameters.CMGS);
			buildCMGS.append(sms.getSmsNumber());
			buildCMGS.append("\"\r");

			String text = sms.getSmsText() + EOF;

			try {

				// SET SMS MODE
				LOGGER.trace(GSMParameters.CMGF);
				if (!debug) {
					outputStream.write(GSMParameters.CMGF.getBytes());
				}
				Thread.sleep(1000);

				// SET SMS PARAMETERS
//				logger.trace(SmsParameters.CSMP);
//				outputStream.write(SmsParameters.CSMP.getBytes());
//				Thread.sleep(1000);

				// SET SMS NUMBER
				LOGGER.trace(buildCMGS.toString());
				if (!debug) {
					outputStream.write(buildCMGS.toString().getBytes());
				}
				Thread.sleep(1000);

				// SET SMS TEXT
				LOGGER.trace(text);
				if (!debug) {
					outputStream.write(text.getBytes());
				}
				Thread.sleep(1000);

				// SEND SMS
				if (!debug) {
					outputStream.write("\u001A".getBytes()); // Ctrl-Z();
				}
				Thread.sleep(1000);

				// FLUSH STREAM
//				if (!debug) outputStream.flush(); // missing callback function on Windows OS
//				Thread.sleep(1000);

				if (!sent) {
					sent = true; // for next message but return false (not sent)
					return false;
				}

			} catch (IOException | InterruptedException exception) {
				LOGGER.error(exception.getMessage(), exception);
				return false;
			}
			return true;
		} else {
			LOGGER.error("Device not connected. Please initialize stream first.");
		}
		return false;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[1];
		try {
			while (inputStream.available() > 0) {
				inputStream.read(buffer);
				sb.append(new String(buffer));
			}
			String answer = sb.toString();
			LOGGER.debug(answer);
			if (answer.contains("ERROR")) {
				LOGGER.error("ERROR: {}", answer);
				sent = false;
			}
		} catch (IOException e) {
			LOGGER.error("Exception in serialEvent method.", e);
		}
	}

	@Override
	public String getName() {
		return SERVICE_NAME;
	}

	@Override
	public String getRootKey() {
		return SERVICE_NAME;
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}
}
