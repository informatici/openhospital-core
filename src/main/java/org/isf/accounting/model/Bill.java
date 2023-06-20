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
package org.isf.accounting.model;

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

import org.isf.admission.model.Admission;
import org.isf.patient.model.Patient;
import org.isf.priceslist.model.PriceList;
import org.isf.utils.db.Auditable;
import org.isf.utils.time.TimeTools;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Bill - model for the bill entity
 * -----------------------------------------
 * modification history
 * ? - Mwithi - first version
 * 25/08/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_BILLS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "BLL_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "BLL_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "BLL_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "BLL_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "BLL_LAST_MODIFIED_DATE"))
public class Bill extends Auditable<String> implements Cloneable, Comparable<Bill> {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BLL_ID")
	private int id;
	
	@NotNull
	@Column(name="BLL_DATE")		// SQL type: datetime
	private LocalDateTime date;

	@NotNull
	@Column(name="BLL_UPDATE")		// SQL type: datetime
	private LocalDateTime update;

	@NotNull
	@Column(name="BLL_IS_LST")
	private boolean isList;
	
	@ManyToOne
	@JoinColumn(name="BLL_ID_LST")
	private PriceList list;
	
	@Column(name="BLL_LST_NAME")
	private String listName;

	@NotNull
	@Column(name="BLL_IS_PAT")
	private boolean isPatient;
	
	@ManyToOne
	@JoinColumn(name="BLL_ID_PAT")
	private Patient billPatient;
		
	@Column(name="BLL_PAT_NAME")
	private String patName;
	
	@Column(name="BLL_STATUS")
	private String status;
	
	@Column(name="BLL_AMOUNT")
	private Double amount;
	
	@Column(name="BLL_BALANCE")
	private Double balance;

	@NotNull
	@Column(name="BLL_USR_ID_A")
	private String user;
	
	@ManyToOne
	@JoinColumn(name="BLL_ADM_ID")
	private Admission admission;

	@Transient
	private volatile int hashCode = 0;
	
	
	public Bill() {
		super();
		this.id = 0;
		this.date = TimeTools.getNow();
		this.update = TimeTools.getNow();
		this.isList = true;
		this.listName = "";
		this.isPatient = false;
		this.patName = "";
		this.status = "";
		this.amount = 0.;
		this.balance = 0.;
		this.user = "admin";
	}

	public Bill(int id, LocalDateTime  date, LocalDateTime  update,
			boolean isList, PriceList list, String listName, boolean isPatient,
			Patient billPatient, String patName, String status, Double amount, Double balance, String user, Admission admission) {
		super();
		this.id = id;
		this.date = TimeTools.truncateToSeconds(date);
		this.update = TimeTools.truncateToSeconds(update);
		this.isList = isList;
		this.list = list;
		this.listName = listName;
		this.isPatient = isPatient;
		this.billPatient = billPatient;
		this.patName = patName;
		this.status = status;
		this.amount = amount;
		this.balance = balance;
		this.user = user;
		this.admission = admission;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = TimeTools.truncateToSeconds(date);
	}
	public LocalDateTime getUpdate() {
		return update;
	}
	public void setUpdate(LocalDateTime update) {
		this.update = TimeTools.truncateToSeconds(update);
	}
	public boolean isList() {
		return isList;
	}
	public void setIsList(boolean isList) {
		this.isList = isList;
	}
	public PriceList getPriceList() {
		return list;
	}
	public void setPriceList(PriceList list) {
		this.list = list;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public boolean isPatient() {
		return isPatient;
	}
	public void setIsPatient(boolean isPatient) {
		this.isPatient = isPatient;
	}
	public Patient getBillPatient() {
		return billPatient;
	}
	public void setBillPatient(Patient billPatient) {
		this.billPatient = billPatient;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public Admission getAdmission() {
		return admission;
	}
	
	public void setAdmission(Admission admission) {
		this.admission = admission;
	}

	@Override
	public int compareTo(Bill obj) {
		return this.id - obj.getId();
	}
		
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Bill)) {
			return false;
		}
		
		Bill bill = (Bill)obj;
		return (id == bill.getId());
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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
