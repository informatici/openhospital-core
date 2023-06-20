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
import org.springframework.cloud.openfeign.support.SpringMvcContract;
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

	private static final String KEY_USER_KEY = "skebby-gateway-service.userKey";
	private static final String KEY_ACCESS_TOKEN = "skebby-gateway-service.accessToken";

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
		String sessionKeyOrAccessToken = SkebbyGatewayUtil.extractSessionKey(userKeyAccessToken);
		MessageType messageType = null;

		final String msgType = this.smsProperties.getProperty(KEY_MESSAGE_TYPE);
		if (msgType != null) {
			messageType = MessageType.valueOf(msgType);
		}

		final String sender = this.smsProperties.getProperty(KEY_SENDER);

		SckebbySmsRequest smsSendingRequest = this.skebbyGatewayConverter.toServiceDTO(sms, messageType, sender);

		SkebbyGatewayRemoteService httpClient = buildHttlClient();
		System.out.println("Sending...");
		SckebbySmsResponse result;
		try {
			if (this.isAccessTokenAuthentication()) {
				result = httpClient.sendSmsWithAccessToken(userKey, sessionKeyOrAccessToken, smsSendingRequest).getBody();
			} else {
				result = httpClient.sendSmsWithSessionKey(userKey, sessionKeyOrAccessToken, smsSendingRequest).getBody();
			}
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
		// For debug remember to update log level to: feign.Logger.Level.FULL. Happy debugging!
		return Feign.builder().encoder(new CustomCommonEncoder()).decoder(new CustomCommonDecoder()).logger(new Slf4jLogger(SkebbyGatewayRemoteService.class))
						.logLevel(feign.Logger.Level.BASIC).contract(new SpringMvcContract()).target(SkebbyGatewayRemoteService.class, baseUrl);
	}

	private String loginUserKeySessionKey() {
		// USER_KEY and ACCESS_TOKEN avoids the login call every time we need to send sms
		final String userKey = this.smsProperties.getProperty(KEY_USER_KEY);
		final String token = this.smsProperties.getProperty(KEY_ACCESS_TOKEN);
		if (userKey != null && !userKey.trim().isEmpty() && token != null && !token.trim().isEmpty()) {
			return userKey + ";" + token;
		}

		final String username = this.smsProperties.getProperty(KEY_USERNAME);
		final String password = this.smsProperties.getProperty(KEY_PASSWORD);
		SkebbyGatewayRemoteService httpClient = buildHttlClient();
		return httpClient.loginUserKeySessionKey(username, password).getBody();
	}

	private boolean isAccessTokenAuthentication() {
		// if user defined these properties, then it means that we will retrieve data with ACCESS_TOKEN (which does not expires -> SESSION_KEY instead expires)
		final String userKey = this.smsProperties.getProperty(KEY_USER_KEY);
		final String token = this.smsProperties.getProperty(KEY_ACCESS_TOKEN);
		return (userKey != null && !userKey.trim().isEmpty() && token != null && !token.trim().isEmpty());
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
