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
package org.isf.menu.service;

import org.isf.menu.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserIoOperationRepository extends JpaRepository<User, String> {
    List<User> findAllByOrderByUserNameAsc();

    User findByUserName(String userName);
    
    @Query(value = "select user from User user where user.userGroupName.code=:groupId order by user.userName")
    List<User> findAllWhereUserGroupNameByOrderUserNameAsc(@Param("groupId") String groupId);

    @Modifying
    @Transactional
    @Query(value = "update User user set user.desc=:description where user.userName=:id")
    int updateDescription(@Param("description") String description, @Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "update User set passwd=:password where userName=:id")
    int updatePassword(@Param("password") String password, @Param("id") String id);
}