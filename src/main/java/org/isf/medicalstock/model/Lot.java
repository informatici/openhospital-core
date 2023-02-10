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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medicalstockward.service.MedicalStockWardIoOperations;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Medical Lot - model for the medical lot entity
 * -----------------------------------------
 * modification history
 * ? - ?
 * 17/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSRLOT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "LT_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "LT_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "LT_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "LT_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "LT_LAST_MODIFIED_DATE"))
public class Lot extends Auditable<String> {

	@Id
	@Column(name="LT_ID_A")
	private String code;

	@NotNull
	@ManyToOne
	@JoinColumn(name="LT_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name="LT_PREP_DATE")		// SQL type: datetime
	private LocalDateTime preparationDate;

	@NotNull
	@Column(name="LT_DUE_DATE")		   // SQL type: datetime
	private LocalDateTime dueDate;

	@Column(name="LT_COST")
	private BigDecimal cost;

	/**
	 * Automatic calculated field for a lot's quantity stocked in the main store, 
	 * taking in account only the main MedicalStock movements (charges and discharges).<br>
	 * 
	 * <i>
	 * NB: COALESCE is needed for legacy connection to lots migrated from a version prior v1.11.0;
	 * in theory, there should not exist lots without an initial charge movement
	 * in the main MedicalStock, but sometimes it could happen if 
	 * {@link MedicalStockWardIoOperations} are enabled (with {@link GeneralData}<code>.INTERNALPHARMACIES=true</code>) 
	 * and some lots are registered directly there at the time of the first inventory.
	 * 
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_admin/AdminManual.adoc#5-1-19-internalpharmacies">Admin Manual</a>
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_user/UserManual.adoc#63-pharmaceuticals-stock-ward-pharmaceuticals-stock-ward">User Manual</a>
	 * </i>
	 */
	@Transient
	private int mainStoreQuantity;
	
	/**
	 * Automatic calculated field for a lot's quantity stocked in all wards, 
	 * taking in account only the wards movements (inventories, discharges and transfers).<br>
	 * 
	 * <i>
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_admin/AdminManual.adoc#5-1-19-internalpharmacies">Admin Manual</a>
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_user/UserManual.adoc#63-pharmaceuticals-stock-ward-pharmaceuticals-stock-ward">User Manual</a>
	 * </i>
	 */
	@Transient
	private double wardsTotalQuantity;
	
	/**
	 * Automatic calculated field for a overall lot's quantity (MedicalStock + MedicalStockWards).<br>
	 * 
	 * <i>
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_admin/AdminManual.adoc#5-1-19-internalpharmacies">Admin Manual</a>
	 * @see <a href="https://github.com/informatici/openhospital-doc/blob/develop/doc_user/UserManual.adoc#63-pharmaceuticals-stock-ward-pharmaceuticals-stock-ward">User Manual</a>
	 * </i>
	 */
	@Transient
	private double overallQuantity;

	@Transient
	private volatile int hashCode = 0;

	public Lot() {
	}

	public Lot(String aCode) {
		code = aCode;
	}

	public Lot(String aCode, LocalDateTime aPreparationDate, LocalDateTime aDueDate) {
		code = aCode;
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
		dueDate = TimeTools.truncateToSeconds(aDueDate);
	}

	public Lot(Medical aMedical, String aCode, LocalDateTime aPreparationDate, LocalDateTime aDueDate, BigDecimal aCost) {
		medical = aMedical;
		code = aCode;
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
		dueDate = TimeTools.truncateToSeconds(aDueDate);
		cost = aCost;
	}

	public String getCode() {
		return code;
	}

	public Integer getMainStoreQuantity() {
		return mainStoreQuantity;
	}

	public Double getWardsTotalQuantity() {
		return wardsTotalQuantity;
	}

	public double getOverallQuantity() {
		return mainStoreQuantity + wardsTotalQuantity;
	}

	public Medical getMedical() {
		return medical;
	}

	public LocalDateTime getPreparationDate() {
		return preparationDate;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCode(String aCode) {
		code = aCode;
	}

	public void setMainStoreQuantity(int aQuantity) {
		mainStoreQuantity = aQuantity;
	}

	public void setWardsTotalQuantity(double wardsTotalQuantity) {
		this.wardsTotalQuantity = wardsTotalQuantity;
	}

	public void setPreparationDate(LocalDateTime aPreparationDate) {
		preparationDate = TimeTools.truncateToSeconds(aPreparationDate);
	}

	public void setMedical(Medical aMedical) {
		medical = aMedical;
	}

	public void setDueDate(LocalDateTime aDueDate) {
		dueDate = TimeTools.truncateToSeconds(aDueDate);
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String toString() {
		if (code == null) {
			return MessageBundle.getMessage("angal.medicalstock.nolot.txt");
		}
		return getCode();
	}

	public boolean isValidLot() {
		return getCode().length() <= 50;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Lot other = (Lot) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (cost != null) {
			if (other.cost != null && cost.compareTo(other.cost) != 0) {
				return false;
			}
		}
		if (dueDate == null) {
			if (other.dueDate != null) {
				return false;
			}
		} else if (!dueDate.equals(other.dueDate)) {
			return false;
		}
		if (preparationDate == null) {
			if (other.preparationDate != null) {
				return false;
			}
		} else if (!preparationDate.equals(other.preparationDate)) {
			return false;
		}
		return mainStoreQuantity == other.mainStoreQuantity;
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
