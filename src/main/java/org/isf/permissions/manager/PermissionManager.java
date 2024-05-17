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
package org.isf.permissions.manager;

import java.util.List;

import org.isf.permissions.model.Permission;
import org.isf.permissions.service.PermissionIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class PermissionManager {

	private PermissionIoOperations operations;

	public PermissionManager(PermissionIoOperations permissionIoOperations) {
		this.operations = permissionIoOperations;
	}

	public List<Permission> retrievePermissionsByGroupCode(String userGroupCode) throws OHServiceException {
		return operations.retrivePermisionsByGroupCode(userGroupCode);
	}

	public List<Permission> retrievePermissionsByUsername(String currentUserName) throws OHServiceException {
		return operations.retrievePermissionsByCurrentLoggedInUser(currentUserName);
	}

	public Permission retrievePermissionById(Integer id) throws OHServiceException {
		return operations.retrievePermissionById(id);
	}

	public Permission retrievePermissionByName(String name) throws OHServiceException {
		return operations.retrievePermissionByName(name);
	}

	public Permission insertPermission(Permission permission) throws OHServiceException {
		return operations.insertPermission(permission);
	}

	public Permission updatePermission(Permission model) throws OHServiceException {
		return operations.updatePermission(model);
	}

	public void deletePermission(Integer id) throws OHServiceException {
		operations.deletePermission(id);
	}

	public List<Permission> retrieveAllPermissions() throws OHServiceException {
		return operations.retrieveAllPermissions();
	}

	public boolean exists(int id) throws OHServiceException {
		return operations.exists(id);
	}

}
