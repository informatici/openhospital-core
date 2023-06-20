/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.menu.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.GroupMenuIoOperationRepository;
import org.isf.menu.service.MenuIoOperations;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.menu.service.UserMenuItemIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestUser testUser;
	private static TestUserGroup testUserGroup;
	private static TestUserMenu testUserMenu;
	private static TestGroupMenu testGroupMenu;

	@Autowired
	MenuIoOperations menuIoOperation;
	@Autowired
	UserBrowsingManager userBrowsingManager;
	@Autowired
	GroupMenuIoOperationRepository groupMenuIoOperationRepository;
	@Autowired
	UserGroupIoOperationRepository userGroupIoOperationRepository;
	@Autowired
	UserIoOperationRepository userIoOperationRepository;
	@Autowired
	UserMenuItemIoOperationRepository userMenuItemIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testUser = new TestUser();
		testUserGroup = new TestUserGroup();
		testUserMenu = new TestUserMenu();
		testGroupMenu = new TestGroupMenu();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testUserGroupGets() throws Exception {
		String code = setupTestUserGroup(false);
		checkUserGroupIntoDb(code);
	}

	@Test
	public void testUserGroupSets() throws Exception {
		String code = setupTestUserGroup(true);
		checkUserGroupIntoDb(code);
	}

	@Test
	public void testUserGets() throws Exception {
		String userName = setupTestUser(false);
		checkUserIntoDb(userName);
	}

	@Test
	public void testUserSets() throws Exception {
		String userName = setupTestUser(true);
		checkUserIntoDb(userName);
	}

	@Test
	public void testUserMenuGets() throws Exception {
		String code = setupTestUserMenu(false);
		checkUserMenuIntoDb(code);
	}

	@Test
	public void testUserMenuSets() throws Exception {
		String code = setupTestUserMenu(true);
		checkUserMenuIntoDb(code);
	}

	@Test
	public void testGroupMenuGets() throws Exception {
		Integer code = setupTestGroupMenu(false);
		checkGroupMenuIntoDb(code);
	}

	@Test
	public void testGroupMenuSets() throws Exception {
		Integer code = setupTestGroupMenu(true);
		checkGroupMenuIntoDb(code);
	}

	@Test
	public void testIoGetUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		List<User> users = menuIoOperation.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUsersFromGroupId() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		List<User> users = menuIoOperation.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserByName() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		User user = menuIoOperation.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserInfo() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		String description = menuIoOperation.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		List<UserGroup> userGroups = menuIoOperation.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	public void testIoIsUserNamePresent() throws Exception {
		String userName = setupTestUser(false);
		assertThat(menuIoOperation.isUserNamePresent(userName)).isTrue();
	}

	@Test
	public void testIoIsGroupNamePresent() throws Exception {
		String code = setupTestUserGroup(false);
		assertThat(menuIoOperation.isGroupNamePresent(code)).isTrue();
	}

	@Test
	public void testIoNewUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		// TODO: this illustrates that if the user exists the newUser() method still succeeds; probably not what is expected
		assertThat(menuIoOperation.newUser(user)).isTrue();
		checkUserIntoDb(user.getUserName());
	}

	@Test
	public void testIoUpdateUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		foundUser.setDesc("Update");
		assertThat(menuIoOperation.updateUser(foundUser)).isTrue();
		User updateUser = userIoOperationRepository.findById(userName).get();
		assertThat(updateUser.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testIoUpdatePassword() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		foundUser.setPasswd("Update");
		assertThat(menuIoOperation.updatePassword(foundUser)).isTrue();
		User updateDisease = userIoOperationRepository.findById(userName).get();
		assertThat(updateDisease.getPasswd()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		assertThat(menuIoOperation.deleteUser(foundUser)).isTrue();
		List<User> users = menuIoOperation.getUser(userName);
		assertThat(users).isEmpty();
	}

	@Test
	public void testIoGetMenu() throws Exception {
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
	public void testIoGetGroupMenu() throws Exception {
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
	public void testIoSetGroupMenu() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		ArrayList<UserMenuItem> userMenuItems = new ArrayList<>();
		userMenuItems.add(menuItem);
		assertThat(menuIoOperation.setGroupMenu(userGroup, userMenuItems, true)).isTrue();
	}

	@Test
	public void testIoDeleteGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		assertThat(menuIoOperation.deleteGroup(foundUserGroup)).isTrue();
		assertThat(menuIoOperation.isGroupNamePresent(foundUserGroup.getCode())).isFalse();
	}

	@Test
	public void testIoNewUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		assertThat(menuIoOperation.newUserGroup(userGroup)).isTrue();
		checkUserGroupIntoDb(userGroup.getCode());
	}

	@Test
	public void testIoUpdateUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		foundUserGroup.setDesc("Update");
		assertThat(menuIoOperation.updateUserGroup(foundUserGroup)).isTrue();
		UserGroup updateUserGroup = userGroupIoOperationRepository.findById(code).get();
		assertThat(updateUserGroup.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testMgrGetUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		List<User> users = userBrowsingManager.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUsersFromGroupId() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		List<User> users = userBrowsingManager.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUserByName() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUserInfo() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		String description = userBrowsingManager.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrDeleteGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		assertThat(userBrowsingManager.deleteGroup(foundUserGroup)).isTrue();
		assertThat(menuIoOperation.isGroupNamePresent(foundUserGroup.getCode())).isFalse();
	}

	@Test
	public void testMgrDeleteGroupAdminGroup() throws Exception {
		assertThatThrownBy(() ->
		{
			UserGroup userGroup = testUserGroup.setup(true);
			userGroup.setCode("admin");
			userGroupIoOperationRepository.saveAndFlush(userGroup);
			userBrowsingManager.deleteGroup(userGroup);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrDeleteGroupHasUsers() throws Exception {
		assertThatThrownBy(() ->
		{
			String userName = setupTestUser(true);
			User user = userIoOperationRepository.findById(userName).get();
			userBrowsingManager.deleteGroup(user.getUserGroupName());
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMgrGetUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		List<UserGroup> userGroups = userBrowsingManager.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	public void testMgrNewUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		assertThat(userBrowsingManager.newUser(user)).isTrue();
		checkUserIntoDb(user.getUserName());
	}

	@Test
	public void testMgrNewUserAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			UserGroup userGroup = testUserGroup.setup(false);
			User user = testUser.setup(userGroup, false);
			userGroupIoOperationRepository.saveAndFlush(userGroup);
			userIoOperationRepository.saveAndFlush(user);
			userBrowsingManager.newUser(user);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMgrUpdateUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		foundUser.setDesc("Update");
		assertThat(userBrowsingManager.updateUser(foundUser)).isTrue();
		User updateUser = userIoOperationRepository.findById(userName).get();
		assertThat(updateUser.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdatePassword() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		foundUser.setPasswd("Update");
		assertThat(userBrowsingManager.updatePassword(foundUser)).isTrue();
		User updateDisease = userIoOperationRepository.findById(userName).get();
		assertThat(updateDisease.getPasswd()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).get();
		assertThat(userBrowsingManager.deleteUser(foundUser)).isTrue();
		List<User> users = userBrowsingManager.getUser(userName);
		assertThat(users).isEmpty();
	}

	@Test
	public void testMgrDeleteAdminUser() throws Exception {
		assertThatThrownBy(() ->
		{
			String userName = setupTestUser(false);
			User foundUser = userIoOperationRepository.findById(userName).get();
			foundUser.setUserName("admin");
			userBrowsingManager.deleteUser(foundUser);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrGetMenu() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		userMenuItemIoOperationRepository.saveAndFlush(menuItem);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		List<UserMenuItem> menus = userBrowsingManager.getMenu(user);
		assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
	}

	@Test
	public void testMgrGetGroupMenu() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		userMenuItemIoOperationRepository.saveAndFlush(menuItem);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		List<UserMenuItem> menus = userBrowsingManager.getGroupMenu(userGroup);
		assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
	}

	@Test
	public void testMgrSetGroupMenu() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		ArrayList<UserMenuItem> userMenuItems = new ArrayList<>();
		userMenuItems.add(menuItem);
		assertThat(userBrowsingManager.setGroupMenu(userGroup, userMenuItems)).isTrue();
	}

	@Test
	public void testMgrNewUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		assertThat(userBrowsingManager.newUserGroup(userGroup)).isTrue();
		checkUserGroupIntoDb(userGroup.getCode());
	}

	@Test
	public void testMgrNewUserGroupAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			String code = setupTestUserGroup(true);
			UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
			userBrowsingManager.newUserGroup(foundUserGroup);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMgrUpdateUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
		foundUserGroup.setDesc("Update");
		assertThat(userBrowsingManager.updateUserGroup(foundUserGroup)).isTrue();
		UserGroup updateUserGroup = userGroupIoOperationRepository.findById(code).get();
		assertThat(updateUserGroup.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testGroupMenuSettersGetters() throws Exception {
		GroupMenu groupMenu = testGroupMenu.setup(true);

		Integer code = groupMenu.getCode();
		groupMenu.setCode(-1);
		assertThat(groupMenu.getCode()).isEqualTo(-1);
		groupMenu.setCode(code);

		int active = groupMenu.getActive();
		groupMenu.setActive(-1);
		assertThat(groupMenu.getActive()).isEqualTo(-1);
		groupMenu.setActive(active);
	}

	@Test
	public void testGroupMenuEquals() throws Exception {
		GroupMenu groupMenu = testGroupMenu.setup(true);
		groupMenu.setCode(1);

		assertThat(groupMenu)
				.isNotNull()
				.isNotEqualTo("aString");

		GroupMenu groupMenu1 = testGroupMenu.setup(false);
		groupMenu1.setCode(-1);
		assertThat(groupMenu).isNotEqualTo(groupMenu1);

		groupMenu1.setCode(groupMenu.getCode());
		groupMenu1.setUserGroup("someOtherGroup");
		assertThat(groupMenu).isNotEqualTo(groupMenu1);

		groupMenu1.setUserGroup(groupMenu.getUserGroup());
		groupMenu1.setMenuItem("someOtherMenuItem");
		assertThat(groupMenu).isNotEqualTo(groupMenu1);

		groupMenu1.setMenuItem(groupMenu.getMenuItem());
		groupMenu1.setActive(-1);
		assertThat(groupMenu).isNotEqualTo(groupMenu1);

		groupMenu1.setActive(groupMenu.getActive());
		assertThat(groupMenu).isEqualTo(groupMenu1);
	}

	@Test
	public void testUserToString() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);
		assertThat(user).hasToString(user.getUserName());
	}

	@Test
	public void testUserEquals() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);

		assertThat(user)
				.isNotNull()
				.isNotEqualTo("someString");

		User user1 = testUser.setup(userGroup, false);
		user1.setUserName("someOtherName");
		assertThat(user).isNotEqualTo(user1);

		user1.setUserName(user.getUserName());
		user1.setDesc("someOtherDescription");
		assertThat(user).isNotEqualTo(user1);

		user1.setDesc(user.getDesc().toLowerCase());
		assertThat(user).isEqualTo(user1);
	}

	@Test
	public void testUserHashCode() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);
		// compute value
		int hashCode = user.hashCode();
		// reuse value
		assertThat(user.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testUserGroupToString() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		userGroup.setCode("someCode");
		assertThat(userGroup).hasToString("someCode");
	}

	@Test
	public void testUserGroupEquals() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);

		assertThat(userGroup)
				.isNotNull()
				.isNotEqualTo("someString");

		UserGroup userGroup1 = testUserGroup.setup(false);

		userGroup.setCode("code1");
		userGroup1.setCode("code2");
		assertThat(userGroup).isNotEqualTo(userGroup1);

		userGroup1.setCode(userGroup.getCode());
		userGroup1.setDesc("someOtherDescription");
		assertThat(userGroup).isNotEqualTo(userGroup1);

		userGroup1.setDesc(userGroup.getDesc().toLowerCase());
		assertThat(userGroup).isEqualTo(userGroup1);
	}

	@Test
	public void testUserGroupHashCode() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		// compute value
		int hashCode = userGroup.hashCode();
		// reuse value
		assertThat(userGroup.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testUserMenuItemEquals() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(true);

		assertThat(userMenuItem)
				.isNotNull()
				.isNotEqualTo("someString");

		UserMenuItem userMenuItem1 = testUserMenu.setup(false);
		userMenuItem.setCode("code1");
		userMenuItem1.setCode("code2");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setCode(userMenuItem.getCode());
		userMenuItem1.setButtonLabel("someOtherButtonLabel");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setButtonLabel(userMenuItem.getButtonLabel());
		userMenuItem1.setAltLabel("someOtherLabel");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setAltLabel(userMenuItem.getAltLabel());
		userMenuItem1.setTooltip("someOtherToolTip");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setTooltip(userMenuItem.getTooltip());
		userMenuItem1.setShortcut('?');
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setShortcut(userMenuItem.getShortcut());
		userMenuItem1.setMySubmenu("someOtherSubMenu");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setMySubmenu(userMenuItem.getMySubmenu());
		userMenuItem1.setMyClass("someOtherClass");
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setMyClass(userMenuItem.getMyClass());
		userMenuItem1.setASubMenu(!userMenuItem.isASubMenu());
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setASubMenu(userMenuItem.isASubMenu());
		userMenuItem1.setPosition(-1);
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setPosition(userMenuItem.getPosition());
		userMenuItem1.setActive(!userMenuItem.isActive());
		assertThat(userMenuItem).isNotEqualTo(userMenuItem1);

		userMenuItem1.setActive(userMenuItem.isActive());
		assertThat(userMenuItem).isEqualTo(userMenuItem1);
	}

	@Test
	public void testUserMenuItemToString() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(true);
		assertThat(userMenuItem).hasToString(userMenuItem.getButtonLabel());
	}

	@Test
	public void testUserMenuItemHashCode() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(false);
		userMenuItem.setCode("someCode");
		// compute value
		int hashCode = userMenuItem.hashCode();
		// reuse value
		assertThat(userMenuItem.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestUserGroup(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		return userGroup.getCode();
	}

	private void checkUserGroupIntoDb(String code) throws OHException {
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).get();
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
		User foundUser = userIoOperationRepository.findById(code).get();
		testUser.check(foundUser);
	}

	private String setupTestUserMenu(boolean usingSet) throws OHException {
		UserMenuItem userMenu = testUserMenu.setup(usingSet);
		userMenuItemIoOperationRepository.saveAndFlush(userMenu);
		return userMenu.getCode();
	}

	private void checkUserMenuIntoDb(String code) throws OHException {
		UserMenuItem foundUserMenu = userMenuItemIoOperationRepository.findById(code).get();
		testUserMenu.check(foundUserMenu);
	}

	private Integer setupTestGroupMenu(boolean usingSet) throws OHException {
		GroupMenu groupMenu = testGroupMenu.setup(usingSet);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		return groupMenu.getCode();
	}

	private void checkGroupMenuIntoDb(Integer code) throws OHException {
		GroupMenu foundGroupMenu = groupMenuIoOperationRepository.findById(code).get();
		testGroupMenu.check(foundGroupMenu);
	}
}
