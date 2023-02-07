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
package org.isf.hospital.model;

import java.sql.Time;

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
 * Hospital - model representing the Hospital
 * -----------------------------------------
 * modification history
 * 21-jan-2006 - bob - first version
 * 06/01/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_HOSPITAL")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "HOS_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "HOS_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "HOS_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "HOS_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "HOS_LAST_MODIFIED_DATE"))
public class Hospital extends Auditable<String> {

	public static final String VISIT_START_TIME = "06:30:00";
	public static final String VISIT_END_TIME = "20:00:00";
	public static final int VISIT_INCREMENT = 15;
	public static final int VISIT_DURATION = 30;

	@Id
	@Column(name="HOS_ID_A")
    private String code;

	@NotNull
	@Column(name="HOS_NAME")	
    private String description;

	@NotNull
	@Column(name="HOS_ADDR")
    private String address;

	@NotNull
	@Column(name="HOS_CITY")
    private String city;
	
	@Column(name="HOS_TELE")
    private String telephone;
	
	@Column(name="HOS_FAX")
    private String fax;
	
	@Column(name="HOS_EMAIL")
    private String email;

	@Column(name="HOS_CURR_COD")
    private String currencyCod;

	@NotNull
	@Column(name="HOS_VISIT_START")
	private Time visitStartTime;

	@NotNull
	@Column(name="HOS_VISIT_END")
	private Time visitEndTime;

	@NotNull
	@Column(name="HOS_VISIT_INCREMENT")
	private int visitIncrement;

	@NotNull
	@Column(name="HOS_VISIT_DURATION")
	private int visitDuration;

	@Version
	@Column(name="HOS_LOCK")
    private Integer lock;

	@Transient
	private volatile int hashCode = 0;

	public Hospital() {
		super();
		this.code = null;
		this.description = null;
		this.address = null;
		this.city = null;
		this.telephone = null;
		this.fax = null;
		this.email = null;
		this.currencyCod = null;
		this.visitStartTime = Time.valueOf(VISIT_START_TIME);
		this.visitEndTime = Time.valueOf(VISIT_END_TIME);
		this.visitIncrement = VISIT_INCREMENT;
		this.visitDuration = VISIT_DURATION;
	}

	/**
	 * @param aCode
	 * @param aDescription
	 * @param aAddress
	 * @param aCity
	 * @param aTelephone
	 * @param aFax
	 * @param aEmail
	 * @param aCurrencyCod
	 */
	public Hospital(String aCode, String aDescription, String aAddress,
    		String aCity, String aTelephone, String aFax, 
    		String aEmail, String aCurrencyCod) {
        super();
        this.code = aCode;
        this.description = aDescription;
        this.address = aAddress;
        this.city = aCity;
        this.telephone = aTelephone;
        this.fax = aFax;
        this.email = aEmail;
        this.currencyCod = aCurrencyCod;
		this.visitStartTime = Time.valueOf(VISIT_START_TIME);
		this.visitEndTime = Time.valueOf(VISIT_END_TIME);
		this.visitIncrement = VISIT_INCREMENT;
		this.visitDuration = VISIT_DURATION;
    }

	public Hospital(String aCode, String aDescription, String aAddress,
			String aCity, String aTelephone, String aFax,
			String aEmail, String aCurrencyCod,
			Time visitStartTime, Time visitEndTime, int visitIncrement, int visitDuration) {
		super();
		this.code = aCode;
		this.description = aDescription;
		this.address = aAddress;
		this.city = aCity;
		this.telephone = aTelephone;
		this.fax = aFax;
		this.email = aEmail;
		this.currencyCod = aCurrencyCod;
		this.visitStartTime = visitStartTime;
		this.visitEndTime = visitEndTime;
		this.visitIncrement = visitIncrement;
		this.visitDuration = visitDuration;
	}

    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String aAddress) {
        this.address = aAddress;
    }
    
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String aCity) {
        this.city = aCity;
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
    
    public Integer getLock() {
        return this.lock;
    }
    
    public void setLock(Integer aLock) {
        this.lock = aLock;
    }
    
    public String getTelephone() {
        return this.telephone;
    }
    
    public void setTelephone(String aTelephone) {
        this.telephone = aTelephone;
    }
    
    public String getCurrencyCod() {
        return this.currencyCod;
    }
    
    public void setCurrencyCod(String aCurrencyCod) {
        this.currencyCod = aCurrencyCod;
    }

	public Time getVisitStartTime() {
		return visitStartTime;
	}

	public void setVisitStartTime(Time startTime) {
		this.visitStartTime = startTime;
	}

	public Time getVisitEndTime() {
		return visitEndTime;
	}

	public void setVisitEndTime(Time endTime) {
		this.visitEndTime = endTime;
	}

	public int getVisitIncrement() {
		return visitIncrement;
	}

	public void setVisitIncrement(int visitIncrement) {
		this.visitIncrement = visitIncrement;
	}

	public int getVisitDuration() {
		return visitDuration;
	}

	public void setVisitDuration(int visitDuration) {
		this.visitDuration = visitDuration;
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof Hospital && (getCode().equals(((Hospital) anObject).getCode())
				&& getDescription().equalsIgnoreCase(((Hospital) anObject).getDescription())
				&& getTelephone().equalsIgnoreCase(((Hospital) anObject).getTelephone())
				&& getFax().equalsIgnoreCase(((Hospital) anObject).getFax())
				&& getAddress().equalsIgnoreCase(((Hospital) anObject).getAddress())
				&& getCity().equalsIgnoreCase(((Hospital) anObject).getCity())
				&& getEmail().equalsIgnoreCase(((Hospital) anObject).getEmail())
				&& getCurrencyCod().equalsIgnoreCase(((Hospital) anObject).getCurrencyCod())
				&& getVisitStartTime().equals(((Hospital)anObject).getVisitStartTime())
				&& getVisitEndTime().equals(((Hospital)anObject).getVisitEndTime())
				&& getVisitIncrement() == ((Hospital)anObject).getVisitIncrement()
				&& getVisitDuration() == ((Hospital)anObject).getVisitDuration());
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
