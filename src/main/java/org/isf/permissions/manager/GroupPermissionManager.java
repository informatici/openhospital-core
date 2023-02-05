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
