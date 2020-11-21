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

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.GroupMenuIoOperationRepository;
import org.isf.menu.service.MenuIoOperations;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.menu.service.UserMenuItemIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
		String code = _setupTestUser(false);
		_checkUserIntoDb(code);
	}

	@Test
	public void testUserSets() throws Exception {
		String code = _setupTestUser(true);
		_checkUserIntoDb(code);
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
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		ArrayList<User> users = menuIoOperation.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserFromId() throws Exception {
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		ArrayList<User> users = menuIoOperation.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	public void testIoGetUserInfo() throws Exception {
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		String description = menuIoOperation.getUsrInfo(foundUser.getUserName());
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
		String code = _setupTestUser(false);
		boolean result = menuIoOperation.isUserNamePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsGroupNamePresent() throws Exception {
		String code = _setupTestUserGroup(false);
		boolean result = menuIoOperation.isGroupNamePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoNewUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		boolean result = menuIoOperation.newUser(user);
		assertThat(result).isTrue();
		_checkUserIntoDb(user.getUserName());
	}

	@Test
	public void testIoUpdateUser() throws Exception {
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		foundUser.setDesc("Update");
		boolean result = menuIoOperation.updateUser(foundUser);
		User updateUser = userIoOperationRepository.findOne(code);
		assertThat(result).isTrue();
		assertThat(updateUser.getDesc()).isEqualTo("Update");
	}

	@Test
	public void updatePassword() throws Exception {
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		foundUser.setPasswd("Update");
		boolean result = menuIoOperation.updatePassword(foundUser);
		User updateDisease = userIoOperationRepository.findOne(code);
		assertThat(result).isTrue();
		assertThat(updateDisease.getPasswd()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteDisease() throws Exception {
		String code = _setupTestUser(false);
		User foundUser = userIoOperationRepository.findOne(code);
		boolean result = menuIoOperation.deleteUser(foundUser);
		assertThat(result).isTrue();
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

	@Ignore
	@Test
	public void testIoSetGroupMenu() throws Exception {
		//TODO: Do unit test checking insert
	}

	@Test
	public void testIoDeleteUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		boolean result = menuIoOperation.deleteGroup(foundUserGroup);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoNewUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		boolean result = menuIoOperation.newUserGroup(userGroup);
		assertThat(result).isTrue();
		_checkUserGroupIntoDb(userGroup.getCode());
	}

	@Test
	public void testIoUpdateUserGroup() throws Exception {
		String code = _setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findOne(code);
		foundUserGroup.setDesc("Update");
		boolean result = menuIoOperation.updateUserGroup(foundUserGroup);
		UserGroup updateUserGroup = userGroupIoOperationRepository.findOne(code);
		assertThat(result).isTrue();
		assertThat(updateUserGroup.getDesc()).isEqualTo("Update");
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
