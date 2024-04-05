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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.accounting.model;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="OH_BILLPAYMENTS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLP_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLP_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLP_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLP_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLP_LAST_MODIFIED_DATE"))
public class BillPayments extends Auditable<String> implements Comparable<BillPayments> {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BLP_ID")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="BLP_ID_BILL")	
	private Bill bill;

	@NotNull
	@Column(name="BLP_DATE")		// SQL type: datetime
	private LocalDateTime date;

	@NotNull
	@Column(name="BLP_AMOUNT")
	private double amount;

	@NotNull
	@Column(name="BLP_USR_ID_A")
	private String user;

	@Transient
	private volatile int hashCode;
	
	
	public BillPayments() {
		super();
	}
	
	public BillPayments(int id, Bill bill, LocalDateTime date, double amount, String user) {
		super();
		this.id = id;
		this.bill = bill;
		this.date = TimeTools.truncateToSeconds(date);
		this.amount = amount;
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@Override
	public int compareTo(BillPayments anObject) {
		return this.date.compareTo(anObject.getDate());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof BillPayments)) {
			return false;
		}
		
		BillPayments billPayment = (BillPayments)obj;
		return (id == billPayment.getId());
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + id;
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
}
