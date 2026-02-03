/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.permissions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.permissions.manager.PermissionManager;
import org.isf.permissions.model.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestPermissionManager extends OHCoreTestCase {

	@Autowired
	PermissionManager permissionManager;

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
		executeSQLScript("LoadPermissionTables.sql");
	}

	@Test
	void testRetrieveByGroupCode() throws Exception {
		List<Permission> permissions = permissionManager.retrievePermissionsByGroupCode("doctor");
		assertThat(permissions).isNotEmpty();
		assertThat(permissions.get(0).getName()).isEqualTo("admissions.create");
		assertThat(permissions.get(0).getDescription()).isEmpty();
	}

	@Test
	void testRetrieveByUsername() throws Exception {
		List<Permission> permissions = permissionManager.retrievePermissionsByUsername("admin");
		assertThat(permissions).isNotEmpty();
		assertThat(permissions.get(0).getName()).isEqualTo("admissions.create");
		assertThat(permissions.get(0).getDescription()).isEmpty();
	}

	@Test
	void testRetrieveById() throws Exception {
		Permission permission = permissionManager.retrievePermissionById(3);
		assertThat(permission).isNotNull();
		assertThat(permission.getName()).isEqualTo("admissions.update");
		assertThat(permission.getDescription()).isEmpty();
		assertThat(permission.getId()).isEqualTo(3);
		assertThat(permission.getGroupPermission()).isNotNull();
	}

	@Test
	void testRetrieveByName() throws Exception {
		Permission permission = permissionManager.retrievePermissionByName("admissions.create");
		assertThat(permission).isNotNull();
		assertThat(permission.getName()).isEqualTo("admissions.create");
		assertThat(permission.getDescription()).isEmpty();
	}

	@Test
	void testRetrieveAllPermissions() throws Exception {
		List<Permission> permissions = permissionManager.retrieveAllPermissions();
		assertThat(permissions).isNotEmpty();
		assertThat(permissions.get(0).getName()).isEqualTo("admissions.create");
		assertThat(permissions.get(0).getDescription()).isEmpty();

		assertThat(permissions).hasSize(30);
	}

	@Test
	void testExists() throws Exception {
		Permission permission = permissionManager.retrievePermissionById(3);
		assertThat(permission).isNotNull();
		assertThat(permissionManager.exists(5)).isTrue();
		assertThat(permissionManager.exists(-999)).isFalse();
	}
}
