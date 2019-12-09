
package org.isf.pregtreattype.model;

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
 *
 */
/*------------------------------------------
* Pregnant Type - model for the disease type entity
* -----------------------------------------
* modification history
* ? - bob - first version 
* 11/01/2015 - Antonio - ported to JPA
* 
*------------------------------------------*/
@Entity
@Table(name="PREGNANTTREATMENTTYPE")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="PTT_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="PTT_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="PTT_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="PTT_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="PTT_LAST_MODIFIED_DATE"))
})
public class PregnantTreatmentType extends Auditable<String>
{
	@Id 
	@Column(name="PTT_ID_A")	     
    private String code;

	@NotNull
	@Column(name="PTT_DESC")
    private String description;

	@Transient
	private volatile int hashCode = 0;
	
	public PregnantTreatmentType() 
    {
		super();
    }
	
    /**
     * @param aCode
     * @param aDescription
     */
    public PregnantTreatmentType(String aCode, String aDescription) {
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
    public boolean equals(Object anObject) {
        return (anObject == null) || !(anObject instanceof PregnantTreatmentType) ? false
                : (getCode().equals(((PregnantTreatmentType) anObject).getCode())
                        && getDescription().equalsIgnoreCase(
                                ((PregnantTreatmentType) anObject).getDescription()));
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
