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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.menu.manager;

import java.util.List;

import org.isf.menu.model.UserSetting;
import org.isf.menu.service.UserSettingOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSettingManager {
	
	@Autowired
	private UserSettingOperationRepository userSettingIoOperationRepository;

	/**
	 * Inserts a new {@link UserSetting} into the DB.
	 *
	 * @param userSetting - the {@link UserSetting} to insert
	 * @return <code>{@link UserSetting}</code> if the userSetting has been inserted, <code>null</code> otherwise.
	 * @throws OHServiceException
	 */
	public UserSetting newUserSetting(UserSetting userSetting) throws OHServiceException {
		
		return userSettingIoOperationRepository.save(userSetting);
	}
	
	/**
	 * Updates a new {@link UserSetting} into the DB.
	 *
	 * @param userSetting - the {@link UserSetting} to insert
	 * @return <code>{@link UserSetting}</code> if the userSetting has been inserted, <code>null</code> otherwise.
	 * @throws OHServiceException
	 */
	public UserSetting updateUserSetting(UserSetting userSetting) throws OHServiceException {
		return userSettingIoOperationRepository.save(userSetting);
	}
	
	/**
	 * Returns the list of {@link UserSetting}s of the specified userId.
	 *
	 * @param userId - the user Id
	 * @return the list of {@link UserSetting}s
	 */
	public List<UserSetting> getUserSetting(String userId) throws OHServiceException {
		return userSettingIoOperationRepository.findAllByUSerID(userId);
	}
}
