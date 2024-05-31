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
package org.isf.medicalstock.model;

import java.time.LocalDate;

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
import jakarta.validation.constraints.NotNull;

import org.isf.medicals.model.Medical;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALDSRSTOCK")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MS_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MS_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MS_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "MS_ACTIVE"))
public class MedicalStock extends Auditable<String> {

	public MedicalStock() {
	}

	public MedicalStock(@NotNull Medical medical, @NotNull LocalDate balanceDate, @NotNull int balance, LocalDate nextMovDate, Integer days) {
		this.medical = medical;
		this.balanceDate = balanceDate;
		this.balance = balance;
		this.nextMovDate = nextMovDate;
		this.days = days;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MS_ID")
	private int code;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MS_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name = "MS_DATE_BALANCE")
	private LocalDate balanceDate;

	@NotNull
	@Column(name = "MS_BALANCE")
	private int balance;

	@Column(name = "MS_DATE_NEXT_MOV")
	private LocalDate nextMovDate;

	@Column(name = "MS_DAYS")
	private Integer days;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public LocalDate getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(LocalDate balanceDate) {
		this.balanceDate = balanceDate;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public LocalDate getNextMovDate() {
		return nextMovDate;
	}

	public void setNextMovDate(LocalDate nextMovDate) {
		this.nextMovDate = nextMovDate;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

}
