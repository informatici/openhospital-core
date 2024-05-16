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
import java.util.Optional;

import org.isf.menu.model.UserSetting;
import org.isf.menu.service.UserSettingOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.springframework.stereotype.Component;

@Component
public class UserSettingManager {

	private UserSettingOperationRepository userSettingIoOperationRepository;

	public UserSettingManager(UserSettingOperationRepository userSettingOperationRepository) {
		this.userSettingIoOperationRepository = userSettingOperationRepository;
	}

	/**
	 * Inserts a new {@link UserSetting} into the DB.
	 *
	 * @param userSetting - the {@link UserSetting} to insert
	 * @return {@link UserSetting} if the userSetting has been inserted, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public UserSetting newUserSetting(UserSetting userSetting) throws OHServiceException {
		return userSettingIoOperationRepository.save(userSetting);
	}

	/**
	 * Updates an existing {@link UserSetting} in the DB.
	 *
	 * @param userSetting - the {@link UserSetting} to insert
	 * @return {@link UserSetting} if the userSetting has been updated, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public UserSetting updateUserSetting(UserSetting userSetting) throws OHServiceException {
		return userSettingIoOperationRepository.save(userSetting);
	}
	
	/**
	 * Returns the list of {@link UserSetting}s of the specified userId.
	 *
	 * @param userName - the user name
	 * @return the list of {@link UserSetting}s
	 * @throws OHServiceException
	 */
	public List<UserSetting> getUserSettingByUserName(String userName) throws OHServiceException {
		return userSettingIoOperationRepository.findAllByUserName(userName);
	}

	/**
	 * Returns {@link UserSetting}s of the specified userId.
	 *
	 * @param userName     - the user name
	 * @param configName - the name of the user setting
	 * @return {@link UserSetting} if the userSetting exist, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public UserSetting getUserSettingByUserNameConfigName(String userName, String configName) throws OHServiceException {
		return userSettingIoOperationRepository.findByUserNameAndConfigName(userName, configName);
	}
	
	/**
	 * Returns {@link UserSetting}s of the specified userId.
	 *
	 * @param userSettingId - the user id
	 * @return {@link UserSetting} if the userSetting exist, {@code null} otherwise.
	 * @throws OHServiceException
	 */
	public Optional<UserSetting> getUserSettingById(int userSettingId) throws OHServiceException {
		 return userSettingIoOperationRepository.findById(userSettingId);
	}

	/**
	 * Delete the {@link UserSetting}.
	 * 
	 * @param userSetting - the userSetting to delete
	 * @throws OHServiceException
	 */
	public void deleteUserSetting(UserSetting userSetting) throws OHServiceException {
		userSettingIoOperationRepository.delete(userSetting);
	}
}
