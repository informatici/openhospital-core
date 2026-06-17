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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Audit record of a deleted pharmaceutical stock {@link Movement}. It keeps a snapshot of the removed movement plus an
 * optional reason, so that deletions can be traced (OP-1388). The {@code createdBy}/{@code createdDate} audit columns
 * record who deleted it and when.
 */
@Entity
@Table(name = "OH_MEDICALDSRSTOCKMOV_LOG")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVL_LAST_MODIFIED_DATE"))
public class MovementLog extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MMVL_ID")
	private int code;

	@Column(name = "MMVL_MMV_ID")
	private int movementCode;

	@Column(name = "MMVL_MDSR_ID")
	private int medicalCode;

	@Column(name = "MMVL_MMVT_ID_A")
	private String movementType;

	@Column(name = "MMVL_WRD_ID_A")
	private String wardCode;

	@Column(name = "MMVL_LT_ID_A")
	private String lotCode;

	@Column(name = "MMVL_DATE")
	private LocalDateTime date;

	@Column(name = "MMVL_QTY")
	private int quantity;

	@Column(name = "MMVL_REFNO")
	private String refNo;

	@Column(name = "MMVL_REASON")
	private String reason;

	public MovementLog() {
	}

	public MovementLog(Movement movement, String reason) {
		this.movementCode = movement.getCode();
		this.medicalCode = movement.getMedical() != null ? movement.getMedical().getCode() : 0;
		this.movementType = movement.getType() != null ? movement.getType().getCode() : null;
		this.wardCode = movement.getWard() != null ? movement.getWard().getCode() : null;
		this.lotCode = movement.getLot() != null ? movement.getLot().getCode() : null;
		this.date = movement.getDate();
		this.quantity = movement.getQuantity();
		this.refNo = movement.getRefNo();
		this.reason = reason;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getMovementCode() {
		return movementCode;
	}

	public void setMovementCode(int movementCode) {
		this.movementCode = movementCode;
	}

	public int getMedicalCode() {
		return medicalCode;
	}

	public void setMedicalCode(int medicalCode) {
		this.medicalCode = medicalCode;
	}

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public String getWardCode() {
		return wardCode;
	}

	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}

	public String getLotCode() {
		return lotCode;
	}

	public void setLotCode(String lotCode) {
		this.lotCode = lotCode;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
