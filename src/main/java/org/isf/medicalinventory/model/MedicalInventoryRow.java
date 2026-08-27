/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.math.BigDecimal;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALDSRINVENTORYROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MINVTR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MINVTR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MINVTR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MINVTR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MINVTR_LAST_MODIFIED_DATE"))
public class MedicalInventoryRow extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MINVTR_ID")
	private Integer id;

	@Column(name = "MINVTR_THEORETIC_QTY")
	private BigDecimal theoreticQty = BigDecimal.ZERO;

	@Column(name = "MINVTR_REAL_QTY")
	private BigDecimal realQty = BigDecimal.ZERO;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MINVTR_INVT_ID")
	private MedicalInventory inventory;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MINVTR_MDSR_ID")
	private Medical medical;

	@ManyToOne
	@JoinColumn(name = "MINVTR_LT_ID")
	private Lot lot;

	/**
	 * The printed code of {@link #lot}, kept in sync with the numeric foreign key (see {@link #syncLotCode()}) for the
	 * legacy Jasper reports still joining on MINVTR_LT_ID_A, until stage 2 of OP-1427 rewrites them to use MINVTR_LT_ID.
	 */
	@Column(name = "MINVTR_LT_ID_A")
	private String lotCode;

	@Column(name = "MINVTR_IS_NEW_LOT")
	private boolean isNewLot;

	@Version
	@Column(name = "MINVTR_LOCK")
	private int lock;

	private transient BigDecimal total;

	public MedicalInventoryRow() {
	}

	public MedicalInventoryRow(Integer id, BigDecimal theoreticQty, BigDecimal realQty, MedicalInventory inventory, Medical medical, Lot lot) {
		this.id = id;
		this.theoreticQty = theoreticQty;
		this.realQty = realQty;
		this.inventory = inventory;
		this.medical = medical;
		this.lot = lot;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getTheoreticQty() {
		return theoreticQty;
	}

	public void setTheoreticQty(BigDecimal theoreticQty) {
		this.theoreticQty = theoreticQty;
	}

	public BigDecimal getRealQty() {
		return realQty;
	}

	public void setRealqty(BigDecimal realQty) {
		this.realQty = realQty;
	}

	public MedicalInventory getInventory() {
		return inventory;
	}

	public void setInventory(MedicalInventory inventory) {
		this.inventory = inventory;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
		syncLotCode();
	}

	/**
	 * Keeps the report-facing MINVTR_LT_ID_A column in sync with the lot. Also run as a lifecycle callback to cover
	 * rows whose lot is assigned through the constructor rather than {@link #setLot(Lot)}.
	 */
	@PrePersist
	@PreUpdate
	private void syncLotCode() {
		lotCode = lot == null ? null : lot.getCode();
	}

	public void setRealQty(BigDecimal realQty) {
		this.realQty = realQty;
	}

	public boolean isNewLot() {
		return isNewLot;
	}

	public void setNewLot(boolean isNewLot) {
		this.isNewLot = isNewLot;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getSearchString() {
		Medical medical = getMedical();
		StringBuilder sbNameCode = new StringBuilder();
		if (medical != null) {
			sbNameCode.append(medical.getDescription().toLowerCase())
				.append(medical.getProdCode().toLowerCase());
		}
		return sbNameCode.toString();
	}
}
