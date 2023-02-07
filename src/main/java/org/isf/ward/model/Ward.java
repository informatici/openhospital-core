/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.ward.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * Ward - model for the ward entity; represents a ward
 * -----------------------------------------
 * modification history
 * 21-jan-2006 - bob - first version
 * 30/09/2015 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_WARD")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "WRD_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "WRD_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "WRD_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "WRD_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "WRD_LAST_MODIFIED_DATE"))
public class Ward extends Auditable<String> {

	@Id
	@Column(name="WRD_ID_A")	
	private String code;

	@NotNull
	@Column(name="WRD_NAME")
	private String description;
	
	@Column(name="WRD_TELE")
	private String telephone;
	
	@Column(name="WRD_FAX")
	private String fax;
	
	@Column(name="WRD_EMAIL")
	private String email;

	@NotNull
	@Column(name="WRD_NBEDS")
	private Integer beds;

	@NotNull
	@Column(name="WRD_NQUA_NURS")
	private Integer nurs;

	@NotNull
	@Column(name="WRD_NDOC")
	private Integer docs;
	
	@Column(name="WRD_IS_OPD")	
	private boolean isOpd;

	@NotNull
	@Column(name="WRD_IS_PHARMACY")	
	private boolean isPharmacy;

	@NotNull
	@Column(name="WRD_IS_MALE")   
	private boolean isMale;

	@NotNull
	@Column(name="WRD_IS_FEMALE")	
	private boolean isFemale;

	@NotNull
	@Column(name="WRD_VISIT_DURATION")
	private int visitDuration;

	@Version
	@Column(name="WRD_LOCK")
	private Integer lock;
	
	@Transient
	private volatile int hashCode = 0;
	
	public Ward() {
		super();
	}

	public Ward(String code, String description, String telephone, String fax, String email, Integer beds, Integer nurs, Integer docs, boolean isOpd, boolean isPharmacy,
			boolean isMale, boolean isFemale) {
		this(code, description, telephone, fax, email, beds, nurs, docs, isOpd, isPharmacy, isMale, isFemale, 30);
	}

	/**
	 * @param code
	 * @param description
	 * @param telephone
	 * @param fax
	 * @param email
	 * @param beds
	 * @param nurs
	 * @param docs
	 * @param isPharmacy
	 * @param isMale
	 * @param isFemale
	 * @param visitDuration
	 */
	public Ward(String code, String description, String telephone, String fax, String email, Integer beds, Integer nurs, Integer docs, boolean isOpd, boolean isPharmacy,
			boolean isMale, boolean isFemale, int visitDuration) {
		super();
		this.code = code;
		this.description = description;
		this.telephone = telephone;
		this.fax = fax;
		this.email = email;
		this.beds = beds;
		this.nurs = nurs;
		this.docs = docs;
		this.isOpd = isOpd;
		this.isPharmacy = isPharmacy;
		this.isMale = isMale;
		this.isFemale = isFemale;
		this.visitDuration = visitDuration;
	}

	//TODO: to reduce number of constructors
	public Ward(String code, String description, String telephone, String fax, String email, Integer beds, Integer nurs, Integer docs, boolean isMale,
			boolean isFemale) {
		this(code, description, telephone, fax, email, beds, nurs, docs, isMale, isFemale, 30);
	}

	public Ward(String code, String description, String telephone, String fax, String email, Integer beds, Integer nurs, Integer docs, boolean isMale,
			boolean isFemale, int visitDuration) {
		super();
		this.code = code;
		this.description = description;
		this.telephone = telephone;
		this.fax = fax;
		this.email = email;
		this.beds = beds;
		this.nurs = nurs;
		this.docs = docs;
		this.isOpd = false;
		this.isPharmacy = false;
		this.isMale = isMale;
		this.isFemale = isFemale;
		this.visitDuration = visitDuration;
	}

	public Integer getBeds() {
		return this.beds;
	}

	public void setBeds(Integer aBeds) {
		this.beds = aBeds;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String aCode) {
		this.code = aCode;
	}

	public Integer getDocs() {
		return this.docs;
	}

	public void setDocs(Integer aDocs) {
		this.docs = aDocs;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String aEmail) {
		this.email = aEmail;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String aFax) {
		this.fax = aFax;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	public Integer getNurs() {
		return this.nurs;
	}

	public void setNurs(Integer aNurs) {
		this.nurs = aNurs;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String aTelephone) {
		this.telephone = aTelephone;
	}

	public Integer getLock() {
		return this.lock;
	}

	public void setLock(Integer aLock) {
		this.lock = aLock;
	}
	
	public boolean isOpd() {
		return isOpd;
	}
	
	public void setOpd(boolean isOPD) {
		this.isOpd = isOPD;
	}
	
	public boolean isPharmacy() {
		return isPharmacy;
	}

	public void setPharmacy(boolean isPharmacy) {
		this.isPharmacy = isPharmacy;
	}

	public boolean isMale() {
		return isMale;
	}

	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	public boolean isFemale() {
		return isFemale;
	}

	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}

	public int getVisitDuration() {
		return visitDuration;
	}

	public void setVisitDuration(int visitDuration) {
		this.visitDuration = visitDuration;
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof Ward
				&& (getCode().equals(((Ward) anObject).getCode()))
				&& getDescription().equalsIgnoreCase(((Ward) anObject).getDescription())
				&& getTelephone().equalsIgnoreCase(((Ward) anObject).getTelephone())
				&& (getFax().equalsIgnoreCase(((Ward) anObject).getFax()))
				&& (getEmail().equalsIgnoreCase(((Ward) anObject).getEmail()))
				&& (getBeds().equals(((Ward) anObject).getBeds()))
				&& (getNurs().equals(((Ward) anObject).getNurs()))
				&& (getDocs().equals(((Ward) anObject).getDocs()))
				&& (getVisitDuration() == ((Ward) anObject).getVisitDuration());
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public String debug() {
		return "Ward [code=" + code + ", description=" + description + ", telephone=" + telephone + ", fax=" + fax
				+ ", email=" + email + ", beds=" + beds + ", nurs=" + nurs + ", docs=" + docs + ", isPharmacy="
				+ isPharmacy + ", isMale=" + isMale + ", isFemale=" + isFemale + ", lock=" + lock + ", hashCode="
				+ hashCode + "]";
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
