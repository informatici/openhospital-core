/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.sms.providers.skebby.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.junit.jupiter.api.Test;

class SkebbySmsRequestTest extends OHCoreTestCase {

	@Test
	void settersAndToString_shouldUseRequestFields() throws Exception {
		SkebbySmsRequest skebbySmsRequest = new SkebbySmsRequest();

		skebbySmsRequest.setMessageType(MessageType.GP.toString());
		assertThat(skebbySmsRequest.getMessageType()).isEqualTo(MessageType.GP.toString());

		skebbySmsRequest.setMessage("message");
		assertThat(skebbySmsRequest.getMessage()).isEqualTo("message");

		List<String> recipientList = new ArrayList<>();
		recipientList.add("Person 1");
		recipientList.add("Person 2");
		skebbySmsRequest.setRecipient(recipientList);
		assertThat(skebbySmsRequest.getRecipient()).isNotNull();
		assertThat(skebbySmsRequest.getRecipient()).containsExactlyInAnyOrder("Person 1", "Person 2");

		skebbySmsRequest.setSender("theSender");
		assertThat(skebbySmsRequest.getSender()).isEqualTo("theSender");

		skebbySmsRequest.setScheduledDeliveryTime("1 AM");
		assertThat(skebbySmsRequest.getScheduledDeliveryTime()).isEqualTo("1 AM");

		skebbySmsRequest.setOrderId("order");
		assertThat(skebbySmsRequest.getOrderId()).isEqualTo("order");

		skebbySmsRequest.setReturnCredits(false);
		assertThat(skebbySmsRequest.getReturnCredits()).isFalse();

		assertThat(skebbySmsRequest)
						.hasToString("SkebbySmsRequest [messageType=GP, message=***, recipient=***, sender=***, scheduledDeliveryTime=1 AM, orderId=order, returnCredits=false]");
	}

}
