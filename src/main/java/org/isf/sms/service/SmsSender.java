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

import java.util.Date;
import java.util.List;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.SmsParameters;
import org.isf.menu.manager.Context;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mwithi
 * 31/gen/2014
 */
public class SmsSender implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(SmsSender.class);

	private boolean running = true;
	private int delay = 10;

	public SmsSender() {
		logger.info("SMS Sender started...");
		SmsParameters.initialize();
		delay = SmsParameters.LOOP;
		logger.info("SMS Sender loop set to {} seconds.", delay);
	}

	public void run() {
		while (running) {
			logger.info("SMS Sender running...");
			SmsOperations smsOp = Context.getApplicationContext().getBean(SmsOperations.class);
			List<Sms> smsList = null;
			try {
				smsList = smsOp.getList();
			} catch (OHServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (!smsList.isEmpty()) {
				logger.info("Found {} SMS to send", smsList.size());
				if (SmsParameters.MODE.equals("GSM")) {
					
					SmsSenderGSM sender = new SmsSenderGSM();
					if (sender.initialize()) {
						for (Sms sms : smsList) {
							if (sms.getSmsDateSched().before(new Date())) {
								boolean result = sender.sendSMS(sms, GeneralData.DEBUG);
								if (result) {
									sms.setSmsDateSent(new Date());
									try {
										smsOp.saveOrUpdate(sms);
									} catch (OHServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									logger.debug("Sent");
								} else {
									logger.error("Not sent");
								}
							}
						}
						sender.terminate();
					} else {
						logger.error("SMS Sender GSM initialization error");
						logger.error("Stopping SMS Sender...");
						setRunning(false);
					}
					
				} else if (SmsParameters.MODE.equals("HTTP")) {
					
					SmsSenderHTTP sender = new SmsSenderHTTP();
					if (sender.initialize()) {
						for (Sms sms : smsList) {
							if (sms.getSmsDateSched().before(new Date())) {
								boolean result = sender.sendSMS(sms, GeneralData.DEBUG);
								if (result) {
									sms.setSmsDateSent(new Date());
									try {
										smsOp.saveOrUpdate(sms);
									} catch (OHServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									logger.debug("Sent");
								} else {
									logger.error("Not sent");
								}
							}
						}
					} else {
						logger.error("SMS Sender HTTP initialization error");
						logger.error("Stopping HTTP Sender...");
						setRunning(false);
					}
				}
			} else {
				logger.debug("No SMS to send.");
			}
			try {
				Thread.sleep(delay * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
