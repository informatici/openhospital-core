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
package org.isf.menu.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.menu.model.GroupMenu;
import org.isf.menu.model.User;
import org.isf.menu.model.UserGroup;
import org.isf.menu.model.UserMenuItem;
import org.isf.menu.service.MenuIoOperations;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	private static DbJpaUtil jpa;
	private static TestUser testUser;
	private static TestUserContext testUserContext;
	private static TestUserGroup testUserGroup;
	private static TestUserGroupContext testUserGroupContext;
	private static TestUserMenu testUserMenu;
	private static TestUserMenuContext testUserMenuContext;
	private static TestGroupMenuContext testGroupMenuContext;
	private static TestGroupMenu testGroupMenu;
		

    @Autowired
    MenuIoOperations menuIoOperation;
	
	@BeforeClass
    public static void setUpClass()  
    {
    	jpa = new DbJpaUtil();
    	testUser = new TestUser();
    	testUserContext = new TestUserContext();
    	testUserGroup = new TestUserGroup();
    	testUserGroupContext = new TestUserGroupContext();
    	testUserMenu = new TestUserMenu();
    	testUserMenuContext = new TestUserMenuContext();
    	testGroupMenu = new TestGroupMenu();
    	testGroupMenuContext = new TestGroupMenuContext();
    }

    @Before
    public void setUp() throws OHException
    {
        jpa.open();
        
        _saveContext();
    }
        
    @After
    public void tearDown() throws Exception 
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {

    }
	
		
	@Test
	public void testUserGroupGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestUserGroup(false);
			_checkUserGroupIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testUserGroupSets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestUserGroup(true);
			_checkUserGroupIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	
	
	@Test
	public void testUserGets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestUser(false);
			_checkUserIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testUserSets() throws OHException 
	{
		String code = "";
			

		try 
		{		
			code = _setupTestUser(true);
			_checkUserIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testUserMenuGets() throws OHException 
	{
		String code = "";
			
	
		try 
		{		
			code = _setupTestUserMenu(false);
			_checkUserMenuIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testUserMenuSets() throws OHException 
	{
		String code = "";
			
	
		try 
		{		
			code = _setupTestUserMenu(true);
			_checkUserMenuIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}	
	
	@Test
	public void testGroupMenuGets() throws OHException 
	{
		Integer code = 0;
			
	
		try 
		
		{		
			code = _setupTestGroupMenu(false);
			_checkGroupMenuIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testGroupMenuSets() throws OHException 
	{
		Integer code = 0;
		
		
		try 
		{		
			code = _setupTestGroupMenu(true);
			_checkGroupMenuIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetUser() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			ArrayList<User> users = menuIoOperation.getUser();
			
			assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetUserFromId() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			ArrayList<User> users = menuIoOperation.getUser(foundUser.getUserGroupName().getCode());
			
			assertThat(users.get(users.size() - 1).getDesc()).isEqualTo(foundUser.getDesc());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetUserInfo() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			String description = menuIoOperation.getUsrInfo(foundUser.getUserName());
			
			assertThat(description).isEqualTo(foundUser.getDesc());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetUserGroup() 
	{
		String code = "";
		
		
		try 
		{		
			code = _setupTestUserGroup(false);
			UserGroup foundUserGroup = (UserGroup)jpa.find(UserGroup.class, code); 
			ArrayList<UserGroup> userGroups = menuIoOperation.getUserGroup();
			
			assertThat(userGroups.get(userGroups.size() - 1).getDesc()).isEqualTo(foundUserGroup.getDesc());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoIsUserNamePresent() 
	{
		String code = "admin";
		boolean result = false;
		

		try 
		{		
			code = _setupTestUser(false);
			result = menuIoOperation.isUserNamePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoIsGroupNamePresent() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestUserGroup(false);
			result = menuIoOperation.isGroupNamePresent(code);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewUser() 
	{
		boolean result = false; 
		
		
		try 
		{			
			UserGroup userGroup = testUserGroup.setup(false);
			
			jpa.beginTransaction();
			User user = testUser.setup(userGroup, false);
			jpa.persist(userGroup);
			jpa.commitTransaction();
			result = menuIoOperation.newUser(user);

			assertThat(result).isTrue();
			_checkUserIntoDb(user.getUserName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateUser()
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			foundUser.setDesc("Update");
			result = menuIoOperation.updateUser(foundUser);
			User updateUser = (User)jpa.find(User.class, code);

			assertThat(result).isTrue();
			assertThat(updateUser.getDesc()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void updatePassword()
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			foundUser.setPasswd("Update");
			result = menuIoOperation.updatePassword(foundUser);
			User updateDisease = (User)jpa.find(User.class, code);

			assertThat(result).isTrue();
			assertThat(updateDisease.getPasswd()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoDeleteDisease() 
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestUser(false);
			User foundUser = (User)jpa.find(User.class, code); 
			result = menuIoOperation.deleteUser(foundUser);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
		
	@Test
	public void testIoGetMenu() 
	{		
		try 
		{		
			jpa.beginTransaction();	
			UserGroup userGroup = testUserGroup.setup(false);
			User user = testUser.setup(userGroup, false);
			UserMenuItem menuItem = testUserMenu.setup(false);
			GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
			jpa.persist(userGroup);
			jpa.persist(user);
			jpa.persist(menuItem);
			jpa.persist(groupMenu);
			jpa.commitTransaction();
			 
			ArrayList<UserMenuItem> menus = menuIoOperation.getMenu(user);
			
			assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoGetGroupMenu() 
	{		
		try 
		{		
			jpa.beginTransaction();	
			UserGroup userGroup = testUserGroup.setup(false);
			User user = testUser.setup(userGroup, false);
			UserMenuItem menuItem = testUserMenu.setup(false);
			GroupMenu groupMenu = new GroupMenu(userGroup.getCode(), menuItem.getCode());
			jpa.persist(userGroup);
			jpa.persist(user);
			jpa.persist(menuItem);
			jpa.persist(groupMenu);
			jpa.commitTransaction();
			 
			ArrayList<UserMenuItem> menus = menuIoOperation.getGroupMenu(userGroup);
			
			assertThat(menus.get(menus.size() - 1).getCode()).isEqualTo(menuItem.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoSetGroupMenu() 
	{
		//TODO: Do unit test checking insert
		assertThat(true).isTrue();
	}
	
	@Test
	public void testIoDeleteUserGroup() 
	{
		String code = "";
		boolean result = false;
		

		try 
		{		
			code = _setupTestUserGroup(false);
			UserGroup foundUserGroup = (UserGroup)jpa.find(UserGroup.class, code); 
			result = menuIoOperation.deleteGroup(foundUserGroup);

			assertThat(result).isTrue();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewUserGroup() 
	{
		boolean result = false; 
		
		
		try 
		{						
			UserGroup userGroup= testUserGroup.setup(false);
			result = menuIoOperation.newUserGroup(userGroup);

			assertThat(result).isTrue();
			_checkUserGroupIntoDb(userGroup.getCode());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateUserGroup()
	{
		String code = "";
		boolean result = false;
		
		
		try 
		{		
			code = _setupTestUserGroup(false);
			UserGroup foundUserGroup = (UserGroup)jpa.find(UserGroup.class, code); 
			foundUserGroup.setDesc("Update");
            result = menuIoOperation.updateUserGroup(foundUserGroup);
			UserGroup updateUserGroup = (UserGroup)jpa.find(UserGroup.class, code);

			assertThat(result).isTrue();
			assertThat(updateUserGroup.getDesc()).isEqualTo("Update");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
	}
	
	
	private void _saveContext() throws OHException 
    {	
		testUserGroupContext.saveAll(jpa);
		testUserContext.saveAll(jpa);
		testUserMenuContext.saveAll(jpa);
		testGroupMenuContext.saveAll(jpa);
    }
	
    private void _restoreContext() throws OHException 
    {
		testUserContext.deleteNews(jpa);
		testUserGroupContext.deleteNews(jpa);
		testUserMenuContext.deleteNews(jpa);
		testGroupMenuContext.deleteNews(jpa);
    }
        
	private String _setupTestUserGroup(
			boolean usingSet) throws OHException 
	{
		UserGroup userGroup;
		

    	jpa.beginTransaction();	
    	userGroup = testUserGroup.setup(usingSet);
		jpa.persist(userGroup);
    	jpa.commitTransaction();
    	
		return userGroup.getCode();
	}
		
	private void  _checkUserGroupIntoDb(
			String code) throws OHException 
	{
		UserGroup foundUserGroup;
		

		foundUserGroup = (UserGroup)jpa.find(UserGroup.class, code); 
		testUserGroup.check(foundUserGroup);
	}	
    
	private String _setupTestUser(
			boolean usingSet) throws OHException 
	{
		User user;
		UserGroup userGroup = testUserGroup.setup(usingSet);
		
	
		jpa.beginTransaction();	
		user = testUser.setup(userGroup, usingSet);
		jpa.persist(userGroup);
		jpa.persist(user);
		jpa.commitTransaction();
		
		return user.getUserName();
	}
		
	private void  _checkUserIntoDb(
			String code) throws OHException 
	{
		User foundUser;
		
	
		foundUser = (User)jpa.find(User.class, code); 
		testUser.check(foundUser);
	}		
	
	private String _setupTestUserMenu(
			boolean usingSet) throws OHException 
	{
		UserMenuItem userMenu;
		
	
		jpa.beginTransaction();	
		userMenu = testUserMenu.setup(usingSet);
		jpa.persist(userMenu);
		jpa.commitTransaction();
		
		return userMenu.getCode();
	}
		
	private void  _checkUserMenuIntoDb(
			String code) throws OHException 
	{
		UserMenuItem foundUserMenu;
		
	
		foundUserMenu = (UserMenuItem)jpa.find(UserMenuItem.class, code); 
		testUserMenu.check(foundUserMenu);
	}
	
	private Integer _setupTestGroupMenu(
			boolean usingSet) throws OHException 
	{
		GroupMenu groupMenu;
		
	
		jpa.beginTransaction();	
		groupMenu = testGroupMenu.setup(usingSet);
		jpa.persist(groupMenu);
		jpa.commitTransaction();
		
		return groupMenu.getCode();
	}
		
	private void  _checkGroupMenuIntoDb(
			Integer code) throws OHException 
	{
		GroupMenu foundGroupMenu;
		
	
		foundGroupMenu = (GroupMenu)jpa.find(GroupMenu.class, code); 
		testGroupMenu.check(foundGroupMenu);
	}
}
