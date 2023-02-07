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

import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.supplier.model.Supplier;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Medical Stock Movement - model for the medical stock movement entity
 * -----------------------------------------
 * modification history
 * ? - ?
 * 17/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSRSTOCKMOV")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MMV_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MMV_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MMV_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MMV_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MMV_LAST_MODIFIED_DATE"))
public class Movement extends Auditable<String> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MMV_ID")
	private int code;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MMV_MDSR_ID")
	private Medical medical;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MMV_MMVT_ID_A")
	private MovementType type;

	@ManyToOne
	@JoinColumn(name="MMV_WRD_ID_A")
	private Ward ward;

	@ManyToOne
	@JoinColumn(name="MMV_LT_ID_A")
	private Lot lot;

	@NotNull
	@Column(name="MMV_DATE")		// SQL type: datetime
	private LocalDateTime date;

	@NotNull
	@Column(name="MMV_QTY")
	private int quantity;

	@ManyToOne(optional = true, targetEntity=Supplier.class)
	@JoinColumn(name="MMV_FROM")
	private Supplier supplier;

	@NotNull
	@Column(name="MMV_REFNO")
	private String refNo;

	@Transient
	private volatile int hashCode = 0;
	
	public Movement() { }

	public Movement(Medical aMedical, MovementType aType, Ward aWard, Lot aLot, LocalDateTime aDate, int aQuantity, Supplier aSupplier, String aRefNo) {
		medical = aMedical;
		type = aType;
		ward = aWard;
		lot = aLot;
		date = TimeTools.truncateToSeconds(aDate);
		quantity = aQuantity;
		supplier = aSupplier;
		refNo = aRefNo;
	}

	public int getCode() {
		return code;
	}

	public Medical getMedical() {
		return medical;
	}

	public MovementType getType() {
		return type;
	}

	public Ward getWard() {
		return ward;
	}

	public Lot getLot() {
		return lot;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Supplier getOrigin() {
		return supplier;
	}

	public void setWard(Ward ward) {
		this.ward = ward;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setCode(int aCode) {
		code = aCode;
	}

	public void setMedical(Medical aMedical) {
		medical = aMedical;
	}

	public void setType(MovementType aType) {
		type = aType;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String toString() {
		return MessageBundle.formatMessage("angal.movement.tostring.fmt.txt",
				medical != null ? medical.toString() : "NULL",
				type != null ? type.toString() : "NULL",
				quantity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Movement)) {
			return false;
		}
		
		Movement movement = (Movement)obj;
		return (this.getCode() == movement.getCode());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + code;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
}
