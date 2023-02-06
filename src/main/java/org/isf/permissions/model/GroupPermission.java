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
package org.isf.permissions.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.isf.menu.model.UserGroup;
import org.isf.utils.db.Auditable;

/*------------------------------------------
 * User - model for the user entity
 * -----------------------------------------
 * modification history
 * 24/12/2020 - Andrei - first version
 * 
 *------------------------------------------*/
@Entity
@Table(name = "OH_GROUPPERMISSION")
		@AttributeOverrides({ 
			@AttributeOverride(name = "createdBy", column = @Column(name = "GP_CREATED_BY")), 
			@AttributeOverride(name = "createdDate", column = @Column(name = "GP_CREATED_DATE")), 
			@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "GP_LAST_MODIFIED_BY")),
			@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "GP_LAST_MODIFIED_DATE")), 
			@AttributeOverride(name = "active", column = @Column(name = "GP_ACTIVE"))})
public class GroupPermission extends Auditable<String> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "GP_ID")
	private int id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "GP_UG_ID_A", referencedColumnName = "UG_ID_A")
	private UserGroup userGroup;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "GP_P_ID_A", referencedColumnName="P_ID_A")
	private Permission permission;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
	
	

}
