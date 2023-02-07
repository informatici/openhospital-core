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
package org.isf.permissions.manager;

import java.util.List;

import org.isf.permissions.model.Permission;
import org.isf.permissions.service.PermissionIoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionManager {

	@Autowired
	private PermissionIoOperations operations;

	public List<Permission> retrivePermisionsByGroupCode(String userGropupCode) {
		return this.operations.retrivePermisionsByGroupCode(userGropupCode);
	}

	public List<Permission> retrievePermissionsByUsername(String currentUserName) {
		return this.operations.retrievePermissionsByCurrentLoggedInUser(currentUserName);
	}

	public Permission retrievePermissionById(Integer id) {
		return this.operations.retrievePermissionById(id);
	}

	public Permission retrievePermissionByName(String name) {
		return this.operations.retrievePermissionByName(name);
	}

	public Permission insertPermission(Permission permission) {
		return this.operations.insertPermission(permission);
	}

	public Permission updatePermission(Permission model) {
		return this.operations.updatePermission(model);
	}

	public Boolean deletePermission(Integer id) {
		return this.operations.deletePermission(id);
	}

	public List<Permission> retrieveAllPermissions() {
		return this.operations.retrieveAllPermissions();
	}

	public boolean exists(int id) {
		return this.operations.exists(id);
	}

}
