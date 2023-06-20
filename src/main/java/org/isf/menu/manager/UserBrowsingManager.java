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
package org.isf.menu.manager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.MenuIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserBrowsingManager {

	@Autowired
	private MenuIoOperations ioOperations;

	public static String getCurrentUser() {
		return MDC.get("OHUser");
	}

	/**
	 * Returns the list of {@link User}s.
	 *
	 * @return the list of {@link User}s
	 */
	public List<User> getUser() throws OHServiceException {
		return ioOperations.getUser();
	}

	/**
	 * Returns the list of {@link User}s in specified groupID.
	 *
	 * @param groupID - the group ID
	 * @return the list of {@link User}s
	 */
	public List<User> getUser(String groupID) throws OHServiceException {
		return ioOperations.getUser(groupID);
	}

	/**
	 * Returns a {@link User} with the specified name.
	 *
	 * @param userName - user name
	 * @return {@link User}
	 */
	public User getUserByName(String userName) throws OHServiceException {
		return ioOperations.getUserByName(userName);
	}

	/**
	 * Inserts a new {@link User} into the DB.
	 *
	 * @param user - the {@link User} to insert
	 * @return <code>true</code> if the user has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newUser(User user) throws OHServiceException {
		String username = user.getUserName();
		if (ioOperations.isUserNamePresent(username)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.userbrowser.theuseralreadyexists.fmt.msg", username),
					OHSeverityLevel.ERROR));
		}
		return ioOperations.newUser(user);
	}

	/**
	 * Updates an existing {@link User} in the DB.
	 *
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 */
	public boolean updateUser(User user) throws OHServiceException {
		return ioOperations.updateUser(user);
	}

	/**
	 * Updates the password of an existing {@link User} in the DB.
	 *
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 */
	public boolean updatePassword(User user) throws OHServiceException {
		return ioOperations.updatePassword(user);
	}

	/**
	 * Deletes an existing {@link User}.
	 *
	 * @param user - the {@link User} to delete
	 * @return <code>true</code> if the user has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteUser(User user) throws OHServiceException {
		if (user.getUserName().equals("admin")) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.userbrowser.theadminusercannotbedeleted.msg"),
					OHSeverityLevel.ERROR));
		}
		return ioOperations.deleteUser(user);
	}

	/**
	 * Returns the list of {@link UserGroup}s.
	 *
	 * @return the list of {@link UserGroup}s
	 */
	public List<UserGroup> getUserGroup() throws OHServiceException {
		return ioOperations.getUserGroup();
	}

	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for a specified {@link User}.
	 *
	 * @param aUser - the {@link User}
	 * @return the list of {@link UserMenuItem}s
	 */
	public List<UserMenuItem> getMenu(User aUser) throws OHServiceException {
		return ioOperations.getMenu(aUser);
	}

	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for the specified {@link UserGroup}.
	 *
	 * @param aGroup - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s
	 */
	public List<UserMenuItem> getGroupMenu(UserGroup aGroup) throws OHServiceException {
		return ioOperations.getGroupMenu(aGroup);
	}

	/**
	 * Replaces the {@link UserGroup} rights.
	 *
	 * @param aGroup - the {@link UserGroup}
	 * @param menu - the list of {@link UserMenuItem}s
	 * @return <code>true</code> if the menu has been replaced, <code>false</code> otherwise.
	 */
	public boolean setGroupMenu(UserGroup aGroup, List<UserMenuItem> menu) throws OHServiceException {
		return ioOperations.setGroupMenu(aGroup, menu, false);
	}

	/**
	 * Returns the {@link User} description given the username.
	 *
	 * @param userName - the {@link User}'s username
	 * @return the {@link User}'s description
	 */
	public String getUsrInfo(String userName) throws OHServiceException {
		return ioOperations.getUsrInfo(userName);
	}

	/**
	 * Deletes a {@link UserGroup}.
	 *
	 * @param aGroup - the {@link UserGroup} to delete
	 * @return <code>true</code> if the group has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteGroup(UserGroup aGroup) throws OHServiceException {
		if (aGroup.getCode().equals("admin")) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.groupsbrowser.theadmingroupcannotbedeleted.msg"),
					OHSeverityLevel.ERROR));
		}
		List<User> users = getUser(aGroup.getCode());
		if (users != null && !users.isEmpty()) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.groupsbrowser.thisgrouphasusersandcannotbedeleted.msg"),
					OHSeverityLevel.ERROR));
		}
		return ioOperations.deleteGroup(aGroup);
	}

	/**
	 * Insert a new {@link UserGroup} with a minimum set of rights.
	 *
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return <code>true</code> if the group has been inserted, <code>false</code> otherwise.
	 */
	public boolean newUserGroup(UserGroup aGroup) throws OHServiceException {
		String code = aGroup.getCode();
		if (ioOperations.isGroupNamePresent(code)) {
			throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.groupsbrowser.thegroupalreadyexists.fmt.msg", code),
					OHSeverityLevel.ERROR));
		}
		return ioOperations.newUserGroup(aGroup);
	}

	/**
	 * Updates an existing {@link UserGroup} in the DB.
	 *
	 * @param aGroup - the {@link UserGroup} to update
	 * @return <code>true</code> if the group has been updated, <code>false</code> otherwise.
	 */
	public boolean updateUserGroup(UserGroup aGroup) throws OHServiceException {
		return ioOperations.updateUserGroup(aGroup);
	}

	/**
	 * Tests whether a password meets the requirment for various characters being present
	 *
	 * @param password
	 * @return <code>true</code> if password is meets the minimum requirements, <code>false</code> otherwise.
	 */
	public boolean isPasswordStrong(String password) {
		if (password == null) {
			return false;
		}
		if (!GeneralData.STRONGPASSWORD) {
			return true;
		}

		String regex = "^(?=.*[0-9])"        // a digit must occur at least once
				+ "(?=.*[a-zA-Z])"           // a lower case or upper case alphabetic must occur at least once
				+ "(?=.*[\\\\_$&+,:;=\\\\?@#|/'<>.^*()%!-])" // a special character that must occur at least once
				+ "(?=\\S+$).+$";            // white spaces not allowed
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
