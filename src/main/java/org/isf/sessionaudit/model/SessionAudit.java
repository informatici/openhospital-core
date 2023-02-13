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
package org.isf.sessionaudit.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "OH_SESSION_AUDIT")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "SEA_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "SEA_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "SEA_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "SEA_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "SEA_LAST_MODIFIED_DATE"))
public class SessionAudit extends Auditable<String> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SEA_ID")
	private int code;

	@Column(name = "SEA_US_ID_A")
	private String userName;

	/*
	 * Date of this control
	 */
	@NotNull
	@Column(name = "SEA_LOGIN")
	private LocalDateTime loginDate;

	/*
	 * Date of next control
	 */
	@Column(name = "SEA_LOGOUT")
	private LocalDateTime logoutDate;

	@Transient
	private volatile int hashCode = 0;

	public SessionAudit() {
		super();
	}

	public SessionAudit(String userName, @NotNull LocalDateTime loginDate, LocalDateTime logoutDate) {
		super();
		this.userName = userName;
		this.loginDate = loginDate;
		this.logoutDate = logoutDate;
	}

	public void setCode(int aCode) {
		code = aCode;
	}

	public int getCode() {
		return code;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(LocalDateTime loginDate) {
		this.loginDate = loginDate;
	}

	public LocalDateTime getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(LocalDateTime logoutDate) {
		this.logoutDate = logoutDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, hashCode, loginDate, logoutDate, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SessionAudit other = (SessionAudit) obj;
		return code == other.code && hashCode == other.hashCode && Objects.equals(loginDate, other.loginDate) && Objects.equals(logoutDate, other.logoutDate)
						&& Objects.equals(userName, other.userName);
	}



}
