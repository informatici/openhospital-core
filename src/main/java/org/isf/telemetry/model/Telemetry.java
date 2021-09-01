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
package org.isf.telemetry.model;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author j
 *
 */
@Entity
@Table(name = "TELEMETRY")
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
	private Date sentTimestamp;

	@Column(name = "TEL_OPTIN_DATE")
	private Date optinDate;

	@Column(name = "TEL_OPTOUT_DATE")
	private Date optoutDate;

	@Transient
	private volatile int hashCode = 0;

	public Telemetry() {
	}

	public Date getSentTimestamp() {
		return sentTimestamp;
	}

	public void setSentTimestamp(Date sentTimestamp) {
		this.sentTimestamp = sentTimestamp;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getOptinDate() {
		return optinDate;
	}

	public void setOptinDate(Date optinDate) {
		this.optinDate = optinDate;
	}

	public Date getOptoutDate() {
		return optoutDate;
	}

	public void setOptoutDate(Date optoutDate) {
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
			Type type = generateTypeToken().getType();
			Gson gson = new Gson();
			return gson.fromJson(this.getConsentData(), type);
		}
		return null;
	}

	private TypeToken<Map<String, Boolean>> generateTypeToken() {
		return new TypeToken<Map<String, Boolean>>() {
		};
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
						+ ", consentData=" + consentData + ", info=" + info + ", hashCode=" + hashCode + "]";
	}

}
