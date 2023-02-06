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
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * ------------------------------------------
 * GroupMenu
 * -----------------------------------------
 * modification history
 * ? - ? - first version
 * 07/05/2016 - Antonio - ported to JPA
 * ------------------------------------------
 */
@Entity
@Table(name="OH_GROUPMENU")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverride(name = "createdBy", column = @Column(name = "GM_CREATED_BY"))
@AttributeOverride(name = "createdDate", column = @Column(name = "GM_CREATED_DATE"))
@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "GM_LAST_MODIFIED_BY"))
@AttributeOverride(name = "active", column = @Column(name = "GM_ACTIVE"))
@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "GM_LAST_MODIFIED_DATE"))
public class GroupMenu extends Auditable<String> {

	@Id
	@Column(name="GM_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer code;
	
	@Column(name="GM_UG_ID_A")
	private String userGroup;
	
	@Column(name="GM_MNI_ID_A")
	private String menuItem;

	@Transient
	private volatile int hashCode = 0;

	public GroupMenu() {
	}

	public GroupMenu(String userGroup, String menuItem) {
		this.userGroup = userGroup;
		this.menuItem = menuItem;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;
	}

	@Override
	public int getActive() {
		return active;
	}

	@Override
	public void setActive(int active) {
		this.active = active;
	}
	
	@Override
	public boolean equals(Object anObject) {
        return anObject instanceof GroupMenu && (getCode().equals(((GroupMenu) anObject).getCode())
		        && getUserGroup().equalsIgnoreCase(((GroupMenu) anObject).getUserGroup())
		        && getMenuItem().equals(((GroupMenu) anObject).getMenuItem())
		        && getActive() == ((GroupMenu) anObject).getActive());
    }

}
