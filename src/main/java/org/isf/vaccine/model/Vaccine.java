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
package org.isf.vaccine.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.isf.vactype.model.VaccineType;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Pure Model Vaccine (Hospital vaccines): represents a vaccine
 *
 * @author Eva
 *
 * modification history
 * 20/10/2011 - Cla - insert vaccinetype managment
 * 18/11/2011 - Cla - inserted print method
 * 04/06/2015 - Antonio - ported to JPA
 */
@Entity
@Table(name="OH_VACCINE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "VAC_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "VAC_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "VAC_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "VAC_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "VAC_LAST_MODIFIED_DATE"))
public class Vaccine extends Auditable<String> {

	@Id
	@Column(name="VAC_ID_A")
    private String code;

	@NotNull
	@Column(name="VAC_DESC")
    private String description;

	@NotNull
	@ManyToOne
	@JoinColumn(name="VAC_VACT_ID_A")
    private VaccineType vaccineType;

	@Version
	@Column(name="VAC_LOCK")
    private Integer lock;

	 public Vaccine() {
	        super();
	    }
    /**
     * @param aCode
     * @param aDescription
     * @param aVaccineType
     */
    public Vaccine(String aCode, String aDescription, VaccineType aVaccineType) {
        super();
        this.code = aCode;
        this.description = aDescription;
        this.vaccineType = aVaccineType; 
    }

	public String getCode() {
        return this.code;
    }

    public void setCode(String aCode) {
        this.code = aCode;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String aDescription) {
        this.description = aDescription;
    }

    public VaccineType getVaccineType() {
        return this.vaccineType ;
    }

    public void setVaccineType(VaccineType aVaccineType) {
        this.vaccineType = aVaccineType;
    }
    
    public Integer getLock() {
        return this.lock;
    }

    public void setLock(Integer aLock) {
        this.lock = aLock;
    }

	public boolean equals(Object anObject) {
		return anObject instanceof Vaccine
				&& (getCode().equals(((Vaccine) anObject).getCode())
				&& getDescription().equalsIgnoreCase(((Vaccine) anObject).getDescription())
				&& getVaccineType().equals(((Vaccine) anObject).getVaccineType()));
	}

	public String print() {
		return "Vaccine code =." + getCode() + ". description =." + getDescription() + ".";
	}

	public String toString() {
		return getDescription();
	}

}
