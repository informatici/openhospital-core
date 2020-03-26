package org.isf.vactype.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/*------------------------------------------
 * VaccineType - vaccine type class to model vaccine type
 * -----------------------------------------
 * modification history
 * 19/10/2011 - Cla - version is now 1.0
 * 18/11/2011 - Cla - inserted print method
 *------------------------------------------*/

/**
 * Pure Model vaccineType (type of vaccines)
 * 
 * @author bob
 */
/*------------------------------------------
 * Vaccine Type - model for the vaccine type entity
 * -----------------------------------------
 * modification history
 * ? - bob - first version 
 * 18/01/2015 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="VACCINETYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="VACT_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="VACT_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="VACT_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="VACT_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="VACT_LAST_MODIFIED_DATE"))
})
public class VaccineType extends Auditable<String> 
{
	@Id 
	@Column(name="VACT_ID_A")	
	private String code;

	@NotNull
	@Column(name="VACT_DESC")	
	private String description;
	
	@Transient
	private volatile int hashCode = 0;
	
	public VaccineType() 
    {
		super();
    }

	public VaccineType(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object anObject) {
		return (anObject == null) || !(anObject instanceof VaccineType) ? false
				: (getCode().equals(((VaccineType) anObject).getCode())
						&& getDescription().equalsIgnoreCase(
								((VaccineType) anObject).getDescription()));
	}

	
	public String print() {
		return "vaccineType code=."+getCode()+". description=."+getDescription()+".";
	}

	public String toString() {
		return getDescription();
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
