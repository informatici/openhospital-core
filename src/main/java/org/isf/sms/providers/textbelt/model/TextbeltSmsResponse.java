package org.isf.sms.providers.textbelt.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextbeltSmsResponse implements Serializable {

	private static final long serialVersionUID = -5811243203241212375L;

	private Boolean success;
	private Integer quotaRemaining;
	private Long textId;
	private String error;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Integer getQuotaRemaining() {
		return quotaRemaining;
	}

	public void setQuotaRemaining(Integer quotaRemaining) {
		this.quotaRemaining = quotaRemaining;
	}

	public Long getTextId() {
		return textId;
	}

	public void setTextId(Long textId) {
		this.textId = textId;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "TextbeltSmsResponse [success=" + success + ", quotaRemaining=" + quotaRemaining + ", textId=" + textId + ", error=" + error + "]";
	}

}
