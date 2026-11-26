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
package org.isf.menu.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.menu.TestUser;
import org.isf.menu.TestUserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserTest extends OHCoreTestCase {

	private static TestUser testUser;
	private static TestUserGroup testUserGroup;

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
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String userName = setupTestUser(false);
		checkUserIntoDb(userName);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String userName = setupTestUser(true);
		checkUserIntoDb(userName);
	}

	@Test
	void toString_shouldReturnUserName() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);
		assertThat(user).hasToString(user.getUserName());
	}

	@Test
	void equals_shouldCompareUserNameAndDescriptionIgnoringCase() throws Exception {
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
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		User user = testUser.setup(userGroup, true);
		// compute value
		int hashCode = user.hashCode();
		// reuse value
		assertThat(user.hashCode()).isEqualTo(hashCode);
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
