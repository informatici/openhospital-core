package org.isf.sms.providers.skebby;

import java.awt.TrayIcon.MessageType;
import java.util.Arrays;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.skebby.model.SckebbySmsRequest;
import org.springframework.stereotype.Component;

@Component
public class SkebbyGatewayConverter {

	public SckebbySmsRequest toServiceDTO(Sms sms, MessageType messageType, String sender) {
		SckebbySmsRequest result = new SckebbySmsRequest();
		result.setMessage(sms.getSmsText());
		result.setRecipient(Arrays.asList(sms.getSmsNumber()));
		if (messageType != null) {
			result.setMessageType(messageType.name());
		}
		result.setSender(sender);
		return result;
	}

}
