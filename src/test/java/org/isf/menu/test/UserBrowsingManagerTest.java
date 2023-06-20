/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.menu.manager.UserBrowsingManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserBrowsingManagerTest extends OHCoreTestCase {

	@Autowired
	UserBrowsingManager userBrowsingManager;

	@Before
	public void setup() {
		GeneralData.STRONGPASSWORD = true;
	}

	@Test
	public void isNotStrongPassword() {
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh1")).isFalse();
		assertThat(userBrowsingManager.isPasswordStrong("abcdefgh_")).isFalse();
	}

	@Test
	public void isStrongPassword() {
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
}
