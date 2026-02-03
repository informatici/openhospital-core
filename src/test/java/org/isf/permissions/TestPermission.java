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

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.junit.jupiter.api.Test;

class TestPermission extends OHCoreTestCase {

	@Test
	void testModel() throws Exception {
		Permission permission = new Permission();

		permission.setId(-1);
		assertThat(permission.getId()).isEqualTo(-1);

		permission.setDescription("description");
		assertThat(permission.getDescription()).isEqualTo("description");

		permission.setName("name");
		assertThat(permission.getName()).isEqualTo("name");

		List<GroupPermission> groupPermissions = new ArrayList<>();
		groupPermissions.add(new GroupPermission());
		permission.setGroupPermission(groupPermissions);
		assertThat(permission.getGroupPermission()).hasSize(1);
	}
}
