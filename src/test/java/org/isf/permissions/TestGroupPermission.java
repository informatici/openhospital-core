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

import org.isf.OHCoreTestCase;
import org.isf.menu.model.UserGroup;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.junit.jupiter.api.Test;

class TestGroupPermission extends OHCoreTestCase {

	@Test
	void testModel() throws Exception {
		GroupPermission groupPermission = new GroupPermission();

		groupPermission.setId(-1);
		assertThat(groupPermission.getId()).isEqualTo(-1);

		Permission permission = new Permission();
		permission.setId(-1);
		groupPermission.setPermission(permission);
		assertThat(groupPermission.getPermission().getId()).isEqualTo(-1);

		UserGroup userGroup = new UserGroup();
		userGroup.setCode("code");
		groupPermission.setUserGroup(userGroup);
		assertThat(groupPermission.getUserGroup().getCode()).isEqualTo("code");
	}
}
