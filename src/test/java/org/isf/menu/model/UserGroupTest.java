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
import org.isf.menu.TestUserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserGroupTest extends OHCoreTestCase {

	private static TestUserGroup testUserGroup;

	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testUserGroup = new TestUserGroup();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestUserGroup(false);
		checkUserGroupIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestUserGroup(true);
		checkUserGroupIntoDb(code);
	}

	@Test
	void toString_shouldReturnCode() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		userGroup.setCode("someCode");
		assertThat(userGroup).hasToString("someCode");
	}

	@Test
	void equals_shouldCompareCodeAndDescriptionIgnoringCase() throws Exception {
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
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		UserGroup userGroup = testUserGroup.setup(true);
		// compute value
		int hashCode = userGroup.hashCode();
		// reuse value
		assertThat(userGroup.hashCode()).isEqualTo(hashCode);
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

}
