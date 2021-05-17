package org.isf.sms.providers.textbelt.model;

import java.io.Serializable;

public class TextbeltSmsRequest implements Serializable {

	private static final long serialVersionUID = 6019474639994796818L;

	private String phone;
	private String message;
	private String key;

	public TextbeltSmsRequest() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "TextbeltSmsRequest [phone=" + phone + ", message=" + message + ", key=" + key + "]";
	}

}
