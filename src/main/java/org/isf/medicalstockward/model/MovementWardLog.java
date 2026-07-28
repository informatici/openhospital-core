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
package org.isf.medicalstockward.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Audit record of a deleted ward pharmaceutical {@link MovementWard}. It keeps a snapshot of the removed movement plus an optional reason, so that deletions
 * can be traced (OP-1388). The {@code createdBy}/{@code createdDate} audit columns record who deleted it and when.
 */
@Entity
@Table(name = "OH_MEDICALDSRSTOCKMOVWARD_LOG")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMVNL_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMVNL_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMVNL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMVNL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMVNL_LAST_MODIFIED_DATE"))
public class MovementWardLog extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MMVNL_ID")
	private int code;

	@Column(name = "MMVNL_MMVN_ID")
	private int movementWardCode;

	@Column(name = "MMVNL_WRD_ID_A")
	private String wardCode;

	@Column(name = "MMVNL_DATE")
	private LocalDateTime date;

	@Column(name = "MMVNL_IS_PATIENT")
	private boolean patient;

	@Column(name = "MMVNL_PAT_ID")
	private Integer patientCode;

	@Column(name = "MMVNL_MDSR_ID")
	private int medicalCode;

	@Column(name = "MMVNL_MDSR_QTY")
	private BigDecimal quantity;

	@Column(name = "MMVNL_MDSR_UNITS")
	private String units;

	@Column(name = "MMVNL_DESC")
	private String description;

	@Column(name = "MMVNL_LT_ID")
	private String lotCode;

	@Column(name = "MMVNL_REASON")
	private String reason;

	public MovementWardLog() {
	}

	public MovementWardLog(MovementWard movementWard, String reason) {
		this.movementWardCode = movementWard.getCode();
		this.wardCode = movementWard.getWard() != null ? movementWard.getWard().getCode() : null;
		this.date = movementWard.getDate();
		this.patient = movementWard.isPatient();
		this.patientCode = movementWard.getPatient() != null ? movementWard.getPatient().getCode() : null;
		this.medicalCode = movementWard.getMedical() != null ? movementWard.getMedical().getCode() : 0;
		this.quantity = movementWard.getQuantity();
		this.units = movementWard.getUnits();
		this.description = movementWard.getDescription();
		this.lotCode = movementWard.getLot() != null ? movementWard.getLot().getCode() : null;
		this.reason = reason;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getMovementWardCode() {
		return movementWardCode;
	}

	public void setMovementWardCode(int movementWardCode) {
		this.movementWardCode = movementWardCode;
	}

	public String getWardCode() {
		return wardCode;
	}

	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public boolean isPatient() {
		return patient;
	}

	public void setPatient(boolean patient) {
		this.patient = patient;
	}

	public Integer getPatientCode() {
		return patientCode;
	}

	public void setPatientCode(Integer patientCode) {
		this.patientCode = patientCode;
	}

	public int getMedicalCode() {
		return medicalCode;
	}

	public void setMedicalCode(int medicalCode) {
		this.medicalCode = medicalCode;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLotCode() {
		return lotCode;
	}

	public void setLotCode(String lotCode) {
		this.lotCode = lotCode;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
