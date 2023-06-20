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
package org.isf.opetype.model;

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
 * OperationType - model for the operation type
 * -----------------------------------------
 * modification history
 * ? - bob - first version
 * 07/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_OPERATIONTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OCL_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "OCL_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OCL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OCL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OCL_LAST_MODIFIED_DATE"))
public class OperationType extends Auditable<String> {

	@Id
	@Column(name="OCL_ID_A")
    private String code;

	@NotNull
	@Column(name="OCL_DESC")
    private String description;
    
	@Transient
    private volatile int hashCode = 0;
    

	public OperationType() 
    {
		super();
    }
	
    /**
     * @param aCode
     * @param aDescription
     */
    public OperationType(String aCode, String aDescription) {
        super();
        this.code = aCode;
        this.description = aDescription;
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
    
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
			return true;
		}
		
		if (!(anObject instanceof OperationType)) {
			return false;
		}
		
		OperationType operationType = (OperationType)anObject;
		return (this.getCode().equals(operationType.getCode()) &&
				this.getDescription().equalsIgnoreCase(operationType.getDescription()));
    }
    
    @Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + ((code == null) ? 0 : code.hashCode());
	        c = m * c + ((description == null) ? 0 : description.hashCode());
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}

    public String toString() {
        return this.description;
    }

}
