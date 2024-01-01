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
package org.isf.disease.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.isf.distype.model.DiseaseType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_DISEASE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "DIS_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "DIS_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "DIS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "DIS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "DIS_LAST_MODIFIED_DATE"))
public class Disease extends Auditable<String> {

	@Id
	@Column(name="DIS_ID_A")	    
    private String code;

	@NotNull
	@Column(name="DIS_DESC")
    private String description;

	@NotNull
	@ManyToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name="DIS_DCL_ID_A")
	private DiseaseType diseaseType; 

	@Version
	@Column(name="DIS_LOCK")
	private Integer lock;

	@NotNull
	@Column(name="DIS_OPD_INCLUDE")
	private boolean opdInclude;

	@NotNull
	@Column(name="DIS_IPD_IN_INCLUDE")
	private boolean ipdInInclude;

	@NotNull
	@Column(name="DIS_IPD_OUT_INCLUDE")
	private boolean ipdOutInclude;

	@Transient
	private volatile int hashCode;

	public Disease() 
    {
		super();
    }
	
    /**
     * @param aCode
     * @param aDescription
     * @param aType
     */
    public Disease(String aCode, String aDescription, DiseaseType aType) {
        super();
        this.code = aCode;
        this.description = aDescription;
        this.diseaseType = aType;
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
    
    public Integer getLock() {
        return this.lock;
    }    
    
    public void setLock(Integer aLock) {
        this.lock = aLock;
    }
    
    public DiseaseType getType() {
        return this.diseaseType;
    }
    
    public void setType(DiseaseType aType) {
        this.diseaseType = aType;
    }
    
    public boolean  getOpdInclude() {
        return this.opdInclude;
    }
    
    public void setOpdInclude(boolean opdInclude) {
        this.opdInclude = opdInclude;
    }
    
    public boolean getIpdInInclude() {
        return this.ipdInInclude;
    }
    
    public void setIpdInInclude(boolean ipdInclude) {
        this.ipdInInclude = ipdInclude;
    }
    
    public boolean getIpdOutInclude() {
		return ipdOutInclude;
	}
    
    public void setIpdOutInclude(boolean ipdOutInclude) {
		this.ipdOutInclude = ipdOutInclude;
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof Disease && (getCode().equals(((Disease) anObject).getCode())
				&& getDescription().equalsIgnoreCase(((Disease) anObject).getDescription()) && getType().equals(((Disease) anObject).getType()));
	}

    @Override
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
