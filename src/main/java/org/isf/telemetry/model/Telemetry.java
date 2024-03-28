/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.model;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author Andrei
 *
 */
@Entity
@Table(name = "OH_TELEMETRY")
public class Telemetry {

	@EmbeddedId
	private TelemetryId id;

	// necessary for daemon. Used to understand if daemon is active. TRUE | FALSE | NULL
	@Column(name = "TEL_ACTIVE")
	private Boolean active;

	@Column(name = "TEL_CONSENT")
	private String consentData;

	@Column(name = "TEL_INFO")
	private String info;

	@Column(name = "TEL_SENT_TIME")
	private LocalDateTime sentTimestamp;

	@Column(name = "TEL_OPTIN_DATE")
	private LocalDateTime optinDate;

	@Column(name = "TEL_OPTOUT_DATE")
	private LocalDateTime optoutDate;

	@Transient
	private volatile int hashCode;

	public Telemetry() {
	}

	public LocalDateTime getSentTimestamp() {
		return sentTimestamp;
	}

	public void setSentTimestamp(LocalDateTime sentTimestamp) {
		this.sentTimestamp = sentTimestamp;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDateTime getOptinDate() {
		return optinDate;
	}

	public void setOptinDate(LocalDateTime optinDate) {
		this.optinDate = optinDate;
	}

	public LocalDateTime getOptoutDate() {
		return optoutDate;
	}

	public void setOptoutDate(LocalDateTime optoutDate) {
		this.optoutDate = optoutDate;
	}

	public String getConsentData() {
		return consentData;
	}

	public void setConsentData(String consentData) {
		this.consentData = consentData;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public Map<String, Boolean> getConsentMap() {
		if (this.consentData != null && !this.consentData.isEmpty()) {
			Type type = TypeToken.getParameterized(Map.class, String.class, Boolean.class).getType();
			Gson gson = new Gson();
			return gson.fromJson(this.getConsentData(), type);
		}
		return null;
	}

	public void setConsentMap(Map<String, Boolean> consentMap) {
		Gson gson = new Gson();
		this.consentData = gson.toJson(consentMap);
	}

	public TelemetryId getId() {
		return id;
	}

	public void setId(TelemetryId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Telemetry [id=" + id + ", sentTimestamp=" + sentTimestamp + ", active=" + active + ", optinDate=" + optinDate + ", optoutDate=" + optoutDate
						+ ", consentData=" + consentData + ", info=" + info + ", hashCode=" + hashCode + ']';
	}

}
