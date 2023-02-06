/*
 * Open Hospital (www.open-hospital.org)
<<<<<<< HEAD
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
=======
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
>>>>>>> 2adacb561550c37e230459abb8801dad97229899
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
package org.isf.menu.manager;

import java.util.List;

import org.isf.menu.model.UserGroup;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserGroupManager {

	@Autowired
	private UserGroupIoOperationRepository userGroupIoOperationRepository;

	public List<UserGroup> findByIdIn(List<String> userGroupIds) {
		return this.userGroupIoOperationRepository.findByCodeIn(userGroupIds);
	}

}