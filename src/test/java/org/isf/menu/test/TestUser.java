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

import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.utils.exception.OHException;

public class TestUser {

	private String name = "TestName";
	private String passwd = "TestPaswd";
	private String desc = "TestDesc";

	public User setup(UserGroup userGroupName, boolean usingSet) throws OHException {
		User user;

		if (usingSet) {
			user = new User();
			setParameters(user, userGroupName);
		} else {
			// Create User with all parameters 
			user = new User(name, userGroupName, passwd, desc);
		}

		return user;
	}

	public void setParameters(User user, UserGroup userGroupName) {
		user.setUserName(name);
		user.setDesc(desc);
		user.setUserGroupName(userGroupName);
		user.setPasswd(passwd);
	}

	public void check(User user) {
		assertThat(user.getUserName()).isEqualTo(name);
		assertThat(user.getDesc()).isEqualTo(desc);
		assertThat(user.getPasswd()).isEqualTo(passwd);
	}
}
