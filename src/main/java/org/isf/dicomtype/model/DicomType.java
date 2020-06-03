/**
 * 
 */
package org.isf.dicomtype.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Mwithi
 *
 */
@Entity
@Table(name = "DICOMTYPE")
public class DicomType {
	
	@Id 
	@Column(name = "DCMT_ID")
	private String dicomTypeID;
	
	@NotNull
	@Column(name = "DCMT_DESC")
	private String dicomTypeDescription;

	public DicomType(String dicomTypeID, String dicomTypeDescription) {
		super();
		this.dicomTypeID = dicomTypeID;
		this.dicomTypeDescription = dicomTypeDescription;
	}

	public DicomType() {}

	public String getDicomTypeID() {
		return dicomTypeID;
	}

	public void setDicomTypeID(String dicomTypeID) {
		this.dicomTypeID = dicomTypeID;
	}

	public String getDicomTypeDescription() {
		return dicomTypeDescription;
	}

	public void setDicomTypeDescription(String dicomTypeDescription) {
		this.dicomTypeDescription = dicomTypeDescription;
	}

	@Override
	public String toString() {
		return this.dicomTypeDescription;
	}

}
