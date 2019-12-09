package org.isf.accounting.model;

import java.util.GregorianCalendar;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;

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

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Pure Model BillPayments : represents a patient Payment for a Bill
 * @author Mwithi
 *
 */
/*------------------------------------------
 * BillPayments - model for the bill entity
 * -----------------------------------------
 * modification history
 * ? - Mwithi - first version 
 * 23/08/2051 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="BILLPAYMENTS")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="BLP_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="BLP_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="BLP_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="BLP_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="BLP_LAST_MODIFIED_DATE"))
})
public class BillPayments  extends Auditable<String> implements Comparable<Object>
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BLP_ID")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="BLP_ID_BILL")	
	private Bill bill;

	@NotNull
	@Column(name="BLP_DATE")
	private GregorianCalendar date;

	@NotNull
	@Column(name="BLP_AMOUNT")
	private double amount;

	@NotNull
	@Column(name="BLP_USR_ID_A")
	private String user;

	@Transient
	private volatile int hashCode = 0;
	
	
	public BillPayments() {
		super();
	}
	
	public BillPayments(int id, Bill bill, GregorianCalendar date,
			double amount, String user) {
		super();
		this.id = id;
		this.bill = bill;
		this.date = date;
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

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
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
	
	public int compareTo(Object anObject) {
		if (anObject instanceof BillPayments)
			if (this.date.after(((BillPayments)anObject).getDate()))
				return 1;
			else
				return 0;
		return 0;
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
