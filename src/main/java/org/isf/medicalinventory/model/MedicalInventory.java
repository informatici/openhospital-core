/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalinventory.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALDSRINVENTORY")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MINVT_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MINVT_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MINVT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MINVT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MINVT_LAST_MODIFIED_DATE"))
public class MedicalInventory extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MINVT_ID")
	private Integer id;

	@NotNull
	@Column(name = "MINVT_STATUS")
	private String status;

	@NotNull
	@Column(name = "MINVT_DATE")
	private LocalDateTime inventoryDate;

	@NotNull
	@Column(name = "MINVT_US_ID_A")
	private String user;

	@NotNull
	@Column(name = "MINVT_REFERENCE")
	private String inventoryReference;

	@NotNull
	@Column(name = "MINVT_TYPE")
	private String inventoryType;

	@Column(name = "MINVT_WRD_ID_A")
	private String ward;

	@Version
	@Column(name="MINVT_LOCK")
	private int lock;
	
	public MedicalInventory() {
		super();
	}

	public MedicalInventory(Integer id, String status, LocalDateTime inventoryDate, String user, String reference, String type, String ward) {
		this.id = id;
		this.status = status;
		this.inventoryDate = inventoryDate;
		this.user = user;
		this.inventoryReference = reference;
		this.inventoryType = type;
		this.ward = ward;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getInventoryDate() {
		return inventoryDate;
	}

	public void setInventoryDate(LocalDateTime inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getInventoryReference() {
		return inventoryReference;
	}

	public void setInventoryReference(String inventoryReference) {
		this.inventoryReference = inventoryReference;
	}

	public String getInventoryType() {
		return inventoryType;
	}

	public void setInventoryType(String inventoryType) {
		this.inventoryType = inventoryType;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}
	
	public int getLock() {
		return lock;
	}
	
	public void setLock(int lock) {
		this.lock = lock;
	}
}
