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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.menu.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;

@Entity
@Table(name="OH_USER")
@AttributeOverride(name = "createdBy", column = @Column(name = "US_CREATED_BY", updatable = false))
@AttributeOverride(name = "createdDate", column = @Column(name = "US_CREATED_DATE", updatable = false))
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

	@Column(name="US_FAILED_ATTEMPTS")
	private int failedAttempts;

	@Column(name="US_ACCOUNT_LOCKED")
	private boolean isAccountLocked;

	@Column(name="US_LOCK_TIME")
	private LocalDateTime lockedTime;

	@Column(name="US_LAST_LOGIN")
	private LocalDateTime lastLogin;

	@Transient
	private volatile int hashCode;

	public User() {
	}

	public User(String aName, UserGroup aGroup, String aPasswd, String aDesc) {
		this.userName = aName;
		this.userGroupName = aGroup;
		this.passwd = aPasswd;
		this.desc = aDesc;
		this.failedAttempts = 0;
		this.isAccountLocked = false;
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

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public boolean isAccountLocked() {
		return isAccountLocked;
	}

	public void setAccountLocked(boolean accountLocked) {
		isAccountLocked = accountLocked;
	}

	public LocalDateTime getLockedTime() {
		return lockedTime;
	}

	public void setLockedTime(LocalDateTime lockedTime) {
		this.lockedTime = lockedTime;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
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
