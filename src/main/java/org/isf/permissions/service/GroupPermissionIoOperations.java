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
package org.isf.permissions.service;

import java.util.List;
import java.util.stream.Collectors;

import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = OHServiceException.class)
@TranslateOHServiceException
public class GroupPermissionIoOperations {

	@Autowired
	private GroupPermissionIoOperationRepository repository;

	public List<GroupPermission> findByIdIn(List<Integer> ids) {
		return this.repository.findByIdIn(ids);
	}

	public List<GroupPermission> generateGroupPermissionList(Permission model, List<UserGroup> userGroups) {
		return userGroups.stream().map(ug -> {
			GroupPermission gp = new GroupPermission();
			gp.setPermission(model);
			gp.setUserGroup(ug);
			gp.setActive(1);
			return gp;
		}).collect(Collectors.toList());
	}

	public List<GroupPermission> findByPermissionIdAndUserGroupCodes(Integer permissionId, List<String> userGroupCodes) {
		return this.repository.findByPermission_IdAndUserGroup_CodeIn(permissionId, userGroupCodes);
	}

}
