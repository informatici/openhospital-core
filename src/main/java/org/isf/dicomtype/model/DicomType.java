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
package org.isf.dicomtype.model;

import java.io.Serializable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author Mwithi
 */
@Entity
@Table(name="OH_DICOMTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "DCMT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "DCMT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "DCMT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "DCMT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "DCMT_LAST_MODIFIED_DATE"))
public class DicomType extends Auditable<String> implements Serializable {
	
	@Id 
	@Column(name = "DCMT_ID")
	private String dicomTypeID;
	
	@NotNull
	@Column(name = "DCMT_DESC")
	private String dicomTypeDescription;

	public DicomType(String dicomTypeID, String dicomTypeDescription) {
		super();
		this.dicomTypeID = dicomTypeID;
		this.dicomTypeDescription = dicomTypeDescription;
	}

	public DicomType() {}

	public String getDicomTypeID() {
		return dicomTypeID;
	}

	public void setDicomTypeID(String dicomTypeID) {
		this.dicomTypeID = dicomTypeID;
	}

	public String getDicomTypeDescription() {
		return dicomTypeDescription;
	}

	public void setDicomTypeDescription(String dicomTypeDescription) {
		this.dicomTypeDescription = dicomTypeDescription;
	}

	@Override
	public String toString() {
		return this.dicomTypeDescription;
	}

}
