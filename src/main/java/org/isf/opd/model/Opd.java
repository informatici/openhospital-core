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
package org.isf.opd.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.disease.model.Disease;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.visits.model.Visit;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.drew.lang.annotations.Nullable;

/**
 * ------------------------------------------
 * Opd - model for OPD
 * -----------------------------------------
 * modification history
 * 11/12/2005 - Vero, Rick  pupo
 * 21/11/2006 - ross - renamed from Surgery 
 *                   - added visit date, disease 2, diseas3
 *                   - disease is not mandatory if re-attendance
 * 			         - version is now 1.0 
 * 12/06/2008 - ross - added referral from / to
 * 16/06/2008 - ross - added patient detail
 * 05/09/2008 - alex - added fullname e notefield
 * 09/01/2009 - fabrizio - date field modified to type Date
 * 02/06/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_OPD")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OPD_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "OPD_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OPD_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OPD_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OPD_LAST_MODIFIED_DATE"))
public class Opd extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="OPD_ID") 
	private int code;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "OPD_WRD_ID_A")
	private Ward ward;

	@NotNull
	@Column(name="OPD_DATE") // SQL type: datetime
	private LocalDateTime date;

	@ManyToOne
	@JoinColumn(name="OPD_PAT_ID")
	private Patient patient;

	@NotNull
	@Column(name="OPD_AGE")
	private int age;

	@NotNull
	@Column(name="OPD_SEX")
	private char sex;

	@Nullable
	@Column(name="OPD_NOTE")
	private String note;

	@NotNull
	@Column(name="OPD_PROG_YEAR")	
	private int prog_year;

	@ManyToOne
	@JoinColumn(name="OPD_DIS_ID_A")
	private Disease disease;
	
	@ManyToOne
	@JoinColumn(name="OPD_DIS_ID_A_2")
	private Disease disease2;
	
	@ManyToOne
	@JoinColumn(name="OPD_DIS_ID_A_3")
	private Disease disease3;

	@NotNull
	@Column(name="OPD_NEW_PAT")
	private char newPatient;	//n=NEW R=REATTENDANCE
	
	@Column(name="OPD_REFERRAL_FROM")
	private String referralFrom;	//R=referral from another unit; null=no referral from
	
	@Column(name="OPD_REFERRAL_TO")
	private String referralTo;		//R=referral to another unit; null=no referral to 

	@NotNull
	@Column(name="OPD_USR_ID_A")
	private String userID;
	
	@ManyToOne
	@JoinColumn(name = "OPD_NEXT_VISIT_ID")
	private Visit nextVisit;

	@Version
	@Column(name="OPD_LOCK")
	private int lock;
	
	/*@Column(name="OPD_REASON")
   	private String reason; // ADDED: Arnaud

	@Column(name="OPD_THERAPIES")
	private String therapies; // ADDED: Arnaud*/
	
	/**
	 * Field for "ui"
	 */
	@Nullable
	@Column(name="OPD_PRESCRIPTION")
	private String prescription; // ADDED: Arnaud
	
	@Transient
	private volatile int hashCode = 0;


	public Opd() {
	}
	
	/**
 	 * @param aProgYear
 	 * @param aSex
 	 * @param aAge
 	 * @param aDisease
 	 */
	public Opd(int aProgYear, char aSex, int aAge, Disease aDisease) {
		prog_year = aProgYear;
		sex = aSex;
		age = aAge;
		disease = aDisease;
	}
	
	public String getFullName() {
		return patient == null ? "" : patient.getName();
	}

	/**
	 * Field for "ui"
	 */
	public String getPrescription() {
		return prescription;
	}

	/**
	 * Field for "ui"
	 */
	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getfirstName() {
		return patient == null ? "" : patient.getFirstName();
	}

	public String getsecondName() {
		return patient == null ? "" : patient.getSecondName();
	}

	public String getnextKin() {
		return patient == null ? "" : patient.getNextKin();
	}

	public String getcity() {
		return patient == null ? "" : patient.getCity();
	}

	public String getaddress() {
		return patient == null ? "" : patient.getAddress();
	}
	
	public char getNewPatient() {
		return newPatient;
	}

	public void setNewPatient(char newPatient) {
		this.newPatient = newPatient;
	}

	public String getReferralTo() {
		return referralTo;
	}

	public void setReferralTo(String referralTo) {
		this.referralTo = referralTo;
	}

	public String getReferralFrom() {
		return referralFrom;
	}

	public void setReferralFrom(String referralFrom) {
		this.referralFrom = referralFrom;
	}
	
	public Ward getWard() {
		return ward;
	}
	
	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public Disease getDisease2() {
		return disease2;
	}
	
	public Disease getDisease3() {
		return disease3;
	}
	
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	
	public void setDisease2(Disease disease) {
		this.disease2 = disease;
	}
	
	public void setDisease3(Disease disease) {
		this.disease3 = disease;
	}
	
	public int getLock() {
		return lock;
	}
	
	public void setLock(int lock) {
		this.lock = lock;
	}

	public LocalDateTime getDate() {
		return date;
	}
	
	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public char getSex() {
		return sex;
	}
	
	public void setSex(char sex) {
		this.sex = sex;
	}
	
	public int getProgYear() {
		return prog_year;
	}
	
	public void setProgYear(int progYear) {
		this.prog_year = progYear;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
        
	/*public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getTherapies() {
		return therapies;
	}

	public void setTherapies(String therapies) {
		this.therapies = therapies;
	}

	public String getPrescription() {
		return prescription;
	}

	public void setPrescription(String prescription) {
		this.prescription = prescription;
	}*/
	
	public Visit getNextVisit() {
		return nextVisit;
	}
	
	public void setNextVisit(Visit nextVisit) {
		this.nextVisit = nextVisit;
	}
	
	public boolean isPersisted() {
		return code > 0;
	}

	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + code;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Opd)) {
			return false;
		}
		
		Opd opd = (Opd)obj;
		return (code == opd.getCode());
	}
}
