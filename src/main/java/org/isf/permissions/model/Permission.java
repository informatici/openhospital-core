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

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.isf.utils.db.Auditable;

/*------------------------------------------
 * User - model for the user entity
 * -----------------------------------------
 * modification history
 * 24/12/2020 - Andrei - first version
 * 
 *------------------------------------------*/
@Entity
@Table(name = "OH_PERMISSIONS")
@AttributeOverrides({ 
		@AttributeOverride(name = "createdBy", column = @Column(name = "P_CREATED_BY")), 
		@AttributeOverride(name = "createdDate", column = @Column(name = "P_CREATED_DATE")), 
		@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "P_LAST_MODIFIED_BY")),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "P_LAST_MODIFIED_DATE")), 
		@AttributeOverride(name = "active", column = @Column(name = "P_ACTIVE"))})
public class Permission extends Auditable<String> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "P_ID_A")
	private int id;

	@Column(name="P_NAME")
	private String name;	

	@Column(name="P_DESCRIPTION")
	private String description;
	
	@OneToMany(mappedBy = "permission", cascade = CascadeType.REMOVE)
	private List<GroupPermission> groupPermission;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<GroupPermission> getGroupPermission() {
		return groupPermission;
	}

	public void setGroupPermission(List<GroupPermission> groupPermission) {
		this.groupPermission = groupPermission;
	}	
	
	

}
