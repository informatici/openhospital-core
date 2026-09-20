/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalstock.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.Auditable;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Persisted wizard state of a not-yet-confirmed charge/discharge stock movement.
 * A draft never touches stock quantities: the final approval happens through the
 * standard movement insertion path, after which the draft is deleted.
 */
@Entity
@Table(name = "OH_MEDICALDSRSTOCKMOVDRAFT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVD_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVD_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVD_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVD_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVD_LAST_MODIFIED_DATE"))
public class MovementDraft extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MMVD_ID")
	private Integer id;

	@NotNull
	@Column(name = "MMVD_KIND")
	private String kind;

	@ManyToOne
	@JoinColumn(name = "MMVD_MMVT_ID_A")
	private MovementType type;

	@Column(name = "MMVD_DATE")
	private LocalDateTime date;

	@Column(name = "MMVD_REFNO")
	private String refNo;

	@ManyToOne
	@JoinColumn(name = "MMVD_SUP_ID")
	private Supplier supplier;

	@ManyToOne
	@JoinColumn(name = "MMVD_WRD_ID_A")
	private Ward ward;

	@Version
	@Column(name = "MMVD_LOCK")
	private int lock;

	public MovementDraft() {
		super();
	}

	public MovementDraft(Integer id, String kind, MovementType type, LocalDateTime date, String refNo, Supplier supplier, Ward ward) {
		this.id = id;
		this.kind = kind;
		this.type = type;
		this.date = date;
		this.refNo = refNo;
		this.supplier = supplier;
		this.ward = ward;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public MovementType getType() {
		return type;
	}

	public void setType(MovementType type) {
		this.type = type;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Ward getWard() {
		return ward;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
}
