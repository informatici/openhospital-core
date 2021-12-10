/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicals.service;

import java.util.List;

import org.isf.medicals.model.Medical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalsIoOperationRepository extends JpaRepository<Medical, Integer> {

	@Query(value = "SELECT m FROM Medical m where m.description like :description order BY m.description")
	List<Medical> findAllWhereDescriptionOrderByDescription(@Param("description") String description);

	@Query(value = "SELECT m FROM Medical m order BY m.description")
	List<Medical> findAllByOrderByDescription();

	@Query(value = "SELECT m FROM Medical m where m.type.description like :type order BY m.description")
	List<Medical> findAllWhereTypeOrderByDescription(@Param("type") String type);

	@Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and (m.type.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
	List<Medical> findAllWhereDescriptionAndTypeAndCriticalOrderByTypeAndDescription(@Param("description") String description, @Param("type") String type);

	@Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and (m.type.code=:type) order BY m.type.description, m.description")
	List<Medical> findAllWhereDescriptionAndTypeOrderByTypeAndDescription(@Param("description") String description, @Param("type") String type);

	@Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
	List<Medical> findAllWhereDescriptionAndCriticalOrderByTypeAndDescription(@Param("description") String description);

	@Query(value = "SELECT m FROM Medical m where (m.description like %:description% OR m.prod_code like %:description%) order BY m.type.description, m.description")
	List<Medical> findAllWhereDescriptionOrderByTypeAndDescription(@Param("description") String description);

	@Query(value = "SELECT m FROM Medical m where (m.type.code=:type) and ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
	List<Medical> findAllWhereTypeAndCriticalOrderByTypeAndDescription(@Param("type") String type);

	@Query(value = "SELECT m FROM Medical m where (m.type.code=:type) order BY m.type.description, m.description")
	List<Medical> findAllWhereTypeOrderByTypeAndDescription(@Param("type") String type);

	@Query(value = "SELECT m FROM Medical m where ((m.initialqty+m.inqty-m.outqty)<m.minqty) order BY m.type.description, m.description")
	List<Medical> findAllWhereCriticalOrderByTypeAndDescription();

	@Query(value = "SELECT m FROM Medical m order BY m.type.description, m.description")
	List<Medical> findAllByOrderByTypeAndDescription();

	@Query(value = "SELECT m FROM Medical m WHERE m.type.code = :type AND m.description = :description")
	Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type);

	@Query(value = "SELECT m FROM Medical m WHERE m.type.code = :type AND m.description = :description AND m.code <> :id")
	Medical findOneWhereDescriptionAndType(@Param("description") String description, @Param("type") String type, @Param("id") Integer id);

	@Query(value = "SELECT m FROM Medical m WHERE m.description LIKE :description")
	List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description);

	@Query(value = "SELECT m FROM Medical m WHERE m.description LIKE :description AND m.code <> :id")
	List<Medical> findAllWhereDescriptionSoundsLike(@Param("description") String description, @Param("id") Integer id);

	@Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code")
	Medical findOneWhereProductCode(@Param("prod_code") String prod_code);

	@Query(value = "SELECT m FROM Medical m WHERE m.prod_code = :prod_code AND m.code <> :id")
	Medical findOneWhereProductCode(@Param("prod_code") String prod_code, @Param("id") Integer id);

	@Query(value = "SELECT m FROM Medical m WHERE m.type.description LIKE %:type% ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
	List<Medical> findAllWhereTypeOrderBySmartCodeAndDescription(@Param("type") String type);

	@Query(value = "SELECT m FROM Medical m ORDER BY LENGTH(m.prod_code), m.prod_code, m.description")
	List<Medical> findAllOrderBySmartCodeAndDescription();

}
