package org.isf.menu;

import org.isf.permissions.model.Permission;

import java.util.ArrayList;

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
