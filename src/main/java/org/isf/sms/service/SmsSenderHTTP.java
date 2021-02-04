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

import org.isf.generaldata.SmsParameters;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.SkebbyGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mwithi
 * 03/feb/2014
 */
public class SmsSenderHTTP implements SmsSenderInterface {

	private static Logger logger = LoggerFactory.getLogger(SmsSenderHTTP.class);
	private SmsSenderInterface smsSender;

	public SmsSenderHTTP() {
		logger.info("SMS Sender HTTP started...");
		SmsParameters.getSmsParameters();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SmsSenderHTTP sender = new SmsSenderHTTP();
		sender.sendSMS(new Sms(), true);
		
	}

	@Override
	public boolean sendSMS(Sms sms, boolean test) {
		return smsSender.sendSMS(sms, test);
	}

	public boolean initialize() {
		String gateway = SmsParameters.GATEWAY;
		if (gateway.equals("")) {
			logger.error("No HTTP Gateway has been set. Please check sms.properties file");
			return false;
		}
		if (gateway.equalsIgnoreCase("Skebby")) {
			smsSender = new SkebbyGateway();
		} else {
			logger.error("HTTP Gateway not found. Please check sms.properties file");
			return false;
		}
		return smsSender.initialize(); 
	}
}
