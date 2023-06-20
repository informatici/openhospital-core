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
package org.isf.medtype.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Medical Type - model for the medical type entity
 * Defines a medical type: D: k: S: R:
 * -----------------------------------------
 * modification history
 * 11-dec-2005 - bob - first version
 * 14-jan-2006
 * 18/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSRTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MDSRT_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MDSRT_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MDSRT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MDSRT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MDSRT_LAST_MODIFIED_DATE"))
public class MedicalType extends Auditable<String> {

	@Id
	@Column(name="MDSRT_ID_A")	
	private String code;

	@Column(name="MDSRT_DESC")	
	private String description;

	@Transient
	private volatile int hashCode = 0;
	
	public MedicalType() 
    {
		super();
    }
	
	public MedicalType(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof MedicalType && (getCode().equalsIgnoreCase(((MedicalType) anObject).getCode())
				&& getDescription().equalsIgnoreCase(((MedicalType) anObject).getDescription()));
	}

	public String toString() {
		return getDescription();
	}

	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + code.hashCode();
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
}
