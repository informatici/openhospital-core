package org.isf.accounting.model;

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
 * Pure Model BillItems : represents an item in the Bill
 * @author Mwithi
 */
/*------------------------------------------
 * BillItems - model for the bill entity
 * -----------------------------------------
 * modification history
 * ? - Mwithi - first version 
 * 23/08/2051 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="BILLITEMS")
@EntityListeners(AuditingEntityListener.class)

@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="BLI_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="BLI_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="BLI_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="BLI_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="BLI_LAST_MODIFIED_DATE"))
})

public class BillItems extends Auditable<String>
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BLI_ID")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="BLI_ID_BILL")
	private Bill bill;

	@NotNull
	@Column(name="BLI_IS_PRICE")
	private boolean isPrice;
	
	@Column(name="BLI_ID_PRICE")
	private String priceID;
	
	@Column(name="BLI_ITEM_DESC")
	private String itemDescription;

	@NotNull
	@Column(name="BLI_ITEM_AMOUNT")
	private double itemAmount;

	@NotNull
	@Column(name="BLI_QTY")
	private int itemQuantity;
	
	@Transient
	private volatile int hashCode = 0;
	
	/**
	 * Store  the code of the item that is used for search purpose.
	 * For medical for example it will store the prod_code.
	 * it is necessary for medical and otherPRices items
	 * used in the patient bill edit
	 * The field is not persisted
	 */
	@Transient
	private String itemDisplayCode;
	
	/**
	 * Store the item Id that is involved in this bill item (medId, exaId, opeId...)
	 */
	@Transient
	private String itemId;
	
	
	public BillItems() {
		super();
	}

	public BillItems(int id, Bill bill, boolean isPrice, String priceID,
			String itemDescription, double itemAmount, int itemQuantity) {
		super();
		this.id = id;
		this.bill = bill;
		this.isPrice = isPrice;
		this.priceID = priceID;
		this.itemDescription = itemDescription;
		this.itemAmount = itemAmount;
		this.itemQuantity = itemQuantity;
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

	public boolean isPrice() {
		return isPrice;
	}

	public void setPrice(boolean isPrice) {
		this.isPrice = isPrice;
	}

	public String getPriceID() {
		return priceID;
	}

	public void setPriceID(String priceID) {
		this.priceID = priceID;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public double getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(double itemAmount) {
		this.itemAmount = itemAmount;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof BillItems)) {
			return false;
		}
		
		BillItems billItem = (BillItems)obj;
		return (id == billItem.getId());
	}
/**
 *get the item ID
 * @return
 */
	public String getItemDisplayCode() {
		if(itemDisplayCode==null || itemDisplayCode.equals("")){
			itemDisplayCode=itemId;
		}
		return itemDisplayCode;
	}

	public void setItemDisplayCode(String itemDisplayCode) {
		this.itemDisplayCode = itemDisplayCode;
	}
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
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
