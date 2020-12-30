/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
		String code = _setupTestUserGroup(false);
		_checkUserGroupIntoDb(code);
	}

	@Test
	public void testUserGroupSets() throws Exception {
		String code = _setupTestUserGroup(true);
		_checkUserGroupIntoDb(code);
	}

	@Test
	public void testUserGets() throws Exception {
		String userName = _setupTestUser(false);
		_checkUserIntoDb(userName);
	}

	@Test
	public void testUserSets() throws Exception {
		String userName = _setupTestUser(true);
		_checkUserIntoDb(userName);
	}

	@Test
	public void testUserMenuGets() throws Exception {
		String code = _setupTestUserMenu(false);
		_checkUserMenuIntoDb(code);
	}

	@Test
	public void testUserMenuSets() throws Exception {
		String code = _setupTestUserMenu(true);
		_checkUserMenuIntoDb(code);
	}

	@Test
	public void testGroupMenuGets() throws Exception {
		Integer code = _setupTestGroupMenu(false);
		_checkGroupMenuIntoDb(code);
	}

	@Test
	public void testGroupMenuSets() throws Exception {
		Integer code = _setupTestGroupMenu(true);
		_checkGroupMenuIntoDb(code);
	}

	@Test
	public void testIoGetUser() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		ArrayList<User> users = menuIoOperation.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUsersFromGroupId() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		ArrayList<User> users = menuIoOperation.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserByName() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		User user = menuIoOperation.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserInfo() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		String description = menuIoOperation.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		ArrayList<UserGroup> userGroups = menuIoOperation.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	public void testIoIsUserNamePresent() throws Exception {
		String userName = _setupTestUser(false);
		assertThat(menuIoOperation.isUserNamePresent(userName)).isTrue();
	}

	@Test
	public void testIoIsGroupNamePresent() throws Exception {
		String code = _setupTestUserGroup(false);
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
		_checkUserIntoDb(user.getUserName());
	}

	@Test
	public void testIoUpdateUser() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		foundUser.setDesc("Update");
		assertThat(menuIoOperation.updateUser(foundUser)).isTrue();
		User updateUser = userIoOperationRepository.findOne(userName);
		assertThat(updateUser.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testIoUpdatePassword() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		foundUser.setPasswd("Update");
		assertThat(menuIoOperation.updatePassword(foundUser)).isTrue();
		User updateDisease = userIoOperationRepository.findOne(userName);
		assertThat(updateDisease.getPasswd()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteUser() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		assertThat(menuIoOperation.deleteUser(foundUser)).isTrue();
		ArrayList<User> users = menuIoOperation.getUser(userName);
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
		ArrayList<UserMenuItem> menus = menuIoOperation.getMenu(user);
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
		ArrayList<UserMenuItem> menus = menuIoOperation.getGroupMenu(userGroup);
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
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		assertThat(menuIoOperation.deleteGroup(foundUserGroup)).isTrue();
		assertThat(menuIoOperation.isGroupNamePresent(foundUserGroup.getCode())).isFalse();
	}

	@Test
	public void testIoNewUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		assertThat(menuIoOperation.newUserGroup(userGroup)).isTrue();
		_checkUserGroupIntoDb(userGroup.getCode());
	}

	@Test
	public void testIoUpdateUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		foundUserGroup.setDesc("Update");
		assertThat(menuIoOperation.updateUserGroup(foundUserGroup)).isTrue();
		UserGroup updateUserGroup = userGroupIoOperationRepository.findOne(code);
		assertThat(updateUserGroup.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testMgrGetUser() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		ArrayList<User> users = userBrowsingManager.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUsersFromGroupId() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		ArrayList<User> users = userBrowsingManager.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUserByName() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrGetUserInfo() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		String description = userBrowsingManager.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testMgrDeleteGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
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
			String userName = _setupTestUser(true);
			User user = userIoOperationRepository.findOne(userName);
			userBrowsingManager.deleteGroup(user.getUserGroupName());
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMgrGetUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		ArrayList<UserGroup> userGroups = userBrowsingManager.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	public void testMgrNewUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		assertThat(userBrowsingManager.newUser(user)).isTrue();
		_checkUserIntoDb(user.getUserName());
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
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		foundUser.setDesc("Update");
		assertThat(userBrowsingManager.updateUser(foundUser)).isTrue();
		User updateUser = userIoOperationRepository.findOne(userName);
		assertThat(updateUser.getDesc()).isEqualTo("Update");
	}

	@Test
	public void testMgrUpdatePassword() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		foundUser.setPasswd("Update");
		assertThat(userBrowsingManager.updatePassword(foundUser)).isTrue();
		User updateDisease = userIoOperationRepository.findOne(userName);
		assertThat(updateDisease.getPasswd()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteUser() throws Exception {
		String userName = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(userName);
		assertThat(userBrowsingManager.deleteUser(foundUser)).isTrue();
		ArrayList<User> users = userBrowsingManager.getUser(userName);
		assertThat(users).isEmpty();
	}

	@Test
	public void testMgrDeleteAdminUser() throws Exception {
		assertThatThrownBy(() ->
		{
			String userName = _setupTestUser(false);
			User foundUser = userIoOperationRepository.findOne(userName);
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
		ArrayList<UserMenuItem> menus = userBrowsingManager.getMenu(user);
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
		ArrayList<UserMenuItem> menus = userBrowsingManager.getGroupMenu(userGroup);
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
		_checkUserGroupIntoDb(userGroup.getCode());
	}

	@Test
	public void testMgrNewUserGroupAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			String code = _setupTestUserGroup(true);
			UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
			userBrowsingManager.newUserGroup(foundUserGroup);
		})
				.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	public void testMgrUpdateUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		foundUserGroup.setDesc("Update");
		assertThat(userBrowsingManager.updateUserGroup(foundUserGroup)).isTrue();
		UserGroup updateUserGroup = userGroupIoOperationRepository.findOne(code);
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

		assertThat(groupMenu.equals(null)).isFalse();
		assertThat(groupMenu.equals("aString")).isFalse();

		GroupMenu groupMenu1 = testGroupMenu.setup(false);
		groupMenu1.setCode(-1);
		assertThat(groupMenu.equals(groupMenu1)).isFalse();

		groupMenu1.setCode(groupMenu.getCode());
		groupMenu1.setUserGroup("someOtherGroup");
		assertThat(groupMenu.equals(groupMenu1)).isFalse();

		groupMenu1.setUserGroup(groupMenu.getUserGroup());
		groupMenu1.setMenuItem("someOtherMenuItem");
		assertThat(groupMenu.equals(groupMenu1)).isFalse();

		groupMenu1.setMenuItem(groupMenu.getMenuItem());
		groupMenu1.setActive(-1);
		assertThat(groupMenu.equals(groupMenu1)).isFalse();

		groupMenu1.setActive(groupMenu.getActive());
		assertThat(groupMenu.equals(groupMenu1)).isTrue();
	}

	@Test
	public void testUserToString() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);
		assertThat(user.toString()).isEqualTo(user.getUserName());
	}

	@Test
	public void testUserEquals() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);

		assertThat(user.equals(null)).isFalse();
		assertThat(user.equals("someString")).isFalse();

		User user1 = testUser.setup(userGroup, false);
		user1.setUserName("someOtherName");
		assertThat(user.equals(user1)).isFalse();

		user1.setUserName(user.getUserName());
		user1.setDesc("someOtherDescription");
		assertThat(user.equals(user1)).isFalse();

		user1.setDesc(user.getDesc().toLowerCase());
		assertThat(user.equals(user1)).isTrue();
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
		assertThat(userGroup.toString()).isEqualTo("someCode");
	}

	@Test
	public void testUserGroupEquals() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);

		assertThat(userGroup.equals(null)).isFalse();
		assertThat(userGroup.equals("someString")).isFalse();

		UserGroup userGroup1 = testUserGroup.setup(false);

		userGroup.setCode("code1");
		userGroup1.setCode("code2");
		assertThat(userGroup.equals(userGroup1)).isFalse();

		userGroup1.setCode(userGroup.getCode());
		userGroup1.setDesc("someOtherDescription");
		assertThat(userGroup.equals(userGroup1)).isFalse();

		userGroup1.setDesc(userGroup.getDesc().toLowerCase());
		assertThat(userGroup.equals(userGroup1)).isTrue();
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

		assertThat(userMenuItem.equals(null)).isFalse();
		assertThat(userMenuItem.equals("someString")).isFalse();

		UserMenuItem userMenuItem1 = testUserMenu.setup(false);
		userMenuItem.setCode("code1");
		userMenuItem1.setCode("code2");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setCode(userMenuItem.getCode());
		userMenuItem1.setButtonLabel("someOtherButtonLabel");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setButtonLabel(userMenuItem.getButtonLabel());
		userMenuItem1.setAltLabel("someOtherLabel");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setAltLabel(userMenuItem.getAltLabel());
		userMenuItem1.setTooltip("someOtherToolTip");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setTooltip(userMenuItem.getTooltip());
		userMenuItem1.setShortcut('?');
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setShortcut(userMenuItem.getShortcut());
		userMenuItem1.setMySubmenu("someOtherSubMenu");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setMySubmenu(userMenuItem.getMySubmenu());
		userMenuItem1.setMyClass("someOtherClass");
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setMyClass(userMenuItem.getMyClass());
		userMenuItem1.setASubMenu(!userMenuItem.isASubMenu());
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setASubMenu(userMenuItem.isASubMenu());
		userMenuItem1.setPosition(-1);
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setPosition(userMenuItem.getPosition());
		userMenuItem1.setActive(!userMenuItem.isActive());
		assertThat(userMenuItem.equals(userMenuItem1)).isFalse();

		userMenuItem1.setActive(userMenuItem.isActive());
		assertThat(userMenuItem.equals(userMenuItem1)).isTrue();
	}

	@Test
	public void testUserMenuItemToString() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(true);
		assertThat(userMenuItem.toString()).isEqualTo(userMenuItem.getButtonLabel());
	}

	@Test
	public void testUserMenuItemGetDescription() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(true);
		// TODO: this will need to change if resource bundles are made available.
		assertThat(userMenuItem.getDescription())
				.isEqualTo("angal.menu.usermenuitemZangal.menu.labelstooltipshortTestButtonLabel-TestAltLabel-TestToolTip-Y...\n" +
						"angal.menu.submenuTestMySubmenuangal.menu.classTestMyClass...\n angal.menu.issubmenutrueangal.menu.isactivetrueangal.menu.inposition11");
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

	private String _setupTestUserGroup(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		return userGroup.getCode();
	}

	private void _checkUserGroupIntoDb(String code) throws OHException {
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		testUserGroup.check(foundUserGroup);
	}

	private String _setupTestUser(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		User user = testUser.setup(userGroup, usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		return user.getUserName();
	}

	private void _checkUserIntoDb(String code) throws OHException {
		User foundUser = userIoOperationRepository.findOne(code);
		testUser.check(foundUser);
	}

	private String _setupTestUserMenu(boolean usingSet) throws OHException {
		UserMenuItem userMenu = testUserMenu.setup(usingSet);
		userMenuItemIoOperationRepository.saveAndFlush(userMenu);
		return userMenu.getCode();
	}

	private void _checkUserMenuIntoDb(String code) throws OHException {
		UserMenuItem foundUserMenu = userMenuItemIoOperationRepository.findOne(code);
		testUserMenu.check(foundUserMenu);
	}

	private Integer _setupTestGroupMenu(boolean usingSet) throws OHException {
		GroupMenu groupMenu = testGroupMenu.setup(usingSet);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		return groupMenu.getCode();
	}

	private void _checkGroupMenuIntoDb(Integer code) throws OHException {
		GroupMenu foundGroupMenu = groupMenuIoOperationRepository.findOne(code);
		testGroupMenu.check(foundGroupMenu);
	}
}
