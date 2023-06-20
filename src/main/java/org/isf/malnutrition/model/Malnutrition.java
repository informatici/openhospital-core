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
package org.isf.malnutrition.model;

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

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Malnutrition - malnutrition control model
 * -----------------------------------------
 * modification history
 * 11/01/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MALNUTRITIONCONTROL")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MLN_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MLN_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MLN_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MLN_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MLN_LAST_MODIFIED_DATE"))
public class Malnutrition extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MLN_ID")
	private int code;

	/*
	 * Date of this control
	 */
	@NotNull
	@Column(name="MLN_DATE_SUPP")		// SQL type: datetime
	private LocalDateTime dateSupp;

	/*
	 * Date of next control
	 */
	@Column(name="MNL_DATE_CONF")		// SQL type: datetime. NB: possible typo in column name ("MNL")
	private LocalDateTime dateConf;

	@ManyToOne
	@JoinColumn(name="MLN_ADM_ID")
	private Admission admission;

	@NotNull
	@Column(name="MLN_HEIGHT")
	private float height;

	@NotNull
	@Column(name="MLN_WEIGHT")
	private float weight;

	@Version
	@Column(name="MLN_LOCK")
	private int lock;

	@Transient
	private volatile int hashCode = 0;
	

	public Malnutrition() { }

	public Malnutrition(int aCode, LocalDateTime aDateSupp, LocalDateTime aDateConf, Admission anAdmission, float aHeight, float aWeight) {
		code = aCode;
		dateSupp = TimeTools.truncateToSeconds(aDateSupp);
		dateConf = TimeTools.truncateToSeconds(aDateConf);
		admission = anAdmission;
		height = aHeight;
		weight = aWeight;
	}

	public Malnutrition(int aCode, LocalDateTime aDateSupp, LocalDateTime aDateConf, Admission anAdmission, Patient aPatient, float aHeight, float aWeight) {
		code = aCode;
		dateSupp = TimeTools.truncateToSeconds(aDateSupp);
		dateConf = TimeTools.truncateToSeconds(aDateConf);
		admission = anAdmission;
		height = aHeight;
		weight = aWeight;
	}

	public void setCode(int aCode) {
		code = aCode;
	}

	public int getCode() {
		return code;
	}

	public void setLock(int aLock) {
		lock = aLock;
	}

	public int getLock() {
		return lock;
	}
	
	public Admission getAdmission() {
		return admission;
	}

	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	public void setDateSupp(LocalDateTime aDateSupp) {
		dateSupp = TimeTools.truncateToSeconds(aDateSupp);
	}

	public void setDateConf(LocalDateTime aDateConf) {
		dateConf = TimeTools.truncateToSeconds(aDateConf);
	}
	
	public void setHeight(float aHeight) {
		height = aHeight;
	}

	public void setWeight(float aWeight) {
		weight = aWeight;
	}

	public LocalDateTime getDateSupp() {
		return dateSupp;
	}

	public LocalDateTime getDateConf() {
		return dateConf;
	}

	public float getHeight() {
		return height;
	}

	public float getWeight() {
		return weight;
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if ((!(other instanceof Malnutrition))) {
			return false;
		}
		if ((getDateConf() == null) && (((Malnutrition) other).getDateConf() == null)) {
			result = true;
		}
		if ((getDateSupp() == null) && (((Malnutrition) other).getDateSupp() == null)) {
			result = true;
		}
		if (!result) {
			if ((getDateConf() == null) || (((Malnutrition) other).getDateConf() == null)) {
				return false;
			}
			if ((getDateSupp() == null) || (((Malnutrition) other).getDateSupp() == null)) {
				return false;
			}
			if ((getDateConf().equals(((Malnutrition) other).getDateConf())) && (getDateSupp().equals(((Malnutrition) other).getDateSupp()))) {
				result = true;
			}
		}
		if (result) {
			return (getAdmission() == (((Malnutrition) other).getAdmission())
					&& getHeight() == (((Malnutrition) other).getHeight())
					&& getWeight() == (((Malnutrition) other).getWeight()));
			}
		else {
			return false;
		}
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
