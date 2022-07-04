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
package org.isf.sms.providers.skebby.remote;

import org.isf.sms.providers.skebby.model.SckebbySmsRequest;
import org.isf.sms.providers.skebby.model.SckebbySmsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "skebby-gateway-service")
public interface SkebbyGatewayRemoteService {

	String AUTH_TOKEN = "Authorization";
	String USER_KEY = "user_key";
	String SESSION_KEY = "Session_key";
	String ACCESS_TOKEN = "Access_token";

	// @formatter:off

	@GetMapping(value = "/API/v1.0/REST/login")
	ResponseEntity<String> loginUserKeySessionKey(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);

	@GetMapping(value = "/API/v1.0/REST/token")
	ResponseEntity<String> loginUserKeyAccessToken(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);

	@PostMapping(value = "/API/v1.0/REST/sms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SckebbySmsResponse> sendSmsWithSessionKey(@RequestHeader(USER_KEY) String userKey, @RequestHeader(SESSION_KEY) String sessionKey,
			@RequestBody SckebbySmsRequest smsBody);
	
	
	@PostMapping(value = "/API/v1.0/REST/sms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SckebbySmsResponse> sendSmsWithAccessToken(@RequestHeader(USER_KEY) String userKey, @RequestHeader(ACCESS_TOKEN) String accessToken,
			@RequestBody SckebbySmsRequest smsBody);

	// @formatter:on
}
