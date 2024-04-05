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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.patvac.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.vaccine.model.Vaccine;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_PATIENTVACCINE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "PAV_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "PAV_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "PAV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "PAV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "PAV_LAST_MODIFIED_DATE"))
public class PatientVaccine extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	private volatile int hashCode;

	
	public PatientVaccine()
	{		
	}

	public PatientVaccine(int codeIn, int progIn, LocalDateTime vacDateIn, Patient patient, Vaccine vacIn, int lockIn) {
		this.code = codeIn;
		this.progr = progIn;
		this.vaccineDate = TimeTools.truncateToSeconds(vacDateIn);
		this.patient = patient;
		this.vaccine = vacIn;
		this.lock = lockIn;
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
		this.vaccineDate = TimeTools.truncateToSeconds(vaccineDate);
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
		return patient.getName();
	}

	public int getPatAge() {
		return patient.getAge();
	}

	public char getPatSex() {
		return patient.getSex();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hascode = 1;
		hascode = prime * hascode + code;
		hascode = prime * hascode + ((patient == null) ? 0 : patient.hashCode());
		hascode = prime * hascode + progr;
		hascode = prime * hascode + ((vaccine == null) ? 0 : vaccine.hashCode());
		hascode = prime * hascode + ((vaccineDate == null) ? 0 : vaccineDate.hashCode());
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
		if ((patient == null && other.patient != null)
				|| (patient != null && other.patient == null)) {
			return false;
		}
		if (patient != null && other.patient != null) {
			if ((patient.getCode() == null && other.patient.getCode() != null)
					|| (patient.getCode() != null && other.patient.getCode() == null)) {
				return false;
			}
			if (patient.getCode() != null && other.patient.getCode() != null
							&& !patient.equals(other.patient)) {
				return false;
			}
		}
		if (progr != other.progr) {
			return false;
		}
		if ((vaccine != null && !vaccine.equals(other.vaccine))
				|| (other.vaccine != null && !other.vaccine.equals(vaccine))) {
			return false;
		}
		return (vaccineDate == null || vaccineDate.equals(other.vaccineDate))
				&& (other.vaccineDate == null || other.vaccineDate.equals(vaccineDate));
	}

}
