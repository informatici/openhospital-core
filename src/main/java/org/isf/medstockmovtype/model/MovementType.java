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
package org.isf.medstockmovtype.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * MovementType - model for the movement type
 * -----------------------------------------
 * modification history
 * ? - bob - first version
 * 18/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSRSTOCKMOVTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVT_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVT_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVT_LAST_MODIFIED_DATE"))
public class MovementType extends Auditable<String> {

	@Id
	@Column(name="MMVT_ID_A")	   
    private String code;

	@NotNull
	@Column(name="MMVT_DESC")	
    private String description;

	@NotNull
	@Column(name="MMVT_TYPE")	
    private String type;

	@Transient
	private volatile int hashCode = 0;

	public MovementType() {
	}
    
    /**
     * @param code
     * @param description
     * @param type
     */
    public MovementType(String code, String description, String type) {
	    this.code = code;
	    this.description = description;
	    this.type = type;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
    public String toString() {
        return getDescription();
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof MovementType)) {
			return false;
		}
		
		MovementType movementType = (MovementType)obj;
		return (this.getCode().equals(movementType.getCode()));
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
