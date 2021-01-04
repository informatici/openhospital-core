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

}
