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
package org.isf.sms.providers.skebby.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SckebbySmsRequest implements Serializable {

	private static final long serialVersionUID = -1860994148316130286L;

	@SerializedName(value = "message_type")
	private String messageType = MessageType.SI.name();
	private String message;
	private List<String> recipient;
	private String sender;
	@SerializedName(value = "scheduled_delivery_time")
	private String scheduledDeliveryTime;
	@SerializedName(value = "order_id")
	private String orderId;
	private Boolean returnCredits;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getRecipient() {
		return recipient;
	}

	public void setRecipient(List<String> recipient) {
		this.recipient = recipient;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getScheduledDeliveryTime() {
		return scheduledDeliveryTime;
	}

	public void setScheduledDeliveryTime(String scheduledDeliveryTime) {
		this.scheduledDeliveryTime = scheduledDeliveryTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Boolean getReturnCredits() {
		return returnCredits;
	}

	public void setReturnCredits(Boolean returnCredits) {
		this.returnCredits = returnCredits;
	}

	@Override
	public String toString() {
		return "SckebbySmsRequest [messageType=" + messageType + ", message=***" + ", recipient=***" + ", sender=***" + ", scheduledDeliveryTime="
						+ scheduledDeliveryTime + ", orderId=" + orderId + ", returnCredits=" + returnCredits + "]";
	}

}
