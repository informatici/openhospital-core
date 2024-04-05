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
package org.isf.priceslist.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_PRICELISTS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "LST_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "LST_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "LST_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "LST_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "LST_LAST_MODIFIED_DATE"))
public class PriceList extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="LST_ID")
	private int id;

	@NotNull
	@Column(name="LST_CODE")
    private String code;

	@NotNull
	@Column(name="LST_NAME")
    private String name;

	@NotNull
	@Column(name="LST_DESC")
    private String description;

	@NotNull
	@Column(name="LST_CURRENCY")
    private String currency;
	
	@Transient
	private volatile int hashCode;
	
	
	public PriceList() {
		super();
	}
	 
    public PriceList(int id, String code, String name, String description, String currency) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.currency = currency;
	}

	public int getId() {
		return id;
	}
	
    public void setId(int id) {
		this.id = id;
	}
	
    public String getCode() {
		return code;
	}
	
    public void setCode(String code) {
		this.code = code;
	}
	
    public String getName() {
		return name;
	}
	
    public void setName(String name) {
		this.name = name;
	}
	
    public String getDescription() {
		return description;
	}
	
    public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return name;
	}      
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PriceList)) {
			return false;
		}
		
		PriceList priceList = (PriceList)obj;
		return (id == priceList.getId());
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
