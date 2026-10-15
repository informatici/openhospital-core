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

import java.math.BigDecimal;
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

import org.isf.medicals.model.Medical;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A single row of a {@link MovementDraft}: the wizard grid row exactly as typed.
 * The lot data is stored denormalized (code, dates, cost) because a draft row may
 * reference a lot that does not exist yet, or hold incomplete lot information.
 */
@Entity
@Table(name = "OH_MEDICALDSRSTOCKMOVDRAFTROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVDR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVDR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVDR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVDR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVDR_LAST_MODIFIED_DATE"))
public class MovementDraftRow extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MMVDR_ID")
	private Integer id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MMVDR_MMVD_ID")
	private MovementDraft draft;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MMVDR_MDSR_ID")
	private Medical medical;

	@Column(name = "MMVDR_QTY")
	private int quantity;

	@Column(name = "MMVDR_UNITS_OR_PACKETS")
	private int unitsOrPackets;

	@Column(name = "MMVDR_LT_ID_A")
	private String lotCode;

	@Column(name = "MMVDR_LT_PREP_DATE")
	private LocalDateTime lotPreparationDate;

	@Column(name = "MMVDR_LT_DUE_DATE")
	private LocalDateTime lotDueDate;

	@Column(name = "MMVDR_LT_COST")
	private BigDecimal lotCost;

	@Column(name = "MMVDR_IS_NEW_LOT")
	private boolean newLot;

	@Column(name = "MMVDR_UPDATE_LOT_COST")
	private boolean updateLotCost;

	@Version
	@Column(name = "MMVDR_LOCK")
	private int lock;

	public MovementDraftRow() {
		super();
	}

	public MovementDraftRow(Integer id, MovementDraft draft, Medical medical, int quantity, int unitsOrPackets, String lotCode,
		LocalDateTime lotPreparationDate, LocalDateTime lotDueDate, BigDecimal lotCost, boolean newLot, boolean updateLotCost) {
		this.id = id;
		this.draft = draft;
		this.medical = medical;
		this.quantity = quantity;
		this.unitsOrPackets = unitsOrPackets;
		this.lotCode = lotCode;
		this.lotPreparationDate = lotPreparationDate;
		this.lotDueDate = lotDueDate;
		this.lotCost = lotCost;
		this.newLot = newLot;
		this.updateLotCost = updateLotCost;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MovementDraft getDraft() {
		return draft;
	}

	public void setDraft(MovementDraft draft) {
		this.draft = draft;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getUnitsOrPackets() {
		return unitsOrPackets;
	}

	public void setUnitsOrPackets(int unitsOrPackets) {
		this.unitsOrPackets = unitsOrPackets;
	}

	public String getLotCode() {
		return lotCode;
	}

	public void setLotCode(String lotCode) {
		this.lotCode = lotCode;
	}

	public LocalDateTime getLotPreparationDate() {
		return lotPreparationDate;
	}

	public void setLotPreparationDate(LocalDateTime lotPreparationDate) {
		this.lotPreparationDate = lotPreparationDate;
	}

	public LocalDateTime getLotDueDate() {
		return lotDueDate;
	}

	public void setLotDueDate(LocalDateTime lotDueDate) {
		this.lotDueDate = lotDueDate;
	}

	public BigDecimal getLotCost() {
		return lotCost;
	}

	public void setLotCost(BigDecimal lotCost) {
		this.lotCost = lotCost;
	}

	public boolean isNewLot() {
		return newLot;
	}

	public void setNewLot(boolean newLot) {
		this.newLot = newLot;
	}

	public boolean isUpdateLotCost() {
		return updateLotCost;
	}

	public void setUpdateLotCost(boolean updateLotCost) {
		this.updateLotCost = updateLotCost;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}
}
