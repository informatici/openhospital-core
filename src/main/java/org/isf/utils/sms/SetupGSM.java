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
package org.isf.utils.sms;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.isf.generaldata.ConfigurationProperties;
import org.isf.sms.providers.gsm.GSMGatewayService;
import org.isf.sms.providers.gsm.GSMParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.PortInUseException;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * @author Mwithi
 */
public class SetupGSM extends JFrame implements SerialPortDataListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SetupGSM.class);

	private Properties props;
	private InputStream inputStream;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SetupGSM();
		System.exit(0);
	}

	public SetupGSM() {
		props = ConfigurationProperties.loadPropertiesFile(GSMParameters.FILE_PROPERTIES, LOGGER);

		String model = props.getProperty(GSMGatewayService.SERVICE_NAME + ".gmm");

		SerialPort[] portList = SerialPort.getCommPorts();
		LOGGER.info("Found {} ports: {}", portList.length, portList.toString());

		for (SerialPort comPort : portList) {

			// LOGGER.info("Port {}", comPort.getDescriptivePortName());

			try {
				LOGGER.info("Opening port...");
				comPort.openPort();
				comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				comPort.addDataListener(this);

				OutputStream outputStream = comPort.getOutputStream();
				if (outputStream != null) {
					LOGGER.info("Output stream OK");
				} else {
					LOGGER.info("Output stream not found");
				}

				inputStream = comPort.getInputStream();
				byte[] command = model.getBytes();
				outputStream.write(command);

				Thread.sleep(5000);

			} catch (PortInUseException e) {
				LOGGER.error("Port in use.");
			} catch (RuntimeException re) {
				LOGGER.error("Something strange happened.", re);
			} catch (Exception exception) {
				LOGGER.error("Failed to open port '{}'", comPort);
				LOGGER.error(exception.getMessage(), exception);
			} finally {
				LOGGER.info("Closing port...");
				comPort.closePort();
			}
		}
		LOGGER.info("End.");
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		SerialPort serialPort = (SerialPort) event.getSource();
		String port = serialPort.getSystemPortName();
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[1];
		try {
			while (inputStream.available() > 0) {
				sb.append(new String(buffer));
			}
			String answer = sb.toString();
			if (confirm(port, answer) == JOptionPane.YES_OPTION) {
				save(port);
				System.exit(0);
			}
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
		}
	}

	/**
	 * @param port
	 * @param answer
	 * @return
	 * @throws HeadlessException
	 */
	private int confirm(String port, String answer) throws HeadlessException {
		try {
			int ok = answer.indexOf("OK");
			if (ok > 0) {
				answer = answer.substring(2, ok - 3);
			} else {
				return JOptionPane.NO_OPTION;
			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			LOGGER.error("outofbound: '{}'", answer);
		}
		LOGGER.info(answer.trim());

		return JOptionPane.showConfirmDialog(this, "Found modem: " + answer + " on port " + port + "\nConfirm?");
	}

	/**
	 * @param port
	 */
	private void save(String port) {
		try {
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
					.configure(params.properties()
						.setFileName(GSMParameters.FILE_PROPERTIES)
						.setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
			Configuration config = builder.getConfiguration();
			config.setProperty(GSMGatewayService.SERVICE_NAME + ".port", port);
			builder.save();
			LOGGER.error("Port saved in {}", GSMParameters.FILE_PROPERTIES);
		} catch (ConfigurationException ce) {
			LOGGER.error(ce.getMessage(), ce);
		}
	}

	@Override
	public int getListeningEvents() {
		return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
	}

}
