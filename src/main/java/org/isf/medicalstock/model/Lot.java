/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/*------------------------------------------
 * Medical Lot - model for the medical entity
 * -----------------------------------------
 * modification history
 * ? - ?
 * 17/01/2015 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="MEDICALDSRLOT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="LT_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="LT_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="LT_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="LT_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="LT_LAST_MODIFIED_DATE"))
})
public class Lot extends Auditable<String>
{
	@Id 
	@Column(name="LT_ID_A")
	private String code;

	@ManyToOne
	@JoinColumn(name="LT_MDSR_ID")
	private Medical medical;

	@NotNull
	@Column(name="LT_PREP_DATE")
	private GregorianCalendar preparationDate;

	@NotNull
	@Column(name="LT_DUE_DATE")
	private GregorianCalendar dueDate;

	@Column(name="LT_COST")
	private BigDecimal cost;

	@Transient
	private int quantity;

	@Transient
	private volatile int hashCode = 0;

	@OneToMany(mappedBy = "lot")
	private List<Movement> movements = new ArrayList<Movement>();

	public Lot() {
	}

	public Lot(String aCode){
		code=aCode;

	}
	public Lot(String aCode,GregorianCalendar aPreparationDate,GregorianCalendar aDueDate){
		code=aCode;
		preparationDate=aPreparationDate;
		dueDate=aDueDate;
	}

	public Lot(String aCode,GregorianCalendar aPreparationDate,GregorianCalendar aDueDate,BigDecimal aCost){
		code=aCode;
		preparationDate=aPreparationDate;
		dueDate=aDueDate;
		cost=aCost;
	}

	public String getCode(){
		return code;
	}

	public int getQuantity(){
		return quantity;
	}

	public int calculateQuantity(){ // TODO: this should replace getter logic, for now we are leaving transient field to avoid unnecessary changes in ui
		int quantity = 0;
		for (Movement movement: movements) {
			if (movement.getType().getType().equals("-")) {
				quantity -= movement.getQuantity();
			} else if (movement.getType().getType().equals("+")) {
				quantity += movement.getQuantity();
			}
		}
		return quantity;
	}
	public Medical getMedical(){
			return medical;
	}

	public GregorianCalendar getPreparationDate(){
		return preparationDate;
	}
	public GregorianCalendar getDueDate(){
		return dueDate;
	}
	public BigDecimal getCost() {
		return cost;
	}
	public void setCode(String aCode){
		code=aCode;
	}
	public void setPreparationDate(GregorianCalendar aPreparationDate){
		preparationDate=aPreparationDate;
	}
	public void setQuantity(int aQuantity){
		quantity=aQuantity;
	}
	public void setMedical(Medical aMedical){
				medical=aMedical;
	}
	public void setDueDate(GregorianCalendar aDueDate){
		dueDate=aDueDate;
	}
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	public String toString(){
		if(code==null)return MessageBundle.getMessage("angal.medicalstock.nolot");
		return getCode();
	}

	public boolean isValidLot(){
		return getCode().length()<=50;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lot other = (Lot) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (cost != null) {
			if (cost.compareTo(other.cost) != 0)
				return false;
		}
		if (dueDate == null) {
			if (other.dueDate != null)
				return false;
		} else if (!dueDate.equals(other.dueDate))
			return false;
		if (preparationDate == null) {
			if (other.preparationDate != null)
				return false;
		} else if (!preparationDate.equals(other.preparationDate))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
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
