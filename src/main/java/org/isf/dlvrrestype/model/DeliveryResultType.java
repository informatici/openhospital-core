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
package org.isf.dlvrrestype.model;

import java.io.Serializable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_DELIVERYRESULTTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "DRT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "DRT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "DRT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "DRT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "DRT_LAST_MODIFIED_DATE"))
public class DeliveryResultType extends Auditable<String> implements Serializable {

	@Id 
	@Column(name="DRT_ID_A")	    
    private String code;

	@NotNull
	@Column(name="DRT_DESC")
    private String description;
	
	@Transient
	private volatile int hashCode;
	
	public DeliveryResultType() 
    {
		super();
    }
	
    /**
     * @param aCode
     * @param aDescription
     */
    public DeliveryResultType(String aCode, String aDescription) {
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
        return anObject instanceof DeliveryResultType && (getCode().equals(((DeliveryResultType) anObject).getCode())
		        && getDescription().equalsIgnoreCase(((DeliveryResultType) anObject).getDescription()));
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
