/**
 * @(#) DiseaseType.java
 * 21-jan-2006
 */
package org.isf.distype.model;

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


/**
 * Pure Model Exam : represents a disease type
 * @author bob
 */
/*------------------------------------------
* Disease Type - model for the disease type entity
* -----------------------------------------
* modification history
* ? - bob - first version 
* 03/01/2015 - Antonio - ported to JPA
* 
*------------------------------------------*/
@Entity
@Table(name="DISEASETYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="DCL_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="DCL_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="DCL_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="DCL_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="DCL_LAST_MODIFIED_DATE"))
})
public class DiseaseType extends Auditable<String>
{
	@Id 
	@Column(name="DCL_ID_A")	    
    private String code;
	@NotNull
	@Column(name="DCL_DESC")
    private String description;
	
	@Transient
	private volatile int hashCode = 0;
	
	public DiseaseType() 
    {
		super();
    }
	
    /**
     * @param aCode
     * @param aDescription
     */
    public DiseaseType(String aCode, String aDescription) {
        super();
        this.code = aCode;
        this.description = aDescription;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String aCode) {
        this.code = aCode;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String aDescription) {
        this.description = aDescription;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof DiseaseType)) {
			return false;
		}
		
		DiseaseType diseaseType = (DiseaseType)obj;
		return (this.getCode().equals(diseaseType.getCode()));
	}

    public String toString() 
    {
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
