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
package org.isf.menu;

import java.util.ArrayList;

import org.isf.permissions.model.Permission;

public class TestPermission {
	public static ArrayList<Permission> generatePermissions(int nbPermissions) {
		ArrayList<Permission> permissions = new ArrayList<>();

		for (int i = 1; i <= nbPermissions; i++) {
			Permission permission = new Permission();
			permission.setId(i);
			permission.setName("permission" + i);
			permission.setDescription("permission " + i + " description");
			permissions.add(permission);
		}

		return permissions;
	}

	public static Permission generatePermission() {
		Permission permission = new Permission();
		permission.setId(1);
		permission.setName("permission.test");
		permission.setDescription("Test permission");

		return permission;
	}
}
