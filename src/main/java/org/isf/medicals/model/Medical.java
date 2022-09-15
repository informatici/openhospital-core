/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicals.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.medtype.model.MedicalType;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * MedicalDSR - model for the Medical DSR (Drugs Surgery Rest): represents a medical
 * -----------------------------------------
 * modification history
 * 11-dec-2005 - bob - first version
 * 14-jan-2006
 * ? - modified by alex
 * 			- product code
 * 			- pieces per packet
 * 13/01/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_MEDICALDSR")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MDSR_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "MDSR_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MDSR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MDSR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MDSR_LAST_MODIFIED_DATE"))
public class Medical extends Auditable<String> implements Comparable<Medical>, Cloneable {
	/**
	 * Code of the medical
	 */
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MDSR_ID")
	private Integer code;

	/**
	 * Code of the product
	 */
	@NotNull
	@Column(name="MDSR_CODE")	
	private String prod_code;

	/**
	 * Type of the medical
	 */
	@NotNull
	@ManyToOne
	@JoinColumn(name="MDSR_MDSRT_ID_A")
	private MedicalType type;

	/**
	 * Description of the medical
	 */
	@NotNull
	@Column(name="MDSR_DESC")
	private String description;

	/**
	 * Initial quantity
	 */
	@NotNull
	@Column(name="MDSR_INI_STOCK_QTI")
	private double initialqty;
	
	/**
	 * Pieces per packet
	 */
	@NotNull
	@Column(name="MDSR_PCS_X_PCK")
	private Integer pcsperpck;

	/**
	 * Input quantity
	 */
	@NotNull
	@Column(name="MDSR_IN_QTI")
	private double inqty;

	/**
	 * Out quantity
	 */
	@NotNull
	@Column(name="MDSR_OUT_QTI")
	private double outqty;
	
	/**
	 * Minimum quantity
	 */
	@NotNull
	@Column(name="MDSR_MIN_STOCK_QTI")
	private double minqty;

	/**
	 * Lock control
	 */
	@Version
	@Column(name="MDSR_LOCK")
	private Integer lock;
	
	@Transient
	private volatile int hashCode = 0;
		

	public Medical() { }
	
	
	public Medical(Integer code) { 
		super();
		this.code = code;
	}
	/**
	 * Constructor
	 */
	public Medical(Integer code, MedicalType type, String prodCode, String description,
			double initialqty, Integer pcsperpck, double minqty, double inqty, double outqty) {
		super();
		this.code = code;
		this.type = type;
		this.prod_code = prodCode;
		this.description = description;
		this.initialqty = initialqty;
		this.pcsperpck = pcsperpck;
		this.minqty=minqty;
		this.inqty = inqty;
		this.outqty = outqty;
	}
	
	public double getTotalQuantity()
	{
		return initialqty + inqty - outqty;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getInitialqty() {
		return initialqty;
	}

	public void setInitialqty(double initialqty) {
		this.initialqty = initialqty;
	}

	public double getInqty() {
		return inqty;
	}

	public void setInqty(double inqty) {
		this.inqty = inqty;
	}
	public double getMinqty() {
		return minqty;
	}

	public void setMinqty(double minqty) {
		this.minqty = minqty;
	}

	public Integer getLock() {
		return lock;
	}

	public void setLock(Integer lock) {
		this.lock = lock;
	}

	public double getOutqty() {
		return outqty;
	}

	public void setOutqty(double outqty) {
		this.outqty = outqty;
	}

	public MedicalType getType() {
		return type;
	}

	public void setType(MedicalType type) {
		this.type = type;
	}

	public String getProdCode() {
		return prod_code;
	}

	public void setProdCode(String prodCode) {
		this.prod_code = prodCode;
	}

	public Integer getPcsperpck() {
		return pcsperpck;
	}

	public void setPcsperpck(Integer pcsperpck) {
		this.pcsperpck = pcsperpck;
	}

	@Override
	public boolean equals(Object anObject) {
		if (!(anObject instanceof Medical)) return false;
		if (getProdCode() == null || ((Medical) anObject).getProdCode() == null) return false;
		if (getProdCode() != null && ((Medical) anObject).getProdCode() != null && !getProdCode().equals(((Medical) anObject).getProdCode())) return false;
		return (getCode().equals(((Medical) anObject).getCode())
						&& getDescription().equalsIgnoreCase(((Medical) anObject).getDescription())
						&& getType().equals(((Medical) anObject).getType())
						&& getInitialqty()==(((Medical) anObject).getInitialqty()) 
						&& getInqty()==(((Medical) anObject).getInqty())
						&& getOutqty()==(((Medical) anObject).getOutqty()));
	}

	public String toString() {
		return getDescription();
	}

	@Override
	public int compareTo(Medical o) {
		return this.description.compareTo(o.getDescription());
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
