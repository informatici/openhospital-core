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
package org.isf.permissions.service;

import java.util.List;

import org.isf.permissions.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionIoOperationRepository extends JpaRepository<Permission, Integer> {

	@Query(value = "FROM Permission p WHERE p.active=1 and p.id in (select permission.id from GroupPermission where active=1 and userGroup.code like :userGroupCode)")
	List<Permission> findAllByUserGroupCode(@Param("userGroupCode") String userGroupCode);

	@Query(value = "FROM Permission p WHERE p.active=1 and p.id in (select permission.id from GroupPermission where active=1 and userGroup.code in (select userGroupName from User where active=1 and userName like :currentUserName))")
	List<Permission> retrievePermissionsByCurrentLoggedInUser(@Param("currentUserName") String currentUserName);

	Permission findByName(String name);

}