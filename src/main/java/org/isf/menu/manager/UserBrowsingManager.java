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

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.MenuIoOperations;
import org.isf.permissions.model.Permission;
import org.isf.sessionaudit.model.UserSession;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.time.TimeTools;
import org.springframework.stereotype.Component;

@Component
public class UserBrowsingManager {

	private final MenuIoOperations ioOperations;
	private static final String VALID_USERID_PATTERN = "^[a-z0-9-._]+$";

	public UserBrowsingManager(MenuIoOperations menuIoOperations) {
		this.ioOperations = menuIoOperations;
	}

	public static String getCurrentUser() {
		if (UserSession.getUser() != null) {
			return UserSession.getUser().getUserName();
		}
		return null;
	}

	/**
	 * Returns the list of {@link User}s.
	 *
	 * @return the list of {@link User}s
	 * @throws OHServiceException When failed to retrieve users
	 */
	public List<User> getUser() throws OHServiceException {
		return ioOperations.getUser();
	}

	/**
	 * Returns the list of {@link User}s in specified groupID.
	 *
	 * @param groupID - the group ID
	 * @return the list of {@link User}s
	 * @throws OHServiceException When failed to retrieve group users
	 */
	public List<User> getUser(String groupID) throws OHServiceException {
		return ioOperations.getUser(groupID);
	}

	/**
	 * Returns a {@link User} with the specified name.
	 *
	 * @param userName - username
	 * @return {@link User}
	 * @throws OHServiceException When error occurs
	 */
	public User getUserByName(String userName) throws OHServiceException {
		return ioOperations.getUserByName(userName);
	}

	/**
	 * Inserts a new {@link User} into the DB.
	 *
	 * @param user - the {@link User} to insert
	 * @return the new {@link User}
	 * @throws OHServiceException When error occurs
	 */
	public User newUser(User user) throws OHServiceException {
		String username = user.getUserName();
		if (!username.matches(VALID_USERID_PATTERN)) {
			throw new OHDataValidationException(
							new OHExceptionMessage(MessageBundle.getMessage("angal.userbrowser.theusernamecontainsinvalidcharacters.msg")));
		}
		if (ioOperations.isUserNamePresent(username)) {
			throw new OHDataIntegrityViolationException(
							new OHExceptionMessage(MessageBundle.formatMessage("angal.userbrowser.theuseralreadyexists.fmt.msg", username)));
		}
		return ioOperations.newUser(user);
	}

	/**
	 * Updates an existing {@link User} in the DB.
	 *
	 * @param user - the {@link User} to update
	 * @return {@code true} if the user has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update user
	 */
	public boolean updateUser(User user) throws OHServiceException {
		return ioOperations.updateUser(user);
	}

	/**
	 * Updates the password of an existing {@link User} in the DB.
	 *
	 * @param user - the {@link User} to update
	 * @return {@code true} if the user has been updated, {@code false} otherwise.
	 * @throws OHServiceException When failed to update password
	 */
	public boolean updatePassword(User user) throws OHServiceException {
		return ioOperations.updatePassword(user);
	}

	/**
	 * Deletes an existing {@link User}.
	 *
	 * @param user - the {@link User} to delete
	 * @throws OHServiceException When failed to delete user
	 */
	public void deleteUser(User user) throws OHServiceException {
		if (user.getUserName().equals("admin")) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.userbrowser.theadminusercannotbedeleted.msg")));
		}
		ioOperations.deleteUser(user);
	}

	// TODO:  revisit the individual methods for failed attempts, locking, last login time, etc.
	// The original idea is that last login in time gets called frequently, and number of failed attempts less often, and locking/unlocking users
	// even more infrequently, etc. and only one or two columns changed value.  The thought was that rewriting the entire object everytime for each
	// operation was too heavy handed.
	/**
	 * Increase the number of failed login attempts for {@link User}.
	 *
	 * @param user the {@link User}
	 */
	public void increaseFailedAttempts(User user) {
		int newFailAttempts = user.getFailedAttempts() + 1;
		ioOperations.updateFailedAttempts(user.getUserName(), newFailAttempts);
	}

	/**
	 * Reset the number of failed login attempts to zero for {@link User}.
	 *
	 * @param user the {@link User}
	 */
	public void resetFailedAttempts(User user) {
		ioOperations.updateFailedAttempts(user.getUserName(), 0);
	}

	/**
	 * Lock the {@link User} from logging into the system.
	 *
	 * @param user the {@link User}
	 * @throws OHServiceException When failed to lock user
	 */
	public void lockUser(User user) throws OHServiceException {
		user.setAccountLocked(true);
		user.setLockedTime(TimeTools.getNow());
		ioOperations.updateUserLocked(user.getUserName(), true, user.getLockedTime());
	}

	/**
	 * Unlock the {@link User} so they can log into the system.
	 *
	 * @param user the {@link User}
	 */
	public void setLastLogin(User user) throws OHServiceException {
		ioOperations.setLastLogin(user.getUserName(), TimeTools.getNow());
	}

	/**
	 * Unlock the {@link User} so they can log into the system.
	 *
	 * @param user the {@link User}
	 */
	public void unlockUser(User user) throws OHServiceException {
		user.setAccountLocked(false);
		user.setLockedTime(null);
		user.setFailedAttempts(0);
		String userName = user.getUserName();
		ioOperations.updateFailedAttempts(userName, 0);
		ioOperations.updateUserLocked(userName, false, null);
		ioOperations.setLastLogin(userName, null);
	}

	/**
	 * Unlock the {@link User} after the required "lock time" has expired.
	 *
	 * @param user the {@link User}
	 */
	public boolean unlockWhenTimeExpired(User user) throws OHServiceException {
		LocalDateTime lockedTime = user.getLockedTime();
		if (lockedTime.plusMinutes(GeneralData.PASSWORDLOCKTIME).isBefore(TimeTools.getNow())) {
			user.setAccountLocked(false);
			user.setLockedTime(null);
			user.setFailedAttempts(0);
			String userName = user.getUserName();
			ioOperations.updateFailedAttempts(userName, 0);
			ioOperations.updateUserLocked(userName, false,null);
			ioOperations.setLastLogin(userName, null);
			return true;
		}
		return false;
	}

	/**
	 * Returns the list of {@link UserGroup}s.
	 *
	 * @return the list of {@link UserGroup}s
	 * @throws OHServiceException When failed to retrieve user groups
	 */
	public List<UserGroup> getUserGroup() throws OHServiceException {
		return ioOperations.getUserGroup();
	}

	/**
	 * Find user group by code
	 * @param groupCode UserGroup code
	 * @return The corresponding {@link UserGroup} if found, {@code null} otherwise
	 */
	public UserGroup findUserGroupByCode(String groupCode) {
		return ioOperations.findByCode(groupCode);
	}

	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for a specified {@link User}.
	 *
	 * @param aUser - the {@link User}
	 * @return the list of {@link UserMenuItem}s
	 * @throws OHServiceException When failed to retrieve user menus
	 */
	public List<UserMenuItem> getMenu(User aUser) throws OHServiceException {
		return ioOperations.getMenu(aUser);
	}

	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for the specified {@link UserGroup}.
	 *
	 * @param aGroup - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s
	 * @throws OHServiceException When failed to retrieve group menus
	 */
	public List<UserMenuItem> getGroupMenu(UserGroup aGroup) throws OHServiceException {
		return ioOperations.getGroupMenu(aGroup);
	}

	/**
	 * Replaces the {@link UserGroup} rights.
	 *
	 * @param aGroup - the {@link UserGroup}
	 * @param menu - the list of {@link UserMenuItem}s
	 * @return {@code true} if the menu has been replaced, {@code false} otherwise.
	 * @throws OHServiceException When error occurs
	 */
	public boolean setGroupMenu(UserGroup aGroup, List<UserMenuItem> menu) throws OHServiceException {
		return ioOperations.setGroupMenu(aGroup, menu);
	}

	/**
	 * Returns the {@link User} description given the username.
	 *
	 * @param userName - the {@link User}'s username
	 * @return the {@link User}'s description
	 * @throws OHServiceException When failed to get user info
	 */
	public String getUsrInfo(String userName) throws OHServiceException {
		return ioOperations.getUsrInfo(userName);
	}

	/**
	 * Deletes a {@link UserGroup}.
	 *
	 * @param aGroup - the {@link UserGroup} to delete
	 * @throws OHServiceException When failed to delete group
	 */
	public void deleteGroup(UserGroup aGroup) throws OHServiceException {
		if (aGroup.getCode().equals("admin")) {
			throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.groupsbrowser.theadmingroupcannotbedeleted.msg")));
		}
		List<User> users = getUser(aGroup.getCode());
		if (users != null && !users.isEmpty()) {
			throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(MessageBundle.getMessage("angal.groupsbrowser.thisgrouphasusersandcannotbedeleted.msg")));
		}
		ioOperations.deleteGroup(aGroup);
	}

	/**
	 * Insert a new {@link UserGroup} with a minimum set of rights.
	 *
	 * @param userGroup - the {@link UserGroup} to insert
	 * @return the new {@link UserGroup}
	 * @throws OHServiceException When failed to create user group
	 */
	public UserGroup newUserGroup(UserGroup userGroup) throws OHServiceException {
		String code = userGroup.getCode();
		if (ioOperations.isGroupNamePresent(code)) {
			throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(MessageBundle.formatMessage("angal.groupsbrowser.thegroupalreadyexists.fmt.msg", code)));
		}
		return ioOperations.newUserGroup(userGroup);
	}

	/**
	 * Insert a new {@link UserGroup} with related {@link Permission}s.
	 *
	 * @param userGroup - the {@link UserGroup} to insert
	 * @param permissions List of permissions to assign to the group
	 * @return the new {@link UserGroup}
	 * @throws OHServiceException When failed to create user group
	 */
	public UserGroup newUserGroup(UserGroup userGroup, List<Permission> permissions) throws OHServiceException {
		String code = userGroup.getCode();
		if (ioOperations.isGroupNamePresent(code)) {
			throw new OHDataIntegrityViolationException(
					new OHExceptionMessage(MessageBundle.formatMessage("angal.groupsbrowser.thegroupalreadyexists.fmt.msg", code)));
		}
		return ioOperations.newUserGroup(userGroup, permissions);
	}

	/**
	 * Updates an existing {@link UserGroup} in the DB.
	 *
	 * @param userGroup - the {@link UserGroup} to update
	 * @return {@code true} if the group has been updated, {@code false} otherwise.
	 */
	public boolean updateUserGroup(UserGroup userGroup) throws OHServiceException {
		return ioOperations.updateUserGroup(userGroup);
	}

	/**
	 * Updates an existing {@link UserGroup} and related permissions.
	 * If permissions list is empty, the existing permissions are kept,
	 * otherwise they're replaced with the provided ones.
	 *
	 * @param userGroup - the {@link UserGroup} to update
	 * @param permissions Updated list of permissions to assign to the group
	 * @return {@code true} if the group has been updated, {@code false} otherwise.
	 */
	public boolean updateUserGroup(UserGroup userGroup, List<Permission> permissions) throws OHServiceException {
		return ioOperations.updateUserGroup(userGroup, permissions);
	}

	/**
	 * Tests whether a password meets the requirement for various characters being present
	 *
	 * @param password The given password
	 * @return {@code true} if password is meets the minimum requirements, {@code false} otherwise.
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
