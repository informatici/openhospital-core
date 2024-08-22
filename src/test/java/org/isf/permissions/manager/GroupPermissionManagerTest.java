package org.isf.permissions.manager;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GroupPermissionManagerTest extends OHCoreTestCase {

	@Autowired
	private GroupPermissionManager groupPermissionManager;

	@Autowired
	private PermissionManager permissionManager;

	@Autowired
	private UserBrowsingManager userBrowsingManager;

	private static GroupPermission groupPermission;

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
