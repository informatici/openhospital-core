/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.sms.service;

/**
 * SmsSenderGSM.java - 03/feb/2014
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.GSMParameters;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * @author Mwithi
 *
 */
public class SmsSenderGSM implements SmsSenderInterface, SerialPortEventListener {

	private static Logger logger = LoggerFactory.getLogger(SmsSenderGSM.class);
	
	private final String EOF = "\032\r";
	
	private Enumeration<?> portList;
	private CommPortIdentifier portId;
	private String port;
	private SerialPort serialPort;
	private boolean connected;
	private OutputStream outputStream;
	private InputStream inputStream;
	
	/**
	 * 
	 */
	public SmsSenderGSM() {
		logger.info("SMS Sender GSM started...");
		GSMParameters.getGSMParameters();
	}
	
	/**
	 * Method that close the serial port 
	 */
	public void terminate() {
		serialPort.close();
	}
	
	/**
	 * Method that look for the port specified 
	 * @return <code>true</code> if the COM port is ready to be used, <code>false</code> otherwise.
	 */
	public boolean initialize() {
		logger.debug("Initialize...");
		connected = false;
		portList = CommPortIdentifier.getPortIdentifiers();
		port = GSMParameters.PORT;
		while (portList.hasMoreElements()) {
			
			portId = (CommPortIdentifier) portList.nextElement();
			
			if (portId.getName().equals(port) && portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				logger.debug("COM PORT found ({})", port);
				break;
				
			} else portId = null; 
		}
		
		if (portId != null) {
			try {
				serialPort = (SerialPort) portId.open("SmsSender", 1000);
				if (serialPort != null) {
					
					outputStream = serialPort.getOutputStream();
					if (outputStream != null) {
						inputStream = serialPort.getInputStream(); 
						
						logger.debug("Output stream OK");
						connected = true;
						
					} else logger.debug("A problem occured on output stream");
					
				} else logger.debug("Not possible to open the stream");
				
			} catch (PortInUseException e) {
				logger.error("Port in use: {}", portId.getCurrentOwner());
			} catch (Exception e) {
				logger.error("Failed to open port {} {}", portId.getName(), e);
			}
		} else {
			logger.error("COM PORT not found ({})!!!", port);
 		}
		return connected;
	}

	@Override
	public boolean sendSMS(Sms sms, boolean debug) {
		if (connected) {
			logger.debug("Sending SMS ({}) to: {}", sms.getSmsId(), sms.getSmsNumber());
			logger.debug("Sending text: {}", sms.getSmsText());
			
			StringBuilder build_CMGS = new StringBuilder(GSMParameters.CMGS);
			build_CMGS.append(sms.getSmsNumber());
			build_CMGS.append("\"\r");
			
			String text = sms.getSmsText() + EOF;

			try {

				//SET SMS MODE
				logger.trace(GSMParameters.CMGF);
				if (!debug) outputStream.write(GSMParameters.CMGF.getBytes());
				Thread.sleep(1000);
				
				//SET SMS PARAMETERS
//				logger.trace(SmsParameters.CSMP);
//				outputStream.write(SmsParameters.CSMP.getBytes());
//				Thread.sleep(1000);

				//SET SMS NUMBER
				logger.trace(build_CMGS.toString());
				if (!debug) outputStream.write(build_CMGS.toString().getBytes());
				Thread.sleep(1000);

				//SET SMS TEXT
				logger.trace(text);
				if (!debug) outputStream.write(text.getBytes());
				Thread.sleep(1000);

			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return true;			
		} else {
			logger.error("Device not connected. Please initialize stream first.");
		}
		return false;
	}

	@Override
    public void serialEvent(SerialPortEvent event) {
		StringBuffer sb = new StringBuffer();
        byte[] buffer = new byte[1];
        try {
			while(inputStream.available() > 0){
			  inputStream.read(buffer);
			  sb.append(new String(buffer));
			}
			String answer = sb.toString();
			logger.debug(answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(new File("./rsc/log4j.properties").getAbsolutePath());
		
		//Get SMS
		SmsOperations smsOp = new SmsOperations();
		List<Sms> smsList = null;
		try {
			smsList = smsOp.getList();
		} catch (OHServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("Found {} SMS to send", smsList.size());
		
		//Send
		SmsSenderGSM sender = new SmsSenderGSM();
		boolean result = false;
		if (sender.initialize()) {
			result = sender.sendSMS(smsList.get(0), true);
		}
		logger.debug("{}", result);
	}
}
