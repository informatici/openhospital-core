/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import java.util.Optional;
import java.util.Properties;

import javax.annotation.Resource;

import org.isf.generaldata.SmsParameters;
import org.isf.sms.model.Sms;
import org.isf.sms.providers.SmsSenderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class SmsSenderOperations {

	private static final String KEY_SMS_GATEWAY = "sms.gateway";
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderOperations.class);

	@Resource(name = "smsProperties")
	private Properties smsProperties;

	@Autowired
	private List<SmsSenderInterface> smsGateways;

	public boolean initialize() {
		String gateway = this.smsProperties.getProperty(KEY_SMS_GATEWAY);
		if (gateway == null || gateway.isEmpty()) {
			LOGGER.error("No HTTP Gateway has been set. Please check sms.properties file (v.2 - init)");
			return false;
		}
		Optional<SmsSenderInterface> smsGatewayOpt = findSmsGatewayService(gateway);
		if (smsGatewayOpt.isPresent()) {
			return smsGatewayOpt.get().initialize();
		}
		return false;
	}

	public void preSMSSending(Sms sms) {
		if (sms != null && sms.getSmsNumber() != null && !sms.getSmsNumber().startsWith(SmsParameters.ICC)) {
			sms.setSmsNumber(SmsParameters.ICC + sms.getSmsNumber());
		}
	}

	public boolean sendSMS(Sms sms) {
		String gateway = this.smsProperties.getProperty(KEY_SMS_GATEWAY);
		if (gateway == null || gateway.isEmpty()) {
			LOGGER.error("No HTTP Gateway has been set. Please check sms.properties file (v.2 - send sms)");
			return false;
		}
		Optional<SmsSenderInterface> smsGatewayOpt = findSmsGatewayService(gateway);
		if (smsGatewayOpt.isPresent()) {
			this.preSMSSending(sms);
			LOGGER.debug("sending sms: {}", sms);
			boolean result = smsGatewayOpt.get().sendSMS(sms);
			LOGGER.debug("sms sending result: {}", result);
			return result;
		}
		return false;
	}

	public boolean terminate() {
		String gateway = this.smsProperties.getProperty(KEY_SMS_GATEWAY);
		if (gateway == null || gateway.isEmpty()) {
			LOGGER.error("No HTTP Gateway has been set. Please check sms.properties file (v.2 - init)");
			return false;
		}
		Optional<SmsSenderInterface> smsGatewayOpt = findSmsGatewayService(gateway);
		if (smsGatewayOpt.isPresent()) {
			return smsGatewayOpt.get().terminate();
		}
		return false;
	}

	private Optional<SmsSenderInterface> findSmsGatewayService(String gateway) {
		return this.smsGateways.stream().filter(smsGateway -> gateway.equals(smsGateway.getName())).findFirst();
	}

}
