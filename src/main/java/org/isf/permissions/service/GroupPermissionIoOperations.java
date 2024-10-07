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

import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class GroupPermissionIoOperations {

	private final GroupPermissionIoOperationRepository repository;

	public GroupPermissionIoOperations(GroupPermissionIoOperationRepository groupPermissionIoOperationRepository) {
		this.repository = groupPermissionIoOperationRepository;
	}

	public List<GroupPermission> findByIdIn(List<Integer> ids) throws OHServiceException {
		return repository.findByIdIn(ids);
	}

	public void deleteUserGroupPermissions(UserGroup userGroup) {
		repository.deleteAllByUserGroup_Code(userGroup.getCode());
	}

	public List<GroupPermission> findUserGroupPermissions(String groupCode) {
		return repository.findAllByUserGroup_Code(groupCode);
	}

	public List<GroupPermission> saveAll(List<GroupPermission> groupPermissions) {
		return repository.saveAll(groupPermissions);
	}

	public GroupPermission create(GroupPermission groupPermission) {
		return repository.save(groupPermission);
	}

	public List<GroupPermission> createAll(List<GroupPermission> groupPermissions) {
		return repository.saveAll(groupPermissions);
	}

	public void delete(GroupPermission groupPermission) {
		repository.delete(groupPermission);
	}

	public void deleteAll(List<GroupPermission> groupPermissions) {
		repository.deleteAll(groupPermissions);
	}

	public boolean existsByUserGroupCodeAndPermissionId(String groupCode, int permissionId) {
		return repository.existsByUserGroupCodeAndPermissionId(groupCode, permissionId);
	}

	public GroupPermission findByUserGroupCodeAndPermissionId(String groupCode, int permissionId) {
		return repository.findFirstByUserGroupCodeAndPermissionId(groupCode, permissionId);
	}

	public GroupPermission findById(int id) {
		return repository.findById(id).orElse(null);
	}
}
