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

import org.isf.menu.model.UserSetting;
import org.isf.utils.exception.OHException;

public class TestUserSetting {

	protected String user = "TestUser";
	protected String configName = "TestConfigName";
	protected String configValue = "TestConfigValue";

	public UserSetting setup(boolean usingSet) throws OHException {
		UserSetting userSetting;

		if (usingSet) {
			userSetting = new UserSetting();
			setParameters(userSetting);
		} else {
			// Create User with all parameters 
			userSetting = new UserSetting(user, configName, configValue);
		}

		return userSetting;
	}

	public void setParameters(UserSetting userSetting) {
		userSetting.setUser(user);
		userSetting.setConfigName(configName);
		userSetting.setConfigValue(configValue);
	}

	public void check(UserSetting userSetting) {
		assertThat(userSetting.getUser()).isEqualTo(user);
		assertThat(userSetting.getConfigName()).isEqualTo(configName);
		assertThat(userSetting.getConfigValue()).isEqualTo(configValue);
	}
}
