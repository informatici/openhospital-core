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
import org.isf.menu.TestUserMenu;
import org.isf.menu.service.UserMenuItemIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserMenuItemTest extends OHCoreTestCase {

	private static TestUserMenu testUserMenu;

	@Autowired
	private UserMenuItemIoOperationRepository userMenuItemIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testUserMenu = new TestUserMenu();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestUserMenu(false);
		checkUserMenuIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestUserMenu(true);
		checkUserMenuIntoDb(code);
	}

	@Test
	void equals_shouldCompareFields() throws Exception {
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
	void toString_shouldReturnButtonLabel() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(true);
		assertThat(userMenuItem).hasToString(userMenuItem.getButtonLabel());
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		UserMenuItem userMenuItem = testUserMenu.setup(false);
		userMenuItem.setCode("someCode");
		// compute value
		int hashCode = userMenuItem.hashCode();
		// reuse value
		assertThat(userMenuItem.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestUserMenu(boolean usingSet) throws OHException {
		UserMenuItem userMenu = testUserMenu.setup(usingSet);
		userMenuItemIoOperationRepository.saveAndFlush(userMenu);
		return userMenu.getCode();
	}

	private void checkUserMenuIntoDb(String code) throws OHException {
		UserMenuItem foundUserMenu = userMenuItemIoOperationRepository.findById(code).orElse(null);
		assertThat(foundUserMenu).isNotNull();
		testUserMenu.check(foundUserMenu);
	}

}
