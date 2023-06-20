/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.therapy.model;

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.medicals.model.Medical;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * TherapyRow : Bean to collect data from DB table THERAPIES
 * -----------------------------------------
 * modification history
 * ? - Mwithi - first version
 * 1/08/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_THERAPIES")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "THR_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "THR_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "THR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "THR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "THR_LAST_MODIFIED_DATE"))
public class TherapyRow extends Auditable<String> {

	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="THR_ID")	
	private int therapyID;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "THR_PAT_ID")
	Patient patient;

	@NotNull
	@Column(name="THR_STARTDATE")	
	private LocalDateTime startDate;

	@NotNull
	@Column(name="THR_ENDDATE")	
	private LocalDateTime endDate;

	@NotNull
	@Column(name="THR_MDSR_ID")	
	private Integer medicalId;

	@NotNull
	@Column(name="THR_QTY")	
	private Double qty;

	@NotNull
	@Column(name="THR_UNT_ID")	
	private int unitID;

	@NotNull
	@Column(name="THR_FREQINDAY")	
	private int freqInDay;

	@NotNull
	@Column(name="THR_FREQINPRD")	
	private int freqInPeriod;
	
	@Column(name="THR_NOTE")	
	private String note;

	@NotNull
	@Column(name="THR_NOTIFY")	
	private int notifyInt;

	@NotNull
	@Column(name="THR_SMS")	
	private int smsInt;

	@Transient
	private volatile int hashCode = 0;
	
	
	public TherapyRow() {
		super();
	}

	/**
	 * @param therapyID
	 * @param patient
	 * @param startDate
	 * @param endDate
	 * @param medical
	 * @param qty
	 * @param unitID
	 * @param freqInDay
	 * @param freqInPeriod
	 * @param note
	 * @param notify
	 * @param sms
	 */
	public TherapyRow(int therapyID, Patient patient,
			LocalDateTime startDate, LocalDateTime endDate,
			Medical medical, Double qty, int unitID, int freqInDay,
			int freqInPeriod, String note, boolean notify, boolean sms) {
		super();
		this.therapyID = therapyID;
		this.patient = patient;
		this.startDate = TimeTools.truncateToSeconds(startDate);
		this.endDate = TimeTools.truncateToSeconds(endDate);
		this.medicalId = medical.getCode();
		this.qty = qty;
		this.unitID = unitID;
		this.freqInDay = freqInDay;
		this.freqInPeriod = freqInPeriod;
		this.note = note;
		this.notifyInt = notify ? 1 : 0;
		this.smsInt = sms ? 1 : 0;
	}

	public int getTherapyID() {
		return therapyID;
	}

	public void setTherapyID(int therapyID) {
		this.therapyID = therapyID;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = TimeTools.truncateToSeconds(startDate);
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = TimeTools.truncateToSeconds(endDate);
	}

	public Integer getMedical() {
		return medicalId;
	}

	public void setMedical(Medical medical) {
		this.medicalId = medical.getCode();
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public int getUnitID() {
		return unitID;
	}

	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}

	public int getFreqInDay() {
		return freqInDay;
	}

	public void setFreqInDay(int freqInDay) {
		this.freqInDay = freqInDay;
	}

	public int getFreqInPeriod() {
		return freqInPeriod;
	}

	public void setFreqInPeriod(int freqInPeriod) {
		this.freqInPeriod = freqInPeriod;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isNotify() {
		return (this.notifyInt == 1);
	}

	public void setNotify(boolean notify) {
		this.notifyInt = notify ? 1 : 0;
	}

	public boolean isSms() {
		return (this.smsInt == 1);
	}

	public void setSms(boolean sms) {
		this.smsInt = sms ? 1 : 0;
	}

	public Integer getMedicalId() {
		return medicalId;
	}

	public void setMedicalId(Integer medicalId) {
		this.medicalId = medicalId;
	}

	public int getNotifyInt() {
		return notifyInt;
	}

	public void setNotifyInt(int notifyInt) {
		this.notifyInt = notifyInt;
	}

	public int getSmsInt() {
		return smsInt;
	}

	public void setSmsInt(int smsInt) {
		this.smsInt = smsInt;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(medicalId);
		sb.append(" - ");
		sb.append(this.unitID);
		sb.append(" ");
		sb.append(this.qty);
		sb.append("/");
		sb.append(freqInDay);
		sb.append("/");
		sb.append(this.freqInPeriod);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof TherapyRow)) {
			return false;
		}
		
		TherapyRow therapy = (TherapyRow)obj;
		return (therapyID == therapy.getTherapyID());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + therapyID;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}
}
