package org.isf.medicalinventory.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_MEDICALDSRINVENTORYROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "MINVTR_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "MINVTR_CREATED_DATE", updatable = false))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "MINVTR_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "MINVTR_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "MINVTR_LAST_MODIFIED_DATE"))
public class MedicalInventoryRow extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MINVTR_ID")
	private Integer id;

	@Column(name = "MINVTR_THEORITIC_QTY")
	private double theoreticqty;

	@Column(name = "MINVTR_REAL_QTY")
	private double realqty;

	@Column(name = "MINVTR_MINVT_ID")
	private MedicalInventory inventory;

	@Column(name = "MINVTR_MDSR_ID")
	private Medical medical;

	@Column(name = "MINVTR_LT_ID_A")
	private Lot lot;

	@Column(name = "MINVTR_U_PRICE")
	private double unitprice;

	@Column(name = "MINVTR_COST")
	private double cost;
	
	@Version
	@Column(name="MINVTR_LOCK")
	private Integer lock;

	public MedicalInventoryRow() {
	}

	public MedicalInventoryRow(Integer id, double theoreticqty, double realqty, MedicalInventory inventory, Medical medical,
					Lot lot, double unitprice, double cost) {
		this.id = id;
		this.theoreticqty = theoreticqty;
		this.realqty = realqty;
		this.inventory = inventory;
		this.medical = medical;
		this.lot = lot;
		this.unitprice = unitprice;
		this.cost = cost;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getTheoreticqty() {
		return theoreticqty;
	}

	public void setTheoreticqty(double theoreticqty) {
		this.theoreticqty = theoreticqty;
	}

	public double getRealqty() {
		return realqty;
	}

	public void setRealqty(double realqty) {
		this.realqty = realqty;
	}

	public MedicalInventory getInventory() {
		return inventory;
	}

	public void setInventory(MedicalInventory inventory) {
		this.inventory = inventory;
	}

	public Medical getMedical() {
		return medical;
	}

	public void setMedical(Medical medical) {
		this.medical = medical;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public double getUnitPrice() {
		return unitprice;
	}

	public void setUnitPrice(double unitprice) {
		this.unitprice = unitprice;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getMedical() != null ? getMedical().getDescription().toLowerCase() : "");
		sbNameCode.append(getMedical() != null ? getMedical().getProdCode().toLowerCase() : "");
		return sbNameCode.toString();
	}
}
