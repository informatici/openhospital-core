/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.List;

import org.isf.generaldata.SmsParameters;
import org.isf.menu.manager.Context;
import org.isf.sms.model.Sms;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mwithi 31/gen/2014
 */
public class SmsSender implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsSender.class);

	private boolean running = true;
	private int delay;

	public SmsSender() {
		LOGGER.info("SMS Sender started...");
		SmsParameters.initialize();
		delay = SmsParameters.LOOP;
		LOGGER.info("SMS Sender loop set to {} seconds.", delay);
	}

	@Override
	public void run() {
		while (running) {
			LOGGER.info("SMS Sender running...");
			SmsOperations smsOp = Context.getApplicationContext().getBean(SmsOperations.class);
			List<Sms> smsList = null;
			try {
				smsList = smsOp.getList();
			} catch (OHServiceException e1) {
				LOGGER.error("Error list loading");
			}
			if (!smsList.isEmpty()) {
				LOGGER.info("Found {} SMS to send", smsList.size());
				SmsSenderOperations sender = Context.getApplicationContext().getBean(SmsSenderOperations.class);
				if (sender.initialize()) {
					for (Sms sms : smsList) {
						if (sms.getSmsDateSched().isBefore(TimeTools.getNow())) {
							boolean result = sender.sendSMS(sms);
							if (result) {
								sms.setSmsDateSent(TimeTools.getNow());
								try {
									smsOp.saveOrUpdate(sms);
								} catch (OHServiceException e) {
									LOGGER.error("Failed saving: {}", e.getMessage());
								}
								LOGGER.debug("Sent");
							} else {
								LOGGER.error("Not sent");
							}
						}
					}
					boolean terminationResult = sender.terminate();
					LOGGER.debug("termination result: {}", terminationResult);
				} else {
					LOGGER.error("SMS Sender HTTP initialization error");
					LOGGER.error("Stopping HTTP Sender...");
					setRunning(false);
				}
			} else {
				LOGGER.debug("No SMS to send.");
			}
			try {
				Thread.sleep(delay * 1000);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
			}
		}
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
}
