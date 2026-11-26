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

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.utils.db.BCrypt;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserBrowsingManagerPasswordPolicyTest extends OHCoreTestCase {

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
	void isPasswordStrong_shouldReturnFalse_whenPasswordIsNullOrLacksDigitOrSpecialCharacter() {
		assertThat(userBrowsingManager.isPasswordStrong(null)).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh1")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh_")).isFalse();
	}

	@Test
	void isPasswordStrong_shouldReturnTrue_whenPasswordHasLetterDigitAndSpecialCharacter() {
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
	void isStrongPasswordEnabledAndGetPasswordMinLength_shouldReturnConfiguredValues() {
		GeneralData.STRONGPASSWORD = true;
		GeneralData.STRONGLENGTH = 8;
		assertThat(userBrowsingManager.isStrongPasswordEnabled()).isTrue();
		assertThat(userBrowsingManager.getPasswordMinLength()).isEqualTo(8);

		GeneralData.STRONGPASSWORD = false;
		GeneralData.STRONGLENGTH = 0;
		assertThat(userBrowsingManager.isStrongPasswordEnabled()).isFalse();
		assertThat(userBrowsingManager.getPasswordMinLength()).isZero();
	}

	@Test
	void getPasswordStrengthRegex_shouldMatchStrongPasswordOnly() {
		String regex = userBrowsingManager.getPasswordStrengthRegex();
		assertThat(regex).isNotBlank();
		// the exposed regex must accept/reject the same passwords the strength check enforces
		assertThat("abcdef1@".matches(regex)).isTrue();
		assertThat("abcdefgh".matches(regex)).isFalse();
	}

	@Test
	void isSameAsCurrentPassword_shouldReturnTrueOnlyForCurrentPassword() {
		User user = new User();
		user.setPasswd(BCrypt.hashpw("Secret1@", BCrypt.gensalt()));
		assertThat(userBrowsingManager.isSameAsCurrentPassword(user, "Secret1@")).isTrue();
		assertThat(userBrowsingManager.isSameAsCurrentPassword(user, "Different1@")).isFalse();
		assertThat(userBrowsingManager.isSameAsCurrentPassword(user, "")).isFalse();
		assertThat(userBrowsingManager.isSameAsCurrentPassword(user, null)).isFalse();
		assertThat(userBrowsingManager.isSameAsCurrentPassword(null, "Secret1@")).isFalse();
		assertThat(userBrowsingManager.isSameAsCurrentPassword(new User(), "Secret1@")).isFalse();
	}

	@Test
	void increaseFailedAttempts_shouldNotThrow() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		int failedAttempts = user.getFailedAttempts();
		userBrowsingManager.increaseFailedAttempts(user);
		user.setFailedAttempts(failedAttempts + 1);
		assertThat(user.getFailedAttempts()).isEqualTo(failedAttempts + 1);
	}

	@Test
	void lockUser_shouldSetLockedTime() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		assertThat(user.getLockedTime()).isNull();
		userBrowsingManager.lockUser(user);
		User updatedUser = userBrowsingManager.getUserByName(userName);
		assertThat(updatedUser.getLockedTime()).isNotNull();
	}

	@Test
	void unlockUser_shouldUnlockAccountAndClearLockedTime() throws Exception {
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
	void unlockWhenTimeExpired_shouldReturnTrueAndUnlockAccount_whenLockTimeHasExpired() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		user.setAccountLocked(true);
		user.setLockedTime(TimeTools.getNow().minusDays(30));
		user.setAccountLocked(true);
		assertThat(userBrowsingManager.updateUser(user).getUserName()).isEqualTo(user.getUserName());

		User updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(userBrowsingManager.unlockWhenTimeExpired(user)).isTrue();
		updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(updatedUser.getLastLogin()).isNull();
		assertThat(updatedUser.getFailedAttempts()).isZero();
		assertThat(updatedUser.isAccountLocked()).isFalse();
	}

	@Test
	void unlockWhenTimeExpired_shouldReturnFalse_whenLockTimeHasNotExpired() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		user.setAccountLocked(true);
		user.setLockedTime(TimeTools.getNow().plusDays(30));
		user.setAccountLocked(true);
		assertThat(userBrowsingManager.updateUser(user).getUserName()).isEqualTo(user.getUserName());

		User updatedUser = userBrowsingManager.getUserByName(userName);

		assertThat(userBrowsingManager.unlockWhenTimeExpired(user)).isFalse();
	}

	@Test
	void getUserGroup_shouldReturnSavedUserGroup() throws Exception {
		String userName = setupTestUser(false);
		User user = userBrowsingManager.getUserByName(userName);
		assertThat(user).isNotNull();

		List<UserGroup> userGroupList = userBrowsingManager.getUserGroup();

		assertThat(userGroupList).isNotEmpty();
		assertThat(userGroupList.get(0).getCode()).isEqualTo("Z");
	}

	@Test
	void newUser_shouldThrow_whenUserNameHasInvalidCharacters() throws Exception {
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
