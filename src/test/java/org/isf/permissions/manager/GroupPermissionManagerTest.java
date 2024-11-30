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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.isf.OHCoreTestCase;
import org.isf.menu.TestPermission;
import org.isf.menu.TestUserGroup;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupPermissionManagerTest extends OHCoreTestCase {

	@Autowired
	private GroupPermissionManager groupPermissionManager;

	@Autowired
	private PermissionManager permissionManager;

	@Autowired
	private UserBrowsingManager userBrowsingManager;

	private GroupPermission groupPermission;

	void setUpDependencies() throws OHException, OHServiceException {
		Permission permission = permissionManager.save(TestPermission.generatePermission());
		UserGroup userGroup = userBrowsingManager.newUserGroup(new TestUserGroup().setup(false));
		groupPermission = new GroupPermission();
		groupPermission.setPermission(permission);
		groupPermission.setUserGroup(userGroup);
	}

	@BeforeEach
	void setUp() throws OHException, OHServiceException {
		cleanH2InMemoryDb();
		setUpDependencies();
	}

	@Test
	@DisplayName("Assign a permission to a user group")
	void assignPermissionToUserGroup() throws OHDataValidationException {
		GroupPermission createdGroupPermission = groupPermissionManager.create(groupPermission.getUserGroup(), groupPermission.getPermission());
		GroupPermission foundPermission = groupPermissionManager.findById(createdGroupPermission.getId());
		assertThat(foundPermission).isNotNull();
		assertThat(foundPermission.getPermission().getName()).isEqualTo(groupPermission.getPermission().getName());
		assertThat(foundPermission.getUserGroup().getCode()).isEqualTo(groupPermission.getUserGroup().getCode());
	}

	@Test
	@DisplayName("Assign already assigned permission to a user group")
	void assignAlreadyAssignedPermissionToUserGroup() throws OHDataValidationException {
		groupPermissionManager.create(groupPermission.getUserGroup(), groupPermission.getPermission());
		assertThatThrownBy(() -> {
			groupPermissionManager.create(groupPermission.getUserGroup(), groupPermission.getPermission());
		}).isInstanceOf(OHDataValidationException.class);
	}

	@Test
	@DisplayName("Remove a permission from a user group")
	void removePermissionFromUserGroup() throws OHDataValidationException {
		GroupPermission createdGroupPermission = groupPermissionManager.create(groupPermission.getUserGroup(), groupPermission.getPermission());
		groupPermissionManager.delete(groupPermission.getUserGroup(), groupPermission.getPermission());
		GroupPermission foundPermission = groupPermissionManager.findById(createdGroupPermission.getId());
		assertThat(foundPermission).isNull();
	}

	@Test
	@DisplayName("Remove not assigned permission from a user group")
	void removeNotAssignedPermissionFromUserGroup() throws OHDataValidationException {
		assertThatThrownBy(() -> {
			groupPermissionManager.delete(groupPermission.getUserGroup(), groupPermission.getPermission());
		}).isInstanceOf(OHDataValidationException.class);
	}
}
