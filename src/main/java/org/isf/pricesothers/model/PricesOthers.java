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
package org.isf.pricesothers.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * PricesOthers : represents an other entry for prices
 * -----------------------------------------
 * modification history
 * ? - Alex - first version
 * 1/08/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_PRICESOTHERS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "OTH_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "OTH_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "OTH_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "OTH_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "OTH_LAST_MODIFIED_DATE"))
public class PricesOthers extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="OTH_ID")
    private int id;

	@NotNull
	@Column(name="OTH_CODE")  
    private String code;

	@NotNull
	@Column(name="OTH_DESC") 
    private String description;

	@NotNull
	@Column(name="OTH_OPD_INCLUDE") 
	private boolean opdInclude;

	@NotNull
	@Column(name="OTH_IPD_INCLUDE") 
	private boolean ipdInclude;

	@NotNull
	@Column(name="OTH_DAILY") 
	private boolean daily;
	
	@Column(name="OTH_DISCHARGE") 
	private boolean discharge;
	
	@Column(name="OTH_UNDEFINED") 
	private boolean undefined;
    
	@Transient
	private volatile int hashCode = 0;
	
	public PricesOthers() {
		super();
	}

	public PricesOthers(String code, String desc, boolean opdInclude, boolean ipdInclude, boolean discharge, boolean undefined) {
		super();
		this.code = code;
		this.description = desc;
		this.opdInclude = opdInclude;
		this.ipdInclude = ipdInclude;
		this.discharge = discharge;
		this.undefined = undefined;
	}

	public PricesOthers(int id, String code, String desc, boolean opdInclude, boolean ipdInclude, boolean discharge, boolean undefined) {
		super();
		this.id = id;
		this.code = code;
		this.description = desc;
		this.opdInclude = opdInclude;
		this.ipdInclude = ipdInclude;
		this.discharge = discharge;
		this.undefined = undefined;
	}

	public PricesOthers(int id, String code, String description, boolean opdInclude, boolean ipdInclude, boolean daily, boolean discharge, boolean undefined) {
		super();
		this.id = id;
		this.code = code;
		this.description = description;
		this.opdInclude = opdInclude;
		this.ipdInclude = ipdInclude;
		this.daily = daily;
		this.discharge = discharge;
		this.undefined = undefined;
	}

	public int getId() {
		return id;
	}
    
	public void setId(int id) {
		this.id = id;
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
	
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	public boolean isOpdInclude() {
		return opdInclude;
	}
	
	public void setOpdInclude(boolean opdInclude) {
		this.opdInclude = opdInclude;
	}
	
	public boolean isIpdInclude() {
		return ipdInclude;
	}
	
	public void setIpdInclude(boolean ipdInclude) {
		this.ipdInclude = ipdInclude;
	}

	public boolean isDaily() {
		return daily;
	}

	public void setDaily(boolean daily) {
		this.daily = daily;
	}

	public boolean isDischarge() {
		return discharge;
	}

	public void setDischarge(boolean discharge) {
		this.discharge = discharge;
	}

	public boolean isUndefined() {
		return undefined;
	}

	public void setUndefined(boolean undefined) {
		this.undefined = undefined;
	}

	@Override
	public String toString() {
		return this.description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PricesOthers)) {
			return false;
		}
		
		PricesOthers priceOther = (PricesOthers)obj;
		return (id == priceOther.getId());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + id;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}

}
