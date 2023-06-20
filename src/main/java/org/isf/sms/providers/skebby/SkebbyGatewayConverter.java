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
import java.util.Collections;

import org.isf.sms.model.Sms;
import org.isf.sms.providers.skebby.model.SckebbySmsRequest;
import org.springframework.stereotype.Component;

@Component
public class SkebbyGatewayConverter {

	public SckebbySmsRequest toServiceDTO(Sms sms, MessageType messageType, String sender) {
		SckebbySmsRequest result = new SckebbySmsRequest();
		result.setMessage(sms.getSmsText());
		result.setRecipient(Collections.singletonList(sms.getSmsNumber()));
		if (messageType != null) {
			result.setMessageType(messageType.name());
		}
		result.setSender(sender);
		return result;
	}

}
