/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.patvac.model;

import java.time.LocalDateTime;

/*------------------------------------------
* PatientVaccine - class 
* -----------------------------------------
* modification history
* 25/08/2011 - claudia - first beta version
* 04/06/2015 - Antonio - ported to JPA
*------------------------------------------*/

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

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

import org.isf.utils.db.Auditable;
import org.isf.patient.model.Patient;
import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="PATIENTVACCINE")
@EntityListeners(AuditingEntityListener.class) 
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="PAV_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="PAV_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="PAV_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="PAV_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="PAV_LAST_MODIFIED_DATE"))
})
public class PatientVaccine extends Auditable<String>
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PAV_ID")
	private int code;

	@NotNull
	@Column(name="PAV_YPROG")
	private int progr;

	@NotNull
	@Column(name="PAV_DATE")		// SQL type: datetime
	private LocalDateTime vaccineDate;

	@NotNull
	@ManyToOne
	@JoinColumn(name="PAV_PAT_ID")
	private Patient patient;

	@NotNull
	@ManyToOne
	@JoinColumn(name="PAV_VAC_ID_A")
	private Vaccine vaccine;
	
	@Column(name="PAV_LOCK")
	private int lock;
	
	@Transient
	private volatile int hashCode = 0;

	
	public PatientVaccine()
	{		
	}
	
	public PatientVaccine(int codeIn, int progIn, LocalDateTime vacDateIn, 
			Patient patient, Vaccine vacIn, int lockIn) {
		this.code = codeIn;
		this.progr = progIn;
		this.vaccineDate = vacDateIn;
		this.patient = patient;
		this.vaccine = vacIn;
		this.lock = lockIn ;
	}
	
	public PatientVaccine(int codeIn, int progIn, LocalDateTime vacDateIn, 
			Patient patient, Vaccine vacIn, int lockIn,
                          String patNameIn, int patAgeIn, char patSexIn) {
		this.code = codeIn;
		this.progr = progIn;
		this.vaccineDate = vacDateIn;
		this.patient = patient;
		this.vaccine = vacIn;
		this.lock = lockIn ;
		patient.setFirstName(patNameIn);
		patient.setAge(patAgeIn);
		patient.setSex(patSexIn);
	}
	
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getProgr() {
		return progr;
	}

	public void setProgr(int progr) {
		this.progr = progr;
	}

	public LocalDateTime getVaccineDate() {
		return vaccineDate;
	}

	public void setVaccineDate(LocalDateTime vaccineDate) {
		this.vaccineDate = vaccineDate;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}
	

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getPatName() {
		return patient.getFirstName();
	}

	public void setPatName(String patName) {
		this.patient.setFirstName(patName);
	}

	public int getPatAge() {
		return patient.getAge();
	}

	public void setPatAge(int patAge) {
		this.patient.setAge(patAge);
	}

	public char getPatSex() {
		return patient.getSex();
	}

	public void setPatSex(char patSex) {
		this.patient.setSex(patSex);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int hascode = 1;
		hascode = prime * hascode + code;
		hascode = prime * hascode + ((patient == null) ? 0 : patient.hashCode());
		hascode = prime * hascode + progr;
		hascode = prime * hascode + ((vaccine == null) ? 0 : vaccine.hashCode());
		hascode = prime * hascode
				+ ((vaccineDate == null) ? 0 : vaccineDate.hashCode());
		return hascode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PatientVaccine)) {
			return false;
		}
		PatientVaccine other = (PatientVaccine) obj;
		if (code != other.code) {
			return false;
		}
		if (patient == null && other.patient != null) {
				return false;
		}
		if (progr != other.progr) {
			return false;
		}
		if (vaccine == null) {
			if (other.vaccine != null) {
				return false;
			}
		} else if (!vaccine.equals(other.vaccine)) {
			return false;
		}
		if (vaccineDate == null) {
			if (other.vaccineDate != null) {
				return false;
			}
		} else if (!vaccineDate.equals(other.vaccineDate)) {
			return false;
		}
		return true;
	}

}
