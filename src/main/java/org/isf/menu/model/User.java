/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.menu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;

/**
 * ------------------------------------------
 * User - model for the user entity
 * -----------------------------------------
 * modification history
 * ? - ? - first version
 * 07/05/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_USER")
@AttributeOverride(name = "createdBy", column = @Column(name = "US_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "US_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "US_LAST_MODIFIED_BY"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "US_LAST_MODIFIED_DATE"))
@AttributeOverride(name = "active", column = @Column(name = "US_ACTIVE"))
public class User extends Auditable<String> {

	@Id
	@Column(name="US_ID_A")		
	private String userName;

	@NotNull
	@ManyToOne
	@JoinColumn(name="US_UG_ID_A")
	private UserGroup userGroupName;

	@NotNull
	@Column(name="US_PASSWD")
	private String passwd;

	@Column(name="US_DESC")
	private String desc;	
	
	@Transient
	private volatile int hashCode = 0;

	public User() {
	}

	public User(String aName, UserGroup aGroup, String aPasswd, String aDesc) {
		this.userName = aName;
		this.userGroupName = aGroup;
		this.passwd = aPasswd;
		this.desc = aDesc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public UserGroup getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(UserGroup userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String toString() {
		return getUserName();
	}

	@Override
	public boolean equals(Object anObject) {
		return anObject instanceof User && (getUserName().equalsIgnoreCase(((User) anObject).getUserName())
				&& getDesc().equalsIgnoreCase(((User) anObject).getDesc()));
	}

	@Override
	public int hashCode() {
	    if (this.hashCode == 0) {
	        final int m = 23;
	        int c = 133;
	        
	        c = m * c + userName.hashCode();
	        
	        this.hashCode = c;
	    }
	  
	    return this.hashCode;
	}	
	
}//class User
