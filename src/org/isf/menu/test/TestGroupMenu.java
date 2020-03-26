package org.isf.menu.test;


import static org.junit.Assert.assertEquals;

import org.isf.menu.model.GroupMenu;
import org.isf.utils.exception.OHException;

public class TestGroupMenu 
{	
    private String userGroup = "TestDescription";
    private String menuItem = "TestDescription";
    
			
	public GroupMenu setup(
			boolean usingSet) throws OHException 
	{
		GroupMenu groupMenu;
	
				
		if (usingSet)
		{
			groupMenu = new GroupMenu();
			_setParameters(groupMenu);
		}
		else
		{
			// Create GroupMenu with all parameters 
			groupMenu = new GroupMenu(userGroup, menuItem);
		}
				    	
		return groupMenu;
	}
	
	public void _setParameters(
			GroupMenu groupMenu) 
	{	
		groupMenu.setUserGroup(userGroup);
		groupMenu.setMenuItem(menuItem);
		
		return;
	}
	
	public void check(
			GroupMenu groupMenu) 
	{		
    	assertEquals(userGroup, groupMenu.getUserGroup());
    	assertEquals(menuItem, groupMenu.getMenuItem());
		
		return;
	}
}
