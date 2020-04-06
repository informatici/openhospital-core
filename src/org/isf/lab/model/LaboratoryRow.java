package org.isf.lab.model;

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

@Entity
@Table(name="LABORATORYROW")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="LABR_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="LABR_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="LABR_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="LABR_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="LABR_LAST_MODIFIED_DATE"))
})
public class LaboratoryRow extends Auditable<String>
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="LABR_ID")
	private Integer code;

	@NotNull
	@ManyToOne
	@JoinColumn(name="LABR_LAB_ID")
	private Laboratory laboratory;

	@NotNull
	@Column(name="LABR_DESC")
	private String description;
	
	@Transient
	private volatile int hashCode = 0;
		

	public LaboratoryRow() { }
	
	public LaboratoryRow(Laboratory aLabId, String aDescription){
		laboratory = aLabId;
		description = aDescription;
	}
	
	public LaboratoryRow(Integer aCode, Laboratory aLabId, String aDescription){
		code=aCode;
		laboratory = aLabId;
		description = aDescription;
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
	public Laboratory getLabId() {
		return laboratory;
	}
	public void setLabId(Laboratory laboratory) {
		this.laboratory = laboratory;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof LaboratoryRow)) {
			return false;
		}
		
		LaboratoryRow laboratoryRow = (LaboratoryRow)obj;
		if (this.getCode() != null && laboratoryRow.getCode() != null)
			return (this.getCode().equals(laboratoryRow.getCode()) );
		return (this.getDescription().equals(laboratoryRow.getDescription()));
	}
	
	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + (code == null ? 0 : code);
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
}
