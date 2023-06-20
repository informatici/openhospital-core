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
package org.isf.agetype.model;

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
 * AgeType - model for the age type entity; represent age's ranges
 * -----------------------------------------
 * modification history
 * ? - bob - first version
 * 17/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_AGETYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "AT_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "AT_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "AT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "AT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "AT_LAST_MODIFIED_DATE"))
public class AgeType extends Auditable<String> {

	@Id 
	@Column(name="AT_CODE") 
    private String code;

	@NotNull
	@Column(name="AT_DESC")	
    private String description;

	@NotNull
	@Column(name="AT_FROM")	
    private int from;

	@NotNull
	@Column(name="AT_TO")
    private int to;

	@Transient
	private volatile int hashCode = 0;
	
	public AgeType() 
    {
		super();
    }
    
    /**
     * @param aCode
     * @param aDescription
     */
    public AgeType(String aCode, String aDescription) {
	    super();
	    this.code = aCode;
	    this.description = aDescription;
    }

	public AgeType(String aCode, int from, int to, String aDescription) {
		super();
		this.code = aCode;
		this.from = from;
		this.to = to;
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
    public void setFrom(int from) {
		this.from = from;
	}
	public int getFrom() {
		return from;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public int getTo() {
		return to;
	}
	public String toString() {
        return getDescription();
    }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof AgeType)) {
			return false;
		}
		
		AgeType ageType = (AgeType)obj;
		return (this.getCode().equals(ageType.getCode()));
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
