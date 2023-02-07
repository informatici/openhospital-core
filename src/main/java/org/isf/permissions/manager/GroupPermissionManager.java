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

import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.permissions.service.GroupPermissionIoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupPermissionManager {

	@Autowired
	private GroupPermissionIoOperations operations;

	public List<GroupPermission> findByIdIn(List<Integer> ids) {
		return this.operations.findByIdIn(ids);
	}

	public List<GroupPermission> findByPermissionIdAndUserGroupCodes(Integer permissionId, List<String> userGroupCodes) {
		return this.operations.findByPermissionIdAndUserGroupCodes(permissionId, userGroupCodes);
	}

	public List<GroupPermission> generateGroupPermissionList(Permission model, List<UserGroup> userGroups) {
		return this.operations.generateGroupPermissionList(model, userGroups);
	}

}
