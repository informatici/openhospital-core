package org.isf.sms.providers.skebby.remote;

import org.isf.sms.providers.skebby.model.SckebbySmsRequest;
import org.isf.sms.providers.skebby.model.SckebbySmsResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "skebby-gateway-service")
public interface SkebbyGatewayRemoteService {

	public static final String AUTH_TOKEN = "Authorization";
	public static final String USER_KEY = "user_key";
	public static final String SESSION_KEY = "session_key";

	// @formatter:off

	@GetMapping(value = "/API/v1.0/REST/login")
	public ResponseEntity<String> loginUserKeySessionKey(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);

	@GetMapping(value = "/API/v1.0/REST/token")
	public ResponseEntity<String> loginUserKeyAccessToken(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password);

	@PostMapping(value = "/API/v1.0/REST/sms", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SckebbySmsResponse> sendSms(@RequestHeader(USER_KEY) String userKey, @RequestHeader(SESSION_KEY) String sessionKey,
					@RequestBody SckebbySmsRequest smsBody);

	// @formatter:on
}
