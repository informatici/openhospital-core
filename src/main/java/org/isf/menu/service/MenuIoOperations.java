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
package org.isf.menu.service;

import java.util.ArrayList;
import java.util.List;

import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.utils.db.TranslateOHServiceException;
import org.isf.utils.exception.OHServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor=OHServiceException.class)
@TranslateOHServiceException
public class MenuIoOperations 
{
	@Autowired
	private UserIoOperationRepository repository;
	@Autowired
	private UserGroupIoOperationRepository groupRepository;
	@Autowired
	private UserMenuItemIoOperationRepository menuRepository;
	@Autowired
	private GroupMenuIoOperationRepository groupMenuRepository;
	
	/**
	 * Returns the list of {@link User}s
	 * 
	 * @return the list of {@link User}s
	 * @throws OHServiceException
	 */
	public List<User> getUser() throws OHServiceException {
		return repository.findAllByOrderByUserNameAsc();
	}

	/**
	 * Returns the list of {@link User}s in specified groupID
	 * 
	 * @param groupID - the group ID
	 * @return the list of {@link User}s
	 * @throws OHServiceException
	 */
	public List<User> getUser(String groupID) throws OHServiceException {
		return repository.findAllWhereUserGroupNameByOrderUserNameAsc(groupID);
	}
	
	/**
	 * Returns {@link User} from its username
	 * @param userName - the {@link User}'s username
	 * @return {@link User}
	 * @throws OHServiceException
	 */
	public User getUserByName(String userName) throws OHServiceException {
		return repository.findByUserName(userName);
	}
	
	/**
	 * Returns {@link User} description from its username
	 * @param userName - the {@link User}'s username
	 * @return the {@link User}'s description
	 * @throws OHServiceException
	 */
	public String getUsrInfo(String userName) throws OHServiceException {
		User user = repository.findById(userName).orElse(null);
		return user.getDesc();
	}
	
	/**
	 * Returns the list of {@link UserGroup}s
	 * 
	 * @return the list of {@link UserGroup}s
	 * @throws OHServiceException
	 */
	public List<UserGroup> getUserGroup() throws OHServiceException {
		return groupRepository.findAllByOrderByCodeAsc();
	}
	
	/**
	 * Checks if the specified {@link User} code is already present.
	 * 
	 * @param userName - the {@link User} code to check.
	 * @return <code>true</code> if the medical code is already stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isUserNamePresent(String userName) throws OHServiceException {
		return repository.existsById(userName);
	}
	
	/**
	 * Checks if the specified {@link UserGroup} code is already present.
	 * 
	 * @param groupName - the {@link UserGroup} code to check.
	 * @return <code>true</code> if the medical code is already stored, <code>false</code> otherwise.
	 * @throws OHServiceException if an error occurs during the check.
	 */
	public boolean isGroupNamePresent(String groupName) throws OHServiceException {
		return groupRepository.existsById(groupName);
	}
	
	/**
	 * Inserts a new {@link User} in the DB
	 * 
	 * @param user - the {@link User} to insert
	 * @return <code>true</code> if the user has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean newUser(User user) throws OHServiceException {
		return repository.save(user) != null;
	}
		
	/**
	 * Updates an existing {@link User} in the DB
	 * 
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updateUser(User user) throws OHServiceException {
		return repository.updateUser(user.getDesc(), user.getUserGroupName(), user.getUserName()) > 0;
	}
	
	/**
	 * Updates the password of an existing {@link User} in the DB
	 * 
	 * @param user - the {@link User} to update
	 * @return <code>true</code> if the user has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean updatePassword(User user) throws OHServiceException {
		return repository.updatePassword(user.getPasswd(), user.getUserName()) > 0;
	}

	/**
	 * Deletes an existing {@link User}
	 * 
	 * @param user - the {@link User} to delete
	 * @return <code>true</code> if the user has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException
	 */
	public boolean deleteUser(User user) throws OHServiceException {
		repository.delete(user);
		return true;
	}
	
	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for specified {@link User}
	 * 
	 * @param aUser - the {@link User}
	 * @return the list of {@link UserMenuItem}s 
	 * @throws OHServiceException
	 */
	public List<UserMenuItem> getMenu(User aUser) throws OHServiceException {
		List<Object[]> menuList = menuRepository.findAllWhereUserId(aUser.getUserName());
		List<UserMenuItem> menu = new ArrayList<>();
		for (Object[] object : menuList) {
			UserMenuItem umi = new UserMenuItem();
			umi.setCode((String) object[0]);
			umi.setButtonLabel((String) object[1]);
			umi.setAltLabel((String) object[2]);
			umi.setTooltip((String) object[3]);
			umi.setShortcut((Character) object[4]);
			umi.setMySubmenu((String) object[5]);
			umi.setMyClass((String) object[6]);
			umi.setASubMenu((Boolean) object[7]);
			umi.setPosition((Integer) object[8]);
			umi.setActive((Integer) object[9] == 1);
			menu.add(umi);
		}
		return menu;
	}

	/**
	 * Returns the list of {@link UserMenuItem}s that compose the menu for specified {@link UserGroup}
	 * 
	 * @param aGroup - the {@link UserGroup}
	 * @return the list of {@link UserMenuItem}s 
	 * @throws OHServiceException
	 */
	public List<UserMenuItem> getGroupMenu(UserGroup aGroup) throws OHServiceException {
		List<Object[]> menuList = menuRepository.findAllWhereGroupId(aGroup.getCode());
		List<UserMenuItem> menu = new ArrayList<>();
		for (Object[] object : menuList) {
			boolean active = (Integer) object[9] == 1;
			UserMenuItem umi = new UserMenuItem();
			umi.setCode((String) object[0]);
			umi.setButtonLabel((String) object[1]);
			umi.setAltLabel((String) object[2]);
			umi.setTooltip((String) object[3]);
			umi.setShortcut((Character) object[4]);
			umi.setMySubmenu((String) object[5]);
			umi.setMyClass((String) object[6]);
			umi.setASubMenu((Boolean) object[7]);
			umi.setPosition((Integer) object[8]);
			umi.setActive(active);
			menu.add(umi);
		}
		return menu;
	}

	/**
	 * Replaces the {@link UserGroup} rights
	 * 
	 * @param aGroup - the {@link UserGroup}
	 * @param menu - the list of {@link UserMenuItem}s
	 * @param insert - specify if is an insert or an update
	 * @return <code>true</code> if the menu has been replaced, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean setGroupMenu(UserGroup aGroup, List<UserMenuItem> menu, boolean insert) throws OHServiceException {
		boolean result = true;
		result = deleteGroupMenu(aGroup);
		for (UserMenuItem item : menu) {
			result = result && insertGroupMenu(aGroup, item, insert);
		}
		return result;
	}

	private boolean deleteGroupMenu(UserGroup aGroup) throws OHServiceException {
		groupMenuRepository.deleteWhereUserGroup(aGroup.getCode());
		return true;
	}

	private boolean insertGroupMenu(UserGroup aGroup, UserMenuItem item, boolean insert) throws OHServiceException {
		GroupMenu groupMenu = new GroupMenu();
		groupMenu.setUserGroup(aGroup.getCode());
		groupMenu.setMenuItem(item.getCode());
		groupMenu.setActive((item.isActive() ? 1 : 0));
		groupMenuRepository.save(groupMenu);
		return true;
	}
	
	/**
	 * Deletes a {@link UserGroup}
	 * 
	 * @param aGroup - the {@link UserGroup} to delete
	 * @return <code>true</code> if the group has been deleted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean deleteGroup(UserGroup aGroup) throws OHServiceException {
		groupMenuRepository.deleteWhereUserGroup(aGroup.getCode());
		groupRepository.delete(aGroup);
		return true;
	}

	/**
	 * Insert a new {@link UserGroup} with a minimum set of rights
	 * 
	 * @param aGroup - the {@link UserGroup} to insert
	 * @return <code>true</code> if the group has been inserted, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean newUserGroup(UserGroup aGroup) throws OHServiceException {
		return groupRepository.save(aGroup) != null;
	}

	/**
	 * Updates an existing {@link UserGroup} in the DB
	 * 
	 * @param aGroup - the {@link UserGroup} to update
	 * @return <code>true</code> if the group has been updated, <code>false</code> otherwise.
	 * @throws OHServiceException 
	 */
	public boolean updateUserGroup(UserGroup aGroup) throws OHServiceException {
		return groupRepository.updateDescription(aGroup.getDesc(), aGroup.getCode()) > 0;
	}

}
