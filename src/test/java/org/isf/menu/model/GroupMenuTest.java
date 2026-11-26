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
import org.isf.menu.TestGroupMenu;
import org.isf.menu.service.GroupMenuIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupMenuTest extends OHCoreTestCase {

	private static TestGroupMenu testGroupMenu;

	@Autowired
	private GroupMenuIoOperationRepository groupMenuIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testGroupMenu = new TestGroupMenu();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		Integer code = setupTestGroupMenu(false);
		checkGroupMenuIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		Integer code = setupTestGroupMenu(true);
		checkGroupMenuIntoDb(code);
	}

	@Test
	void setters_shouldUpdateCodeAndActive() throws Exception {
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
	void equals_shouldCompareFields() throws Exception {
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

	private Integer setupTestGroupMenu(boolean usingSet) throws OHException {
		GroupMenu groupMenu = testGroupMenu.setup(usingSet);
		groupMenuIoOperationRepository.saveAndFlush(groupMenu);
		return groupMenu.getCode();
	}

	private void checkGroupMenuIntoDb(Integer code) throws OHException {
		GroupMenu foundGroupMenu = groupMenuIoOperationRepository.findById(code).orElse(null);
		assertThat(foundGroupMenu).isNotNull();
		testGroupMenu.check(foundGroupMenu);
	}

}
