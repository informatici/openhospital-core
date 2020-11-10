/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import org.isf.generaldata.MessageBundle;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.MenuIoOperations;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserBrowsingManager {

	@Autowired
	private MenuIoOperations ioOperations;
	
	
	public static String getCurrentUser() {
		return MDC.get("OHUser");
	}
	
	/**
	 * returns the list of {@link User}s
	 * @return the list of {@link User}s
	 */
	public ArrayList<User> getUser() throws OHServiceException {
        return ioOperations.getUser();
	}
	
	/**
	 * returns the list of {@link User}s in specified groupID
	 * @param groupID - the group ID
	 * @return the list of {@link User}s
	 */
	public ArrayList<User> getUser(String groupID) throws OHServiceException {
        return ioOperations.getUser(groupID);
	}
	
	/**
	 * returns the {@link User}
	 * @param userName - user name
	 * @return {@link User}
	 */
	public User getUserByName(String userName) throws OHServiceException {
        return ioOperations.getUserByName(userName);
	}
	
	/**
	 * Inserts a new {@link User} in the DB
	 * @param user - the {@link User} to insert
	 * @return <code>true</code> if the user has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newUser(User user) throws OHServiceException {
        String username = user.getUserName();
        if (ioOperations.isUserNamePresent(username)) {
            throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.menu.theuser") +
                            " " + username + " " + MessageBundle.getMessage("angal.menu.isalreadypresent"), OHSeverityLevel.ERROR));
        } else{
            return ioOperations.newUser(user);
        }
	}
	
	/**
	 * Updates an existing {@link User} in the DB
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 */
	public boolean updateUser(User user) throws OHServiceException {
        return ioOperations.updateUser(user);
	}
	
	/**
	 * Updates the password of an existing {@link User} in the DB
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 */
	public boolean updatePassword(User user) throws OHServiceException {
        return ioOperations.updatePassword(user);
	}
	
	/**
	 * Deletes an existing {@link User}
	 * @param user - the {@link User} to delete
	 * @return <code>true</code> if the user has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteUser(User user) throws OHServiceException {
        if (user.getUserName().equals("admin"))
            throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.menu.youcantdeleteadminuser"), OHSeverityLevel.ERROR));
        return ioOperations.deleteUser(user);
	}
	
	/**
	 * returns the list of {@link UserGroup}s
	 * @return the list of {@link UserGroup}s
	 */
	public ArrayList<UserGroup> getUserGroup() throws OHServiceException {
        return ioOperations.getUserGroup();
	}
	
	/**
	 * returns the list of {@link UserMenuItem}s that compose the menu for specified {@link User}
	 * @param aUser - the {@link User}
	 * @return the list of {@link UserMenuItem}s 
	 */
	public ArrayList<UserMenuItem> getMenu(User aUser) throws OHServiceException {
        return ioOperations.getMenu(aUser);
	}
	
	/**
	 * returns the list of {@link UserMenuItem}s that compose the menu for specified {@link UserGroup}
	 * @param aGroup - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s 
	 */
	public ArrayList<UserMenuItem> getGroupMenu(UserGroup aGroup) throws OHServiceException {
        return ioOperations.getGroupMenu(aGroup);
	}
	
	/**
	 * replaces the {@link UserGroup} rights
	 * @param aGroup - the {@link UserGroup}
	 * @param menu - the list of {@link UserMenuItem}s
	 * @return <code>true</code> if the menu has been replaced, <code>false</code> otherwise.
	 */
	public boolean setGroupMenu(UserGroup aGroup, ArrayList<UserMenuItem> menu) throws OHServiceException {
        return ioOperations.setGroupMenu(aGroup,menu,false);
	}
	
	/**
	 * returns {@link User} description from its username
	 * @param userName - the {@link User}'s username
	 * @return the {@link User}'s description
	 */
	public String getUsrInfo(String userName) throws OHServiceException {
        return ioOperations.getUsrInfo(userName);
	}
	
	/**
	 * Deletes a {@link UserGroup}
	 * @param aGroup - the {@link UserGroup} to delete
	 * @return <code>true</code> if the group has been deleted, <code>false</code> otherwise.
	 */
	public boolean deleteGroup(UserGroup aGroup) throws OHServiceException {
		if (aGroup.getCode().equals("admin")){
		    throw new OHDataValidationException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.menu.youcantdeletegroupadmin"), OHSeverityLevel.WARNING));
		}
		ArrayList<User> users = getUser(aGroup.getCode());
		if (users != null && users.size()>0){

            throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.menu.thisgrouphasusers"), OHSeverityLevel.WARNING));
		}
        return ioOperations.deleteGroup(aGroup);
	}
	
	/**
	 * Insert a new {@link UserGroup} with a minimum set of rights
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return <code>true</code> if the group has been inserted, <code>false</code> otherwise.
	 */
	public boolean newUserGroup(UserGroup aGroup) throws OHServiceException {
        String code = aGroup.getCode();
        if (ioOperations.isGroupNamePresent(code)) {
            throw new OHDataIntegrityViolationException(new OHExceptionMessage(MessageBundle.getMessage("angal.hospital"),
                    MessageBundle.getMessage("angal.menu.thegroup") +
                            " " + code + " " + MessageBundle.getMessage("angal.menu.isalreadypresent"), OHSeverityLevel.ERROR));
        } else{
            return ioOperations.newUserGroup(aGroup);
        }
	}
	
	/**
	 * Updates an existing {@link UserGroup} in the DB
	 * @param aGroup - the {@link UserGroup} to update
	 * @return <code>true</code> if the group has been updated, <code>false</code> otherwise.
	 */
	public boolean updateUserGroup(UserGroup aGroup) throws OHServiceException {
        return ioOperations.updateUserGroup(aGroup);
	}
}