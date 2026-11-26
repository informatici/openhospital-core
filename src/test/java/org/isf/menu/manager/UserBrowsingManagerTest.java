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
package org.isf.menu.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.TestUserMenu;
import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.GroupMenuIoOperationRepository;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.menu.service.UserMenuItemIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserBrowsingManagerTest extends OHCoreTestCase {

	private static TestUser testUser;
	private static TestUserGroup testUserGroup;
	private static TestUserMenu testUserMenu;

	@Autowired
	private UserBrowsingManager userBrowsingManager;
	@Autowired
	private GroupMenuIoOperationRepository groupMenuIoOperationRepository;
	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;
	@Autowired
	private UserIoOperationRepository userIoOperationRepository;
	@Autowired
	private UserMenuItemIoOperationRepository userMenuItemIoOperationRepository;

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
	void getUserByName_shouldReturnNull_whenUserIsSoftDeleted() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();
		assertThat(user.isDeleted()).isFalse();

		user.setDeleted(true);
		User foundUser = userIoOperationRepository.findByUserName(userName);
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.isDeleted()).isTrue();
		foundUser = userBrowsingManager.getUserByName(foundUser.getUserName());
		assertThat(foundUser).isNull();
	}

	@Test
	void findUserGroupByCode_shouldReturnNull_whenGroupIsSoftDeleted() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userBrowsingManager.getUserByName(userName);

		assertThat(foundUser).isNotNull();

		UserGroup foundUserGroup = userBrowsingManager.findUserGroupByCode(foundUser.getUserGroupName().getCode());

		assertThat(foundUserGroup).isNotNull();
		UserGroup userGroup = new UserGroup(foundUserGroup.getCode(), foundUserGroup.getDesc());
		assertThatThrownBy(() -> {
			userBrowsingManager.deleteGroup(userGroup);
		}).isInstanceOf(OHServiceException.class);

		foundUser.setDeleted(true);

		foundUserGroup.setDeleted(true);
		foundUserGroup = userGroupIoOperationRepository.findByCodeAndDeleted(foundUserGroup.getCode(), true);
		assertThat(foundUserGroup).isNotNull();
		assertThat(foundUserGroup.isDeleted()).isTrue();
		foundUserGroup = userBrowsingManager.findUserGroupByCode(foundUserGroup.getCode());
		assertThat(foundUserGroup).isNull();
	}

	@Test
	void getUser_shouldReturnSavedUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		List<User> users = userBrowsingManager.getUser();
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUser_shouldReturnUsersOfGroup_whenGroupIdIsGiven() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		List<User> users = userBrowsingManager.getUser(foundUser.getUserGroupName().getCode());
		assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUserByName_shouldReturnSavedUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user.getDesc()).isEqualTo(foundUser.getDesc());
	}

	@Test
	void getUsrInfo_shouldReturnUserDescription() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		String description = userBrowsingManager.getUsrInfo(userName);
		assertThat(description).isEqualTo(foundUser.getDesc());
	}

	@Test
	void deleteGroup_shouldRemoveUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		userBrowsingManager.deleteGroup(foundUserGroup);
		foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNull();
	}

	@Test
	void deleteGroup_shouldThrow_whenCodeIsAdmin() throws Exception {
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
	void deleteGroup_shouldThrowDataIntegrityViolation_whenGroupHasUsers() throws Exception {
		assertThatThrownBy(() ->
		{
			String userName = setupTestUser(true);
			User user = userIoOperationRepository.findById(userName).orElse(null);
			assertThat(user).isNotNull();
			userBrowsingManager.deleteGroup(user.getUserGroupName());
		})
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void getUserGroup_shouldReturnSavedUserGroup() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		List<UserGroup> userGroups = userBrowsingManager.getUserGroup();
		assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
	}

	@Test
	void newUser_shouldSaveUser() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		User newUser = userBrowsingManager.newUser(user);
		assertThat(newUser.getUserName()).isEqualTo(user.getUserName());
		checkUserIntoDb(newUser.getUserName());
	}

	@Test
	void newUser_shouldSetPasswdMustChange_whenFlagIsFalse() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		User user = testUser.setup(userGroup, false);
		user.setPasswdMustChange(false);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		User newUser = userBrowsingManager.newUser(user);
		User foundUser = userIoOperationRepository.findById(newUser.getUserName()).orElse(null);
		assertThat(foundUser).isNotNull();
		// OP-896: the initial password is assigned by an administrator, so the new user must change it at first login
		assertThat(foundUser.isPasswdMustChange()).isTrue();
	}

	@Test
	void newUser_shouldThrowDataIntegrityViolation_whenUserNameExists() throws Exception {
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
	void updateUser_shouldSaveNewDescription() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		foundUser.setDesc("Update");
		assertThat(userBrowsingManager.updateUser(foundUser).getUserName()).isEqualTo(foundUser.getUserName());
		User updatedUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getDesc()).isEqualTo("Update");
	}

	@Test
	void updatePassword_shouldSaveNewPasswordAndLastChangedDate() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		foundUser.setPasswd("Update");
		assertThat(userBrowsingManager.updatePassword(foundUser).getUserName()).isEqualTo(foundUser.getUserName());
		// the password update is a bulk JPQL statement: clear the context so the re-read reflects the DB
		entityManager.clear();
		User updatedUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getPasswd()).isEqualTo("Update");
		// OP-896: changing the password resets the lease clock and clears the must-change flag by default
		assertThat(updatedUser.getPasswdLastChanged()).isNotNull();
		assertThat(updatedUser.isPasswdMustChange()).isFalse();
	}

	@Test
	void updatePassword_shouldSavePasswdMustChange_whenFlagIsTrue() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		// e.g. an administrator setting another user's password: force a change at next login
		foundUser.setPasswd("Reset");
		foundUser.setPasswdMustChange(true);
		userBrowsingManager.updatePassword(foundUser);
		entityManager.clear();
		User updatedUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getPasswdLastChanged()).isNotNull();
		assertThat(updatedUser.isPasswdMustChange()).isTrue();
	}

	@Test
	void isPasswordExpired_shouldReturnTrueOnlyForPasswordOlderThanLease() throws Exception {
		String userName = setupTestUser(false);
		User user = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(user).isNotNull();

		int previousLease = GeneralData.PASSWORDLEASE;
		try {
			// lease disabled: never expired
			GeneralData.PASSWORDLEASE = 0;
			user.setPasswdLastChanged(TimeTools.getNow().minusDays(100));
			assertThat(userBrowsingManager.isPasswordExpired(user)).isFalse();

			// lease enabled but unknown last-changed date: not expired
			GeneralData.PASSWORDLEASE = 30;
			user.setPasswdLastChanged(null);
			assertThat(userBrowsingManager.isPasswordExpired(user)).isFalse();

			// password changed within the lease window: not expired
			user.setPasswdLastChanged(TimeTools.getNow().minusDays(10));
			assertThat(userBrowsingManager.isPasswordExpired(user)).isFalse();

			// password changed just inside the lease window (29 of 30 days): not expired
			user.setPasswdLastChanged(TimeTools.getNow().minusDays(29));
			assertThat(userBrowsingManager.isPasswordExpired(user)).isFalse();

			// password older than the lease: expired
			user.setPasswdLastChanged(TimeTools.getNow().minusDays(31));
			assertThat(userBrowsingManager.isPasswordExpired(user)).isTrue();
		} finally {
			GeneralData.PASSWORDLEASE = previousLease;
		}
	}

	@Test
	void getPasswordLeaseDays_shouldReturnConfiguredLease() {
		int previousLease = GeneralData.PASSWORDLEASE;
		try {
			// lease policy disabled
			GeneralData.PASSWORDLEASE = 0;
			assertThat(userBrowsingManager.getPasswordLeaseDays()).isZero();

			// lease policy active
			GeneralData.PASSWORDLEASE = 30;
			assertThat(userBrowsingManager.getPasswordLeaseDays()).isEqualTo(30);
		} finally {
			GeneralData.PASSWORDLEASE = previousLease;
		}
	}

	@Test
	void isPasswordValid_shouldApplyMinLengthAndStrengthPolicy() {
		boolean previousStrong = GeneralData.STRONGPASSWORD;
		int previousLength = GeneralData.STRONGLENGTH;
		try {
			GeneralData.STRONGPASSWORD = true;
			GeneralData.STRONGLENGTH = 6;
			assertThat(userBrowsingManager.isPasswordValid(null)).isFalse();
			assertThat(userBrowsingManager.isPasswordValid("ab")).isFalse(); // too short
			assertThat(userBrowsingManager.isPasswordValid("abcdef")).isFalse(); // long enough but not strong
			assertThat(userBrowsingManager.isPasswordValid("Admin.123")).isTrue(); // strong and long enough

			// length check disabled (0): only the strength policy applies
			GeneralData.STRONGLENGTH = 0;
			assertThat(userBrowsingManager.isPasswordValid("A.1")).isTrue();

			// strength policy disabled: any non-null password is accepted
			GeneralData.STRONGPASSWORD = false;
			GeneralData.STRONGLENGTH = 6;
			assertThat(userBrowsingManager.isPasswordValid("ab")).isFalse(); // still too short
			assertThat(userBrowsingManager.isPasswordValid("abcdef")).isTrue();
		} finally {
			GeneralData.STRONGPASSWORD = previousStrong;
			GeneralData.STRONGLENGTH = previousLength;
		}
	}

	@Test
	void deleteUser_shouldRemoveUser() throws Exception {
		String userName = setupTestUser(false);
		User foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNotNull();
		userBrowsingManager.deleteUser(foundUser);
		foundUser = userIoOperationRepository.findById(userName).orElse(null);
		assertThat(foundUser).isNull();
	}

	@Test
	void deleteUser_shouldThrow_whenUserNameIsAdmin() throws Exception {
		assertThatThrownBy(() ->
		{
			String userName = setupTestUser(false);
			User foundUser = userIoOperationRepository.findById(userName).orElse(null);
			assertThat(foundUser).isNotNull();
			foundUser.setUserName("admin");
			userBrowsingManager.deleteUser(foundUser);
		})
			.isInstanceOf(OHDataValidationException.class);
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
		List<UserMenuItem> menus = userBrowsingManager.getMenu(user);
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
		List<UserMenuItem> menus = userBrowsingManager.getGroupMenu(userGroup);
		assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
	}

	@Test
	void setGroupMenu_shouldReturnTrue() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserMenuItem menuItem = testUserMenu.setup(false);
		List<UserMenuItem> userMenuItems = new ArrayList<>();
		userMenuItems.add(menuItem);
		assertThat(userBrowsingManager.setGroupMenu(userGroup, userMenuItems)).isTrue();
	}

	@Test
	void newUserGroup_shouldSaveUserGroup() throws Exception {
		UserGroup userGroup = testUserGroup.setup(false);
		UserGroup newUserGroup = userBrowsingManager.newUserGroup(userGroup);
		List<UserGroup> userGroups = userBrowsingManager.getUserGroup();
		assertThat(userGroups).contains(newUserGroup);
		checkUserGroupIntoDb(newUserGroup.getCode());
	}

	@Test
	void newUserGroup_shouldThrowDataIntegrityViolation_whenCodeExists() throws Exception {
		assertThatThrownBy(() ->
		{
			String code = setupTestUserGroup(true);
			UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
			assertThat(foundUserGroup).isNotNull();
			userBrowsingManager.newUserGroup(foundUserGroup);
		})
			.isInstanceOf(OHDataIntegrityViolationException.class);
	}

	@Test
	void updateUserGroup_shouldSaveNewDescription() throws Exception {
		String code = setupTestUserGroup(false);
		UserGroup foundUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserGroup).isNotNull();
		foundUserGroup.setDesc("Update");
		assertThat(userBrowsingManager.updateUserGroup(foundUserGroup).getCode()).isEqualTo(foundUserGroup.getCode());
		UserGroup updatedUserGroup = userGroupIoOperationRepository.findById(code).orElse(null);
		assertThat(updatedUserGroup).isNotNull();
		assertThat(updatedUserGroup.getDesc()).isEqualTo("Update");
	}

	private String setupTestUserGroup(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		return userGroup.getCode();
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
