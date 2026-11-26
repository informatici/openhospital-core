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
package org.isf.menu.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.TestPermission;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.TestUserMenu;
import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.permissions.manager.GroupPermissionManager;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.permissions.service.PermissionIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MenuIoOperationsTest extends OHCoreTestCase {

	private static TestUser testUser;
	private static TestUserGroup testUserGroup;
	private static TestUserMenu testUserMenu;

	@Autowired
	private MenuIoOperations menuIoOperation;
	@Autowired
	private GroupMenuIoOperationRepository groupMenuIoOperationRepository;
	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;
	@Autowired
	private UserIoOperationRepository userIoOperationRepository;
	@Autowired
	private UserMenuItemIoOperationRepository userMenuItemIoOperationRepository;
	@Autowired
	private PermissionIoOperationRepository permissionIoOperationRepository;
	@Autowired
	private GroupPermissionManager groupPermissionManager;

	@BeforeAll
	static void setUpClass() {
		testUser = new TestUser();
		testUserGroup = new TestUserGroup();
		testUserMenu = new TestUserMenu();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getUser_shouldReturnSavedUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		List<User> users = menuIoOperation.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUser_shouldReturnUsersOfGroup_whenGroupIdIsGiven() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		List<User> users = menuIoOperation.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUserByName_shouldReturnSavedUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		User user = menuIoOperation.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUsrInfo_shouldReturnUserDescription() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		String description = menuIoOperation.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUserGroup_shouldReturnSavedUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		List<UserGroup> userGroups = menuIoOperation.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	void isUserNamePresent_shouldReturnTrue_whenUserExists() throws Exception {
		String userName = setupTestUser(false);
		assertThat(menuIoOperation.isUserNamePresent(userName)).isTrue();
	}

	@Test
	void isGroupNamePresent_shouldReturnTrue_whenGroupExists() throws Exception {
		String code = setupTestUserGroup(false);
		assertThat(menuIoOperation.isGroupNamePresent(code)).isTrue();
	}

	@Test
	void newUser_shouldSaveUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		User newUser = menuIoOperation.newUser(user);
		assertThat(menuIoOperation.getUsrInfo(newUser.getUserName())).isEqualTo(user.getDesc());
		checkUserIntoDb(newUser.getUserName());
	}

	@Test
	void updateUser_shouldSaveNewDescription() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		foundUser.setDesc("Update");
		assertThat(menuIoOperation.updateUser(foundUser).getUserName()).isEqualTo(foundUser.getUserName());
		User updatedUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getDesc()).isEqualTo("Update");
	}

	@Test
	void updatePassword_shouldSaveNewPassword() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		foundUser.setPasswd("Update");
		assertThat(menuIoOperation.updatePassword(foundUser).getUserName()).isEqualTo(foundUser.getUserName());
		User updatedUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getPasswd()).isEqualTo("Update");
	}

	@Test
	void deleteUser_shouldNotThrow_whenUserExists() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		menuIoOperation.deleteUser(foundUser);
		List<User> users = menuIoOperation.getUser(userName);
		assertThat(users).isEmpty();
	}

	@Test
	void getMenu_shouldReturnMenuItemsOfUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		userMenuItemIoOperationRepository.saveAndFlush(menuItem);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		List<UserMenuItem> menus = menuIoOperation.getMenu(user);
		assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
	}

	@Test
	void getGroupMenu_shouldReturnMenuItemsOfGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		userMenuItemIoOperationRepository.saveAndFlush(menuItem);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		List<UserMenuItem> menus = menuIoOperation.getGroupMenu(userGroup);
		assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
	}

	@Test
	void setGroupMenu_shouldReturnTrue() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		List<UserMenuItem> userMenuItems = new ArrayList<>();
		userMenuItems.add(menuItem);
		assertThat(menuIoOperation.setGroupMenu(userGroup, userMenuItems)).isTrue();
	}

	@Test
	void deleteGroup_shouldRemoveUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		menuIoOperation.deleteGroup(foundUserGroup);
		assertThat(menuIoOperation.findByCode(foundUserGroup.getCode())).isNull();
		foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNull();
	}

	@Test
	void newUserGroup_shouldSaveUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserGroup newUserGroup = menuIoOperation.newUserGroup(userGroup);
		checkUserGroupIntoDb(newUserGroup.getCode());
	}

	@Test
	void newUserGroup_shouldSaveUserGroupWithPermissions() throws Exception {
		String code = setupTestUserGroupPermissions();
		UserGroup userGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		List<GroupPermission> groupPermissions = groupPermissionManager.findUserGroupPermissions(userGroup.getCode());
		List<Permission> permissions = groupPermissions.stream().map(GroupPermission::getPermission).toList();
		checkUserGroupAndPermissionsIntoDb(userGroup, permissions);
	}

	@Test
	void updateUserGroup_shouldSaveNewDescriptionAndPermissions() throws Exception {
		String code = setupTestUserGroupPermissions();
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		List<GroupPermission> groupPermissions = groupPermissionManager.findUserGroupPermissions(foundUserGroup.getCode());
		ArrayList<Permission> permissions = new ArrayList<>(groupPermissions.stream().map(GroupPermission::getPermission).toList().subList(0, 2));
		Permission permission = permissions.get(0);
		permission.setName("updated.permission");
		permission.setDescription("Updated permission");
		permissions.set(0, permission);
		foundUserGroup.setDesc("Update");
		assertThat(menuIoOperation.updateUserGroup(foundUserGroup, permissions).getCode()).isEqualTo(foundUserGroup.getCode());
		checkUserGroupAndPermissionsIntoDb(foundUserGroup, permissions);
	}

	@Test
	void updateUserGroup_shouldSaveNewDescription() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		foundUserGroup.setDesc("Update");
		assertThat(menuIoOperation.updateUserGroup(foundUserGroup).getCode()).isEqualTo(foundUserGroup.getCode());
		UserGroup updatedUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedUserGroup).isNotNull();
		assertThat(updatedUserGroup.getDesc()).isEqualTo("Update");
	}

	@Test
	void updateUser_shouldSetDeletedDateOnlyOnSoftDeletion() throws Exception {
		String userName = setupTestUser(false);
		User user = userIoOperationRepository.findByUserName(userName);
		assertThat(user.getDeletedDate()).isNull();

		// a regular update (deleted == false) must NOT stamp the deletion audit
		user.setDesc("changed");
		menuIoOperation.updateUser(user);
		assertThat(userIoOperationRepository.findByUserName(userName).getDeletedDate()).isNull();

		// a soft deletion records when the user was deleted
		user.setDeleted(true);
		menuIoOperation.updateUser(user);
		entityManager.clear(); // the bulk update bypasses the context; drop the cached entity to read fresh
		User afterDelete = userIoOperationRepository.findByUserName(userName);
		assertThat(afterDelete.isDeleted()).isTrue();
		assertThat(afterDelete.getDeletedDate()).isNotNull();
	}

	@Test
	void updateUserGroup_shouldSetDeletedDate_whenGroupIsSoftDeleted() throws Exception {
		String userName = setupTestUser(false);
		User user = userIoOperationRepository.findByUserName(userName);
		UserGroup group = userGroupIoOperationRepository.findByCode(user.getUserGroupName().getCode());
		assertThat(group.getDeletedDate()).isNull();

		group.setDeleted(true);
		menuIoOperation.updateUserGroup(group);
		entityManager.clear(); // the bulk update bypasses the context; drop the cached entity to read fresh
		UserGroup afterDelete = userGroupIoOperationRepository.findByCode(group.getCode());
		assertThat(afterDelete.isDeleted()).isTrue();
		assertThat(afterDelete.getDeletedDate()).isNotNull();
	}

	private String setupTestUserGroup(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		return userGroup.getCode();
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

	private void checkUserGroupAndPermissionsIntoDb(UserGroup userGroup, List<Permission> permissions) {
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(userGroup.getCode()).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		assertThat(foundUserGroup.getCode()).isEqualTo(userGroup.getCode());
		assertThat(foundUserGroup.getDesc()).isEqualTo(userGroup.getDesc());

		List<GroupPermission> groupPermissions = groupPermissionManager.findUserGroupPermissions(userGroup.getCode());
		List<Permission> dbGroupPermissions = groupPermissions.stream().map(GroupPermission::getPermission).toList();

		assertThat(permissions.size()).isEqualTo(dbGroupPermissions.size());
		assertThat(permissions.get(0)).isEqualTo(dbGroupPermissions.get(0));
	}

	private void checkUserGroupIntoDb(String code) throws OHException {
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		testUserGroup.check(foundUserGroup);
	}

	private String setupTestUser(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		User user = testUser.setup(userGroup, usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		return user.getUserName();
	}

	private void checkUserIntoDb(String code) throws OHException {
		User foundUser = userIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUser).isNotNull();
		testUser.check(foundUser);
	}

}
