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
package org.isf.sms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Generated 31-gen-2014 15.39.04 by Hibernate Tools 3.4.0.CR1
 *
 * @author Mwithi
 */
@Entity
@Table(name = "SMS")
public class Sms {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SMS_ID")
	private int smsId;

	@NotNull
	@Column(name = "SMS_DATE")
	private Date smsDate;

	@NotNull
	@Column(name = "SMS_DATE_SCHED")
	private Date smsDateSched;

	@NotNull
	@Column(name = "SMS_NUMBER")
	private String smsNumber;

	@NotNull
	@Column(name = "SMS_TEXT")
	private String smsText;

	@Column(name = "SMS_DATE_SENT")
	private Date smsDateSent;

	@NotNull
	@Column(name = "SMS_USER")
	private String smsUser;

	@NotNull
	@Column(name = "SMS_MOD")
	private String module;

	@Column(name = "SMS_MOD_ID")
	private String moduleID;

	@Transient
	private volatile int hashCode = 0;

	public Sms() {
	}

	public Sms(Date smsDateSched, String smsNumber, String smsText, String smsUser) {
		this.smsDateSched = smsDateSched;
		this.smsNumber = smsNumber;
		this.smsText = smsText;
		this.smsUser = smsUser;
	}

	public Sms(int smsId, Date smsDate, Date smsDateSched, String smsNumber, String smsText, Date smsDateSent, String smsUser, String module, String moduleID) {
		this.smsId = smsId;
		this.smsDate = smsDate;
		this.smsDateSched = smsDateSched;
		this.smsNumber = smsNumber;
		this.smsText = smsText;
		this.smsDateSent = smsDateSent;
		this.smsUser = smsUser;
		this.module = module;
		this.moduleID = moduleID;
	}

	public int getSmsId() {
		return this.smsId;
	}

	public void setSmsId(int smsId) {
		this.smsId = smsId;
	}

	public Date getSmsDate() {
		return this.smsDate;
	}

	public void setSmsDate(Date smsDate) {
		this.smsDate = smsDate;
	}

	public Date getSmsDateSched() {
		return this.smsDateSched;
	}

	public void setSmsDateSched(Date smsDateSched) {
		this.smsDateSched = smsDateSched;
	}

	public String getSmsNumber() {
		return this.smsNumber;
	}

	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}

	public String getSmsText() {
		return this.smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public Date getSmsDateSent() {
		return this.smsDateSent;
	}

	public void setSmsDateSent(Date smsDateSent) {
		this.smsDateSent = smsDateSent;
	}

	public String getSmsUser() {
		return this.smsUser;
	}

	public void setSmsUser(String smsUser) {
		this.smsUser = smsUser;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Sms)) {
			return false;
		}

		Sms sms = (Sms) obj;
		return (this.getSmsId() == sms.getSmsId());
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			final int m = 23;
			int c = 133;

			c = m * c + smsId;

			this.hashCode = c;
		}

		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Sms [smsId=" + smsId + ", smsDate=" + smsDate + ", smsDateSched=" + smsDateSched + ", smsNumber=***" + ", smsText=***" + ", smsDateSent="
						+ smsDateSent + ", smsUser=" + smsUser + ", module=" + module + ", moduleID=" + moduleID + ", hashCode=" + hashCode + "]";
	}

	private String hide(String value, boolean isHidden) {
		return isHidden ? "***" : value;
	}

}
