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

import java.util.List;
import java.util.stream.Collectors;

import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class PermissionIoOperations {

	private PermissionIoOperationRepository repository;

	private GroupPermissionIoOperationRepository groupPermissionRepository;

	public PermissionIoOperations(PermissionIoOperationRepository permissionIoOperationRepository, GroupPermissionIoOperationRepository groupPermissionIoOperationRepository) {
		this.repository = permissionIoOperationRepository;
		this.groupPermissionRepository = groupPermissionIoOperationRepository;
	}

	public List<Permission> retrivePermisionsByGroupCode(String userGropupCode) throws OHServiceException {
		return repository.findAllByUserGroupCode(userGropupCode);
	}

	public List<Permission> retrievePermissionsByCurrentLoggedInUser(String currentUserName) throws OHServiceException {
		return repository.retrievePermissionsByCurrentLoggedInUser(currentUserName);
	}

	public Permission retrievePermissionById(Integer id) throws OHServiceException {
		return repository.findById(id).orElse(null);
	}

	public Permission retrievePermissionByName(String name) throws OHServiceException {
		return repository.findByName(name);
	}

	public Permission insertPermission(Permission permission) throws OHServiceException {
		Permission permissionResult = repository.save(permission);
		permission.getGroupPermission().forEach(gp -> {
			gp.setPermission(permissionResult);
			groupPermissionRepository.save(gp);
		});
		return permissionResult;
	}

	public Permission updatePermission(Permission permission) throws OHServiceException {
		// All group permissions (could exist already in the DB)
		List<GroupPermission> gp = permission.getGroupPermission();

		Permission permissionUpdated = repository.save(permission);
		// retrieve groupPermission stored in DB
		List<GroupPermission> groupPermissionInDB = groupPermissionRepository.findByPermission_id(permission.getId());

		// calculate GroupPermission to delete
		List<String> allUserGroupCodesToStore = gp.stream().map(item -> item.getUserGroup().getCode()).collect(Collectors.toList());
		List<String> allUserGroupCodesOnDB = groupPermissionInDB.stream().map(item -> item.getUserGroup().getCode()).collect(Collectors.toList());

		List<String> allUserGroupCodesToDelete = allUserGroupCodesOnDB.stream().filter(onDB -> !allUserGroupCodesToStore.contains(onDB)).collect(Collectors.toList());
		List<String> allUserGroupCodesToInsert = allUserGroupCodesToStore.stream().filter(item -> !allUserGroupCodesOnDB.contains(item)).collect(Collectors.toList());

		// delete obsolete relations
		List<GroupPermission> groupPermissionToDelete = groupPermissionRepository.findByUserGroup_codeInAndPermission_id(allUserGroupCodesToDelete, permission.getId());
		groupPermissionRepository.deleteAll(groupPermissionToDelete);

		// store new relations
		gp.forEach(item -> {
			if (allUserGroupCodesToInsert.contains(item.getUserGroup().getCode())) {
				item.setPermission(permissionUpdated);
				groupPermissionRepository.save(item);
			}
		});
		return repository.getReferenceById(permissionUpdated.getId());
	}

	public void deletePermission(Integer id) throws OHServiceException {
		repository.deleteById(id);
	}

	public List<Permission> retrieveAllPermissions() throws OHServiceException {
		return repository.findAll();
	}

	public boolean exists(int id) throws OHServiceException {
		return repository.existsById(id);
	}

}
