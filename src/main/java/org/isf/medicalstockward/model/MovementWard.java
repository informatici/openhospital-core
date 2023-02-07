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
package org.isf.medicalstockward.model;

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
import org.isf.medicalstock.model.Lot;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * -----------------------------------------
 * Medical Ward - model for the medical ward entity
 * -----------------------------------------
 * modification history
 * ? - mwithi
 * 17/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSRSTOCKMOVWARD")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVN_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVN_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVN_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVN_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVN_LAST_MODIFIED_DATE"))
public class MovementWard extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MMVN_ID")
	private int code;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MMVN_WRD_ID_A")
	private Ward ward;
	
	@ManyToOne
	@JoinColumn(name="MMVN_LT_ID")	
	private Lot lot;

	@NotNull
	@Column(name="MMVN_DATE")		// SQL type: datetime
	private LocalDateTime date;

	@NotNull
	@Column(name="MMVN_IS_PATIENT")
	private boolean isPatient;
	
	@ManyToOne
	@JoinColumn(name="MMVN_PAT_ID")
	private Patient patient;
	
	@Column(name="MMVN_PAT_AGE")
	private int age;
	
	@Column(name="MMVN_PAT_WEIGHT")
	private float weight;

	@NotNull
	@Column(name="MMVN_DESC")
	private String description;

	@ManyToOne
	@JoinColumn(name="MMVN_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name="MMVN_MDSR_QTY")
	private Double quantity;

	@NotNull
	@Column(name="MMVN_MDSR_UNITS")
	private String units;

	@Transient
	private volatile int hashCode = 0;
	
	@ManyToOne
	@JoinColumn(name="MMVN_WRD_ID_A_TO")	
	private Ward wardTo;
	
	@ManyToOne
	@JoinColumn(name="MMVN_WRD_ID_A_FROM")	
	private Ward wardFrom;
	
	public MovementWard() {}
	
	/**
	 * 
	 * @param ward
	 * @param date
	 * @param isPatient
	 * @param patient
	 * @param age
	 * @param weight
	 * @param description
	 * @param medical
	 * @param quantity
	 * @param units
	 */
	public MovementWard(Ward ward, LocalDateTime date, boolean isPatient, Patient patient, int age, float weight, String description, Medical medical,
			Double quantity, String units) {
		super();
		this.ward = ward;
		this.date = TimeTools.truncateToSeconds(date);
		this.isPatient = isPatient;
		this.patient = patient;
		this.age = age;
		this.weight = weight;
		this.description = description;
		this.medical = medical;
		this.quantity = quantity;
		this.units = units;
	}

	public MovementWard(Ward ward, LocalDateTime date, boolean isPatient, Patient patient, int age, float weight, String description, Medical medical,
			Double quantity, String units, Lot lot) {
		super();
		this.ward = ward;
		this.date = TimeTools.truncateToSeconds(date);
		this.isPatient = isPatient;
		this.patient = patient;
		this.age = age;
		this.weight = weight;
		this.description = description;
		this.medical = medical;
		this.quantity = quantity;
		this.units = units;
		this.lot = lot;
	}

    /**
	 * 
	 * @param ward
	 * @param date
	 * @param isPatient
	 * @param patient
	 * @param age
	 * @param weight
	 * @param description
	 * @param medical
	 * @param quantity
	 * @param units
     * @param wardTo
     * @param wardFrom
	 */
    public MovementWard(Ward ward, LocalDateTime date, boolean isPatient, Patient patient, int age, float weight, String description, Medical medical,
		    Double quantity, String units, Ward wardTo, Ward wardFrom, Lot lot) {
	    super();
	    this.ward = ward;
	    this.date = TimeTools.truncateToSeconds(date);
	    this.isPatient = isPatient;
	    this.patient = patient;
	    this.age = age;
	    this.weight = weight;
	    this.description = description;
	    this.medical = medical;
	    this.quantity = quantity;
	    this.units = units;
	    this.wardTo = wardTo;
	    this.wardFrom = wardFrom;
	    this.lot = lot;
    }

	public MovementWard(Ward ward, Lot lot, String description, Medical medical, Double quantity, String units) {
		super();
		this.ward = ward;
		this.lot = lot;
		this.description = description;
		this.medical = medical;
		this.quantity = quantity;
		this.units = units;
	}

	public int getCode() {
		return code;
	}

	public Medical getMedical() {
		return medical;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Double getQuantity() {
		return quantity;
	}

	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public Lot getLot() {
		return lot;
	}

	public void setlot(Lot lot) {
		this.lot = lot;
	}

	public boolean isPatient() {
		return isPatient;
	}

	public void setPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public void setCode(int aCode) {
		code = aCode;
	}

	public void setMedical(Medical aMedical) {
		medical = aMedical;
	}

	public Ward getWardTo() {
		return wardTo;
	}

	public void setWardTo(Ward wardTo) {
		this.wardTo = wardTo;
	}

	public Ward getWardFrom() {
		return wardFrom;
	}

	public void setWardFrom(Ward wardFrom) {
		this.wardFrom = wardFrom;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MovementWard)) {
			return false;
		}
		MovementWard movement = (MovementWard)obj;
		return (this.getCode() == movement.getCode());
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
}
