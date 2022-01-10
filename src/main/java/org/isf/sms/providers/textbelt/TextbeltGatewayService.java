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
package org.isf.sms.providers.textbelt;

import java.util.Properties;

import javax.annotation.Resource;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.SmsSenderInterface;
import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.isf.sms.providers.textbelt.model.TextbeltSmsRequest;
import org.isf.sms.providers.textbelt.model.TextbeltSmsResponse;
import org.isf.sms.providers.textbelt.remote.TextbeltGatewayRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.slf4j.Slf4jLogger;

@Component
public class TextbeltGatewayService implements SmsSenderInterface {

	private static final String SERVICE_NAME = "textbelt-gateway-service";
	private static final Boolean RESPONSE_SUCCESS = Boolean.TRUE;

	private static final Logger LOGGER = LoggerFactory.getLogger(TextbeltGatewayService.class);

	@Resource(name = "smsProperties")
	private Properties smsProperties;

	@Autowired
	private TextbeltGatewayConverter textbeltGatewayConverter;

	@Override
	public boolean sendSMS(Sms sms) {
		String sessionKey = retrieveSessionKey();
		TextbeltSmsRequest smsSendingRequest = this.textbeltGatewayConverter.toServiceDTO(sessionKey, sms);
		TextbeltGatewayRemoteService httpClient = buildHttlClient();
		LOGGER.debug("TextBeltRequest: {}", smsSendingRequest);
		ResponseEntity<TextbeltSmsResponse> rs = httpClient.sendSMS(smsSendingRequest);
		TextbeltSmsResponse result = rs.getBody();
		LOGGER.debug("TextBeltResponse: {}", result);
		return result != null && RESPONSE_SUCCESS.equals(result.getSuccess());
	}

	private TextbeltGatewayRemoteService buildHttlClient() {
		String baseUrl = this.smsProperties.getProperty(this.getRootKey() + ".ribbon.base-url");
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy debugging!
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder()).logger(new Slf4jLogger(TextbeltGatewayService.class))
						.logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract()).target(TextbeltGatewayRemoteService.class, baseUrl);
	}

	private String retrieveSessionKey() {
		String key = this.smsProperties.getProperty(this.getRootKey() + ".key");
		if (Boolean.parseBoolean(this.smsProperties.getProperty(this.getRootKey() + ".enable-testing-mode"))) {
			key = key + "_test";
		}
		return key;
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
	public boolean initialize() {
		return true;
	}

	@Override
	public boolean terminate() {
		return true;
	}

}
