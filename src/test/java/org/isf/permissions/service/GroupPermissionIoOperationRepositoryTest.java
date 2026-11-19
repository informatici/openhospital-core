/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.TestPermission;
import org.isf.menu.model.UserGroup;
import org.isf.menu.service.MenuIoOperations;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class GroupPermissionIoOperationRepositoryTest extends OHCoreTestCase {

	@Autowired
	private MenuIoOperations menuIoOperation;
	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;
	@Autowired
	private PermissionIoOperationRepository permissionIoOperationRepository;
	@Autowired
	private GroupPermissionIoOperationRepository groupPermissionIoOperationRepository;
	@Autowired
	private GroupPermissionManager groupPermissionManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void saveAndFlush_shouldThrowDataIntegrityViolation_whenGroupPermissionPairExists() throws Exception {
		String code = setupTestUserGroupPermissions();
		UserGroup userGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(userGroup).isNotNull();
		GroupPermission existing = groupPermissionManager.findUserGroupPermissions(code).get(0);

		GroupPermission duplicate = new GroupPermission();
		duplicate.setUserGroup(userGroup);
		duplicate.setPermission(existing.getPermission());
		assertThatThrownBy(() -> groupPermissionIoOperationRepository.saveAndFlush(duplicate))
			.isInstanceOf(DataIntegrityViolationException.class);
	}

	private String setupTestUserGroupPermissions() throws OHServiceException {
		UserGroup userGroup = new UserGroup();
		userGroup.setCode("contrib");
		userGroup.setDesc("contributors group");
		List<Permission> permissions = TestPermission.generatePermissions(4);
		List<Permission> savedPermissions = permissionIoOperationRepository.saveAllAndFlush(permissions);
		UserGroup newUserGroup = menuIoOperation.newUserGroup(userGroup, savedPermissions);
		return userGroup.getCode();
	}

}
