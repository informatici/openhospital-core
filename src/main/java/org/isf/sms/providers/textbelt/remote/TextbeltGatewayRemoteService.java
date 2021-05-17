package org.isf.sms.providers.textbelt.remote;

import org.isf.sms.providers.textbelt.model.TextbeltSmsRequest;
import org.isf.sms.providers.textbelt.model.TextbeltSmsResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "textbelt-gateway-service")
public interface TextbeltGatewayRemoteService {

	// @formatter:off

	@PostMapping(value = "/text", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TextbeltSmsResponse> sendSMS(@RequestBody TextbeltSmsRequest smsRequest);

	// @formatter:on
}
