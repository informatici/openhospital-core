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
package org.isf.visits.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Visits
 * -----------------------------------------
 * modification history
 * ? - ? - first version
 * 1/08/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_VISITS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "VST_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "VST_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "VST_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "VST_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "VST_LAST_MODIFIED_DATE"))
public class Visit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "VST_ID")
	private int visitID;

	@NotNull
	@ManyToOne
	@JoinColumn(name="VST_PAT_ID")
	private Patient patient;
	
	/**
	 * if {@code null} the visit is meant for OPD. 
	 */
	@ManyToOne
	@JoinColumn(name="VST_WRD_ID_A")
	private Ward ward;

	@NotNull
	@Column(name="VST_DATE") 		// SQL type: datetime
	private LocalDateTime date;
	
	@Column(name="VST_NOTE")	
	private String note;
	
	@Column(name="VST_DURATION")	
	private Integer duration;
	
	@Column(name="VST_SERVICE")	
	private String service;
	
	@Column(name="VST_SMS")	
	private boolean sms;
	
	@Transient
	private volatile int hashCode = 0;
	

	public Visit() {
		super();
	}

	public Visit(int visitID, LocalDateTime date, Patient patient, String note, boolean sms, Ward ward, Integer duration, String service) {
		super();
		this.visitID = visitID;
		this.date = TimeTools.truncateToSeconds(date);
		this.patient = patient;
		this.note = note;
		this.sms = sms;		
		this.ward = ward;
		this.duration = duration;
		this.service = service;
	}
	
	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public int getVisitID() {
		return visitID;
	}

	public void setVisitID(int visitID) {
		this.visitID = visitID;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public boolean isSms() {
		return sms;
	}

	public void setSms(boolean sms) {
		this.sms = sms;
	}
	
	public String toStringSMS() {
		
		return formatDateTimeSMS(this.date);
	}

	public String formatDateTime(LocalDateTime time) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy - HH:mm:ss");
		return time.format(dtf);
	}
	
	public String formatDateTimeSMS(LocalDateTime time) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
		return time.format(dtf);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Visit)) {
			return false;
		}
		
		Visit visit = (Visit)obj;
		return (visitID == visit.getVisitID());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (ward != null) {
			sb.append(ward.getDescription());
		} else {
			sb.append(MessageBundle.getMessage("angal.menu.opd"));
		}
		if (service != null) {
			sb.append(" - ").append(service);
		}
		sb.append(" - ").append(formatDateTime(this.date));
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + visitID;
	        
	        this.hashCode = c;
	    }
	    return this.hashCode;
	}

}
