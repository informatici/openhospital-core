/*
 * Open Hospital (www.open-hospital.org) Copyright © 2006-2021 Informatici Senza
 * Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data
 * management.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.sms.providers.skebby;

import java.awt.TrayIcon.MessageType;
import java.util.Properties;

import javax.annotation.Resource;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.SmsSenderInterface;
import org.isf.sms.providers.common.CustomCommonDecoder;
import org.isf.sms.providers.common.CustomCommonEncoder;
import org.isf.sms.providers.skebby.model.SckebbySmsRequest;
import org.isf.sms.providers.skebby.model.SckebbySmsResponse;
import org.isf.sms.providers.skebby.remote.SkebbyGatewayRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.stereotype.Component;

import feign.Feign;
import feign.FeignException;
import feign.slf4j.Slf4jLogger;

@Component
public class SkebbyGatewayService implements SmsSenderInterface {

	private static final String SERVICE_NAME = "skebby-gateway-service";
	private static final String RESPONSE_SUCCESS = "OK";
	private static final String KEY_PASSWORD = "skebby-gateway-service.password";
	private static final String KEY_USERNAME = "skebby-gateway-service.username";
	private static final String KEY_MESSAGE_TYPE = "skebby-gateway-service.message-type";
	private static final String KEY_SENDER = "skebby-gateway-service.sender";

	private static final Logger LOGGER = LoggerFactory.getLogger(SkebbyGatewayService.class);

	@Resource(name = "smsProperties")
	private Properties smsProperties;

	@Autowired
	private SkebbyGatewayConverter skebbyGatewayConverter;

	@Override
	public boolean sendSMS(Sms sms) {
		String userKeyAccessToken = loginUserKeySessionKey();
		String userKey = SkebbyGatewayUtil.extractUserKey(userKeyAccessToken);
		String sessionKey = SkebbyGatewayUtil.extractSessionKey(userKeyAccessToken);
		MessageType messageType = null;

		final String msgType = this.smsProperties.getProperty(KEY_MESSAGE_TYPE);
		if (msgType != null) {
			messageType = MessageType.valueOf(msgType);
		}

		final String sender = this.smsProperties.getProperty(KEY_SENDER);

		SckebbySmsRequest smsSendingRequest = this.skebbyGatewayConverter.toServiceDTO(sms, messageType, sender);

		SkebbyGatewayRemoteService httpClient = buildHttlClient();
		System.out.println("Sending...");
		SckebbySmsResponse result = null;
		try {
			result = httpClient.sendSms(userKey, sessionKey, smsSendingRequest).getBody();
		} catch (FeignException fe) {
			// skebby replies with HTTP 400 when you have not more sms, so that we could have an exception
			LOGGER.error("Gateway error!");
			LOGGER.error(fe.getMessage());
			return false;
		}
		return result != null && RESPONSE_SUCCESS.equals(result.getResult());
	}

	private SkebbyGatewayRemoteService buildHttlClient() {
		String baseUrl = this.smsProperties.getProperty(this.getRootKey() + ".ribbon.base-url");
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder()).logger(new Slf4jLogger(SkebbyGatewayRemoteService.class))
						.logLevel(feign.Logger.Level.FULL).contract(new SpringMvcContract()).target(SkebbyGatewayRemoteService.class, baseUrl);
	}

	private String loginUserKeySessionKey() {
		final String username = this.smsProperties.getProperty(KEY_USERNAME);
		final String password = this.smsProperties.getProperty(KEY_PASSWORD);
		SkebbyGatewayRemoteService httpClient = buildHttlClient();
		return httpClient.loginUserKeySessionKey(username, password).getBody();
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
