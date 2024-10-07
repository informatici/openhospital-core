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

import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.permissions.service.GroupPermissionIoOperations;
import org.isf.permissions.service.PermissionIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GroupPermissionManager {

	private final GroupPermissionIoOperations operations;

	private final PermissionIoOperations permissionOperations;

	public GroupPermissionManager(GroupPermissionIoOperations groupPermissionIoOperations, PermissionIoOperations permissionIoOperations) {
		this.operations = groupPermissionIoOperations;
		this.permissionOperations = permissionIoOperations;
	}

	public List<GroupPermission> findByIdIn(List<Integer> ids) throws OHServiceException {
		return operations.findByIdIn(ids);
	}

	public void deleteUserGroupPermissions(UserGroup userGroup) {
		operations.deleteUserGroupPermissions(userGroup);
	}

	public List<GroupPermission> findUserGroupPermissions(String groupCode) {
		return operations.findUserGroupPermissions(groupCode);
	}

	public GroupPermission findById(int id) {
		return operations.findById(id);
	}

	public GroupPermission create(UserGroup userGroup, Permission permission) throws OHDataValidationException {
		if (operations.existsByUserGroupCodeAndPermissionId(userGroup.getCode(), permission.getId())) {
			throw new OHDataValidationException(
				new OHExceptionMessage(MessageBundle.getMessage("usergroup.permissionalreadyassigned"))
			);
		}

		GroupPermission groupPermission = new GroupPermission();
		groupPermission.setPermission(permission);
		groupPermission.setUserGroup(userGroup);

		return operations.create(groupPermission);
	}

	@Transactional
	public List<Permission> update(UserGroup userGroup, List<Integer> permissionIds, Boolean replace) throws OHDataValidationException {

		List<Permission> permissions = permissionOperations.findByIdIn(permissionIds).stream().toList();

		List<GroupPermission> groupPermissions = operations.findUserGroupPermissions(userGroup.getCode()).stream().toList();

		List<GroupPermission> permissionsToAssign = permissions.stream()
			.filter(item -> groupPermissions.stream().noneMatch(groupPermission -> groupPermission.getPermission().getId() == item.getId())).map(
				permission -> new GroupPermission(userGroup, permission)
			).toList();
		operations.createAll(permissionsToAssign);

		if (replace) {
			List<GroupPermission> permissionsToRemove = groupPermissions.stream()
				.filter(item -> permissions.stream().noneMatch(permission -> permission.getId() == item.getPermission().getId())).toList();
			operations.deleteAll(permissionsToRemove);
		}

		return operations.findUserGroupPermissions(userGroup.getCode()).stream().map(GroupPermission::getPermission).toList();
	}

	public void delete(UserGroup userGroup, Permission permission) throws OHDataValidationException {
		GroupPermission groupPermission = operations.findByUserGroupCodeAndPermissionId(userGroup.getCode(), permission.getId());

		if (groupPermission == null) {
			throw new OHDataValidationException(
				new OHExceptionMessage(MessageBundle.getMessage("usergroup.permissionnotassigned"))
			);
		}

		operations.delete(groupPermission);
	}
}
