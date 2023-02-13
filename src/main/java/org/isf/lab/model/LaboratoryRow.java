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
package org.isf.lab.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_LABORATORYROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "LABR_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "LABR_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "LABR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "LABR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "LABR_LAST_MODIFIED_DATE"))
public class LaboratoryRow extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="LABR_ID")
	private Integer code;

	@NotNull
	@ManyToOne
	@JoinColumn(name="LABR_LAB_ID")
	private Laboratory laboratory;

	@NotNull
	@Column(name="LABR_DESC")
	private String description;

	@Transient
	private volatile int hashCode = 0;

	public LaboratoryRow() {
	}

	public LaboratoryRow(Laboratory aLabId, String aDescription) {
		laboratory = aLabId;
		description = aDescription;
	}

	public LaboratoryRow(Integer aCode, Laboratory aLabId, String aDescription) {
		code = aCode;
		laboratory = aLabId;
		description = aDescription;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Laboratory getLabId() {
		return laboratory;
	}
	public void setLabId(Laboratory laboratory) {
		this.laboratory = laboratory;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof LaboratoryRow)) {
			return false;
		}
		
		LaboratoryRow laboratoryRow = (LaboratoryRow)obj;
		if (this.getCode() != null && laboratoryRow.getCode() != null) {
			return (this.getCode().equals(laboratoryRow.getCode()) );
		}
		return (this.getDescription().equals(laboratoryRow.getDescription()));
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + (code == null ? 0 : code);
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
}
