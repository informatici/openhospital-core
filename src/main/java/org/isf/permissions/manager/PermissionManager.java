package org.isf.permissions.manager;

import java.util.List;

import org.isf.permissions.model.Permission;
import org.isf.permissions.service.PermissionIoOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PermissionManager {

	@Autowired
	private PermissionIoOperations operations;

	public List<Permission> retrivePermisionsByGroupCode(String userGropupCode) {
		return this.operations.retrivePermisionsByGroupCode(userGropupCode);
	}

	public List<Permission> retrievePermissionsByUsername(String currentUserName) {
		return this.operations.retrievePermissionsByCurrentLoggedInUser(currentUserName);
	}

	public Permission retrievePermissionById(Integer id) {
		return this.operations.retrievePermissionById(id);
	}

	public Permission retrievePermissionByName(String name) {
		return this.operations.retrievePermissionByName(name);
	}

	public Permission insertPermission(Permission permission) {
		return this.operations.insertPermission(permission);
	}

	public Permission updatePermission(Permission model) {
		return this.operations.updatePermission(model);
	}

	public Boolean deletePermission(Integer id) {
		return this.operations.deletePermission(id);
	}

	public List<Permission> retrieveAllPermissions() {
		return this.operations.retrieveAllPermissions();
	}

	public boolean exists(int id) {
		return this.operations.exists(id);
	}

}
