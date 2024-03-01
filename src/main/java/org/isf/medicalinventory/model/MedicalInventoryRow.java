package org.isf.medicalinventory.model;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

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

	@NotNull
	@Column(name = "MINVTR_THEORITIC_QTY")
	private double theoreticQty;

	@NotNull
	@Column(name = "MINVTR_REAL_QTY")
	private double realQty;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MINVTR_MINVT_ID")
	private MedicalInventory inventory;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MINVTR_MDSR_ID")
	private Medical medical;

	@NotNull
	@ManyToOne
	@JoinColumn(name="MINVTR_LT_ID_A")
	private Lot lot;

	@NotNull
	@Column(name = "MINVTR_U_PRICE")
	private double unitPrice;
	
	@Version
	@Column(name="MINVTR_LOCK")
	private Integer lock;

	public MedicalInventoryRow() {
	}

	public MedicalInventoryRow(Integer id, double theoreticQty, double realQty, MedicalInventory inventory, Medical medical,
					Lot lot, double unitPrice) {
		this.id = id;
		this.theoreticQty = theoreticQty;
		this.realQty = realQty;
		this.inventory = inventory;
		this.medical = medical;
		this.lot = lot;
		this.unitPrice = unitPrice;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getTheoreticQty() {
		return theoreticQty;
	}

	public void setTheoreticQty(double theoreticQty) {
		this.theoreticQty = theoreticQty;
	}

	public double getRealQty() {
		return realQty;
	}

	public void setRealqty(double realQty) {
		this.realQty = realQty;
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
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getSearchString() {
		StringBuffer sbNameCode = new StringBuffer();
		sbNameCode.append(getMedical() != null ? getMedical().getDescription().toLowerCase() : "");
		sbNameCode.append(getMedical() != null ? getMedical().getProdCode().toLowerCase() : "");
		return sbNameCode.toString();
	}
}
