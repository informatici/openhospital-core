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
package org.isf.operation.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.opetype.model.OperationType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Operation - model for the bill entity
 * -----------------------------------------
 * modification history
 * ? - bob - first version
 * 13/02/09 - Alex - added Major/Minor control
 * 007/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_OPERATION")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OPE_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "OPE_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OPE_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OPE_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OPE_LAST_MODIFIED_DATE"))
public class Operation extends Auditable<String> {

	@Id
	@Column(name="OPE_ID_A")	    
    private String code;

	@NotNull
	@Column(name="OPE_DESC")
    private String description;

	@NotNull
	@ManyToOne
	@JoinColumn(name="OPE_OCL_ID_A")
    private OperationType type;

	@NotNull
	@Column(name="OPE_STAT")
    private Integer major;

	/*
	 * //TODO: replace "integers" values with mnemonic ones, CHAR(1) -> VARCHAR(10)
	 * "1" = OPD / ADMISSION
	 * "2" = ADMISSION
	 * "3" = OPD
	 */
	@Column(name="OPE_FOR")
    private String operFor;
	
	
	@Version
	@Column(name="OPE_LOCK")
    private Integer lock;

	@Transient
    private volatile int hashCode = 0;
    
	public Operation() {
		super();
	}

	/**
	 * @param aCode
	 * @param aDescription
	 * @param aType
	 */
	public Operation(String aCode, String aDescription, OperationType aType, Integer major) {
		super();
		this.code = aCode;
		this.description = aDescription;
		this.type = aType;
		this.major = major;
	}
    
    public String getCode() {
        return this.code;
    }
    public void setCode(String aCode) {
        this.code = aCode;
    }
    public void setOpeFor(String operFor) {
        this.operFor = operFor;
    }
    public String getOpeFor() {
        return this.operFor;
    }
    
  
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String aDescription) {
        this.description = aDescription;
    }
    
    public void setMajor(Integer major) {
		this.major = major;
	}
    
	public Integer getMajor() {
		return major;
	}
	
	public Integer getLock() {
        return this.lock;
    }
	
    public void setLock(Integer aLock) {
        this.lock = aLock;
    }
    
    public OperationType getType() {
        return this.type;
    }
    
    public void setType(OperationType aType) {
        this.type = aType;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
			return true;
		}
		
		if (!(anObject instanceof Operation)) {
			return false;
		}
		
		Operation operation = (Operation)anObject;
		return (this.getCode().equals(operation.getCode()) &&
				this.getDescription().equalsIgnoreCase(operation.getDescription()) &&
				this.getType().equals(operation.getType()) &&
				this.getMajor().equals(operation.getMajor()));
    }
    
    @Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + ((code == null) ? 0 : code.hashCode());
	        c = m * c + ((description == null) ? 0 : description.hashCode());
	        c = m * c + ((type == null) ? 0 : type.hashCode());
	        c = m * c + ((major == null) ? 0 : major);
	        c = m * c + ((lock == null) ? 0 : lock);
	        
	        this.hashCode = c;
	    }
	    return this.hashCode;
	}

    public String toString() {
        return this.description;
    }
}
