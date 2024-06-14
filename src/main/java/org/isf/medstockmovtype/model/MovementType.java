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
package org.isf.medstockmovtype.model;

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
@Table(name = "OH_MEDICALDSRSTOCKMOVTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVT_LAST_MODIFIED_DATE"))
public class MovementType extends Auditable<String> {

	@Id
	@Column(name = "MMVT_ID_A")
	private String code;

	@NotNull
	@Column(name = "MMVT_DESC")
	private String description;

	@NotNull
	@Column(name = "MMVT_TYPE")
	private String type;

	@NotNull
	@Column(name = "MMVT_CATEGORY")
	private String category; // 'operational' or 'non-operational'

	@Transient
	private volatile int hashCode;

	public MovementType() {
	}

	/**
	 * @param code
	 * @param description
	 * @param type
	 * @param category
	 */
	public MovementType(String code, String description, String type, String category) {
		this.code = code;
		this.description = description;
		this.type = type;
		this.category = category;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
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

		MovementType movementType = (MovementType) obj;
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
