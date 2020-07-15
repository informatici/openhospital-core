package org.isf.menu.service;

import org.isf.menu.model.UserMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMenuItemIoOperationRepository extends JpaRepository<UserMenuItem, String> {

    @Query(value = "select menuItem.code, menuItem.buttonLabel, menuItem.altLabel, menuItem.tooltip, menuItem.shortcut, " +
			"menuItem.mySubmenu, menuItem.myClass, menuItem.isASubMenu, menuItem.position, groupMenu.active " +
			"from UserMenuItem menuItem, GroupMenu groupMenu, UserGroup  userGroup, User user " +
			"where (user.userName=:id) " +
			"and (user.userGroupName=userGroup.code) " +
			"and (userGroup.code=groupMenu.userGroup) " +
			"and (menuItem.code=groupMenu.menuItem) " +
			"order by menuItem.position")
    List<Object[]> findAllWhereId(@Param("id") String id);

    @Query(value = "select menuItem.code, menuItem.buttonLabel, menuItem.altLabel, menuItem.tooltip, menuItem.shortcut, " +
			"menuItem.mySubmenu, menuItem.myClass, menuItem.isASubMenu, menuItem.position, groupMenu.active " +
			"from UserMenuItem menuItem, GroupMenu groupMenu, UserGroup  userGroup, User user " +
			"where userGroup.code=:groupId " +
			"and (user.userGroupName=userGroup.code) " +
			"and (userGroup.code=groupMenu.userGroup) " +
			"and (menuItem.code=groupMenu.menuItem) " +
			"order by menuItem.position")
    List<Object[]> findAllWhereGroupId(@Param("groupId") String groupId);
    
}