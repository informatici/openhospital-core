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

import org.isf.menu.model.UserMenuItem;
import org.isf.utils.exception.OHException;

public class TestUserMenu {

	private String code = "Z";
	private String buttonLabel = "TestButtonLabel";
	private String altLabel = "TestAltLabel";
	private String tooltip = "TestToolTip";
	private char shortcut = 'Y';
	private String mySubmenu = "TestMySubmenu";
	private String myClass = "TestMyClass";
	private boolean isASubMenu = true;
	private int position = 11;

	public UserMenuItem setup(boolean usingSet) throws OHException {
		UserMenuItem userMenuItem;

		if (usingSet) {
			userMenuItem = new UserMenuItem();
			setParameters(userMenuItem);
		} else {
			// Create UserMenuItem with all parameters 
			userMenuItem = new UserMenuItem(code, buttonLabel, altLabel, tooltip, shortcut, mySubmenu, myClass, isASubMenu, position, true);
		}

		return userMenuItem;
	}

	public void setParameters(UserMenuItem userMenuItem) {
		userMenuItem.setCode(code);
		userMenuItem.setAltLabel(altLabel);
		userMenuItem.setButtonLabel(buttonLabel);
		userMenuItem.setActive(true);
		userMenuItem.setASubMenu(isASubMenu);
		userMenuItem.setMyClass(myClass);
		userMenuItem.setMySubmenu(mySubmenu);
		userMenuItem.setPosition(position);
		userMenuItem.setShortcut(shortcut);
		userMenuItem.setTooltip(tooltip);
	}

	public void check(UserMenuItem userMenuItem) {
		assertThat(userMenuItem.getCode()).isEqualTo(code);
		assertThat(userMenuItem.getAltLabel()).isEqualTo(altLabel);
		assertThat(userMenuItem.getButtonLabel()).isEqualTo(buttonLabel);
		assertThat(userMenuItem.isASubMenu()).isEqualTo(isASubMenu);
		assertThat(userMenuItem.getMyClass()).isEqualTo(myClass);
		assertThat(userMenuItem.getMySubmenu()).isEqualTo(mySubmenu);
		assertThat(userMenuItem.getPosition()).isEqualTo(position);
		assertThat(userMenuItem.getShortcut()).isEqualTo(shortcut);
		assertThat(userMenuItem.getTooltip()).isEqualTo(tooltip);
	}
}
