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
package org.isf.permissions.service;

import org.isf.permissions.model.Permission;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PermissionIoOperations {

	private final PermissionIoOperationRepository repository;

	public PermissionIoOperations(PermissionIoOperationRepository permissionIoOperationRepository) {
		this.repository = permissionIoOperationRepository;
	}

	public List<Permission> retrivePermisionsByGroupCode(String userGroupCode) {
		return repository.findAllByUserGroupCode(userGroupCode);
	}

	public List<Permission> retrievePermissionsByCurrentLoggedInUser(String currentUserName) {
		return repository.retrievePermissionsByCurrentLoggedInUser(currentUserName);
	}

	public Permission retrievePermissionById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	public Permission retrievePermissionByName(String name) {
		return repository.findByName(name);
	}

	public List<Permission> retrieveAllPermissions() {
		return repository.findAll();
	}

	public boolean exists(int id) throws OHServiceException {
		return repository.existsById(id);
	}
}
