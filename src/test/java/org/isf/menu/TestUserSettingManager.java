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

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.menu.manager.UserSettingManager;
import org.isf.menu.model.UserSetting;
import org.isf.menu.service.UserSettingOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestUserSettingManager extends OHCoreTestCase {

	private static TestUserSetting testUserSetting;

	@Autowired
	private UserSettingManager userSettingManager;

	@Autowired
	private UserSettingOperationRepository userSettingIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testUserSetting = new TestUserSetting();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testGet() throws Exception {
		String name = setupTestUserSetting(false);
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(name);
		assertThat(userSettings).isNotNull();
		assertThat(userSettings).isNotEmpty();
		testUserSetting.check(userSettings.get(0));
	}

	@Test
	void testSet() throws Exception {
		String name = setupTestUserSetting(true);
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(name);
		assertThat(userSettings).isNotNull();
		assertThat(userSettings).isNotEmpty();
		testUserSetting.check(userSettings.get(0));
	}

	@Test
	void testNewUserSetting() throws Exception {
		UserSetting userSetting = new UserSetting();
		userSetting.setUser(testUserSetting.user);
		userSetting.setConfigName(testUserSetting.configName);
		userSetting.setConfigValue(testUserSetting.configValue);
		UserSetting newUserSetting = userSettingManager.newUserSetting(userSetting);
		testUserSetting.check(newUserSetting);
	}

	@Test
	void testUpdateUserSetting() throws Exception {
		String name = setupTestUserSetting(true);
		List<UserSetting> userSettings = userSettingManager.getUserSettingByUserName(name);
		assertThat(userSettings).isNotNull();
		assertThat(userSettings).isNotEmpty();
		UserSetting userSetting = userSettings.get(0);
		userSetting.setConfigValue("newConfigValue");
		UserSetting updatedUserSetting = userSettingManager.updateUserSetting(userSetting);
		assertThat(updatedUserSetting.getConfigValue()).isEqualTo("newConfigValue");
	}

	@Test
	void testGetSettingByUserNameAndConfigName() throws Exception {
		String name = setupTestUserSetting(true);
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(testUserSetting.user, testUserSetting.configName);
		assertThat(userSetting).isNotNull();
		assertThat(userSetting.getUser()).isEqualTo(testUserSetting.user);
		assertThat(userSetting.getConfigName()).isEqualTo(testUserSetting.configName);
	}

	@Test
	void testGetSettingById() throws Exception {
		String name = setupTestUserSetting(true);
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(testUserSetting.user, testUserSetting.configName);
		assertThat(userSetting).isNotNull();
		UserSetting foundUserSetting = userSettingManager.getUserSettingById(userSetting.getId()).orElse(null);
		assertThat(foundUserSetting).isNotNull();
		assertThat(foundUserSetting.getId()).isEqualTo(userSetting.getId());
	}

	@Test
	void testDeleteUserSetting() throws Exception {
		String name = setupTestUserSetting(true);
		UserSetting userSetting = userSettingManager.getUserSettingByUserNameConfigName(testUserSetting.user, testUserSetting.configName);
		assertThat(userSetting).isNotNull();
		userSettingManager.deleteUserSetting(userSetting);
		List<UserSetting> userSettingsList = userSettingManager.getUserSettingByUserName(name);
		assertThat(userSettingsList).isEmpty();
	}

	private String setupTestUserSetting(boolean usingSet) throws OHException {
		UserSetting userSetting = testUserSetting.setup(usingSet);
		userSettingIoOperationRepository.saveAndFlush(userSetting);
		return userSetting.getUser();
	}
}
