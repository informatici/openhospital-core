package org.isf.sms.providers.textbelt;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.textbelt.model.TextbeltSmsRequest;
import org.springframework.stereotype.Component;

@Component
public class TextbeltGatewayConverter {

	public TextbeltSmsRequest toServiceDTO(String key, Sms sms) {
		TextbeltSmsRequest result = new TextbeltSmsRequest();
		result.setKey(key);
		result.setMessage(sms.getSmsText());
		result.setPhone(sms.getSmsNumber());
		return result;
	}

}
