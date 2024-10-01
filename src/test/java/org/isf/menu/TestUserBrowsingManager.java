/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.menu.manager.UserBrowsingManager;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestUserBrowsingManager extends OHCoreTestCase {

	private static TestUser testUser;
	private static TestUserGroup testUserGroup;

	@Autowired
	private UserBrowsingManager userBrowsingManager;
	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;
	@Autowired
	private UserIoOperationRepository userIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testUser = new TestUser();
		testUserGroup = new TestUserGroup();
	}

	@BeforeEach
	void setUp() {
		GeneralData.STRONGPASSWORD = true;
		GeneralData.PASSWORDTRIES = 3;
		cleanH2InMemoryDb();
	}

	@Test
	void isNotStrongPassword() {
		assertThat(userBrowsingManager.isPasswordStrong(null)).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh1")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh_")).isFalse();
	}

	@Test
	void isStrongPassword() {
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1@")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abCdef1#")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1$")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcDef1%")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1^")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1&")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdeF1+")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1=")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1!")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1-")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1!")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1-")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1?")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1_")).isTrue();

		assertThat(userBrowsingManager.isPasswordStrong("_abcdef1!")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("@abCdef1@")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("#abcdef1#")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("$abcDef1$")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("%abcdef1%")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("^abcdef1^")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdeF1&")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1*")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abCdef1!(")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1-)")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abCDef1!_")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1-+")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdef1?|")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("abcdEF1_<")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("^abcdef1>")).isTrue();
		assertThat(userBrowsingManager.isPasswordStrong("AaBbCcDdeF1/")).isTrue();
	}

	@Test
	void testIncreaseFailedAttempts() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		int failedAttempts = user.getFailedAttempts();
		userBrowsingManager.increaseFailedAttempts(user);
		user.setFailedAttempts(failedAttempts+ 1);
		assertThat(user.getFailedAttempts()).isEqualTo(failedAttempts + 1);
	}

	@Test
	void testLockUser() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		assertThat(user.getLockedTime()).isNull();
		userBrowsingManager.lockUser(user);
		User updatedUser = userBrowsingManager.getUserByName(userName);
		assertThat(updatedUser.getLockedTime()).isNotNull();
	}

	@Test
	void testUnlockUser() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		userBrowsingManager.lockUser(user);
		User updatedUser = userBrowsingManager.getUserByName(userName);
		userBrowsingManager.setLastLogin(updatedUser);
		updatedUser = userBrowsingManager.getUserByName(userName);
		userBrowsingManager.lockUser(updatedUser);
		updatedUser = userBrowsingManager.getUserByName(userName);

		userBrowsingManager.unlockUser(user);
		updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(updatedUser.getLockedTime()).isNull();
		assertThat(updatedUser.getFailedAttempts()).isZero();
		assertThat(updatedUser.isAccountLocked()).isFalse();
	}

	@Test
	void testUnlockWhenTimeExpired() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		user.setAccountLocked(true);
		user.setLockedTime(TimeTools.getNow().minusDays(30));
		user.setAccountLocked(true);
		assertThat(userBrowsingManager.updateUser(user)).isTrue();

		User updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(userBrowsingManager.unlockWhenTimeExpired(user)).isTrue();
		updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(updatedUser.getLastLogin()).isNull();
		assertThat(updatedUser.getFailedAttempts()).isZero();
		assertThat(updatedUser.isAccountLocked()).isFalse();
	}

	@Test
	void testUnlockWhenTimeExpiredFails() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		user.setAccountLocked(true);
		user.setLockedTime(TimeTools.getNow().plusDays(30));
		user.setAccountLocked(true);
		assertThat(userBrowsingManager.updateUser(user)).isTrue();

		User updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(userBrowsingManager.unlockWhenTimeExpired(user)).isFalse();
	}

	@Test
	void testGetUserGroups() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		List<UserGroup> userGroupList = userBrowsingManager.getUserGroup();

		assertThat(userGroupList).isNotEmpty();
		assertThat(userGroupList.get(0).getCode()).isEqualTo("Z");
	}

	@Test
	void testInvalidUserName() throws Exception {
		assertThatThrownBy(() -> {
			UserGroup userGroup = testUserGroup.setup(true);
			User user = testUser.setup(userGroup, true);
			user.setUserName("A@!");
			userBrowsingManager.newUser(user);
		})
						.isInstanceOf(OHDataValidationException.class);
	}

	private String setupTestUser(boolean usingSet) throws OHException {
		UserGroup userGroup = testUserGroup.setup(usingSet);
		User user = testUser.setup(userGroup, usingSet);
		userGroupIoOperationRepository.saveAndFlush(userGroup);
		userIoOperationRepository.saveAndFlush(user);
		return user.getUserName();
	}
}
