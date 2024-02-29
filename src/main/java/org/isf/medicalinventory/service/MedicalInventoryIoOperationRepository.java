/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.medicalinventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.medicalinventory.model.MedicalInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import feign.Param;

@Repository
public interface MedicalInventoryIoOperationRepository extends JpaRepository<MedicalInventory, Integer> {

	@Query(value = "select medinv from MedicalInventory medinv where medinv.reference LIKE %:reference% ")
	MedicalInventory findByReference(@Param("reference") String reference);
	
	@Query(value = "select medinv from MedicalInventory medinv where medinv.status LIKE %:status% and medinv.wardCode LIKE %:wardCode%")
	List<MedicalInventory> findInventoryByStatusAnvWardCode(@Param("status") String status, @Param("wardCode") String wardCode);
	
	@Query(value = "select medinv from MedicalInventory medinv where medinv.status LIKE %:status% ")
	List<MedicalInventory> findInventoryByStatus(@Param("status") String status);

	@Query(value = "select medinv from MedicalInventory medinv where medinv.date >= :dateFrom and medinv.date < :dateTo "
					+ "and medinv.status LIKE %:status% and medinv.type LIKE %:type%")
	List<MedicalInventory> findInventoryByParams(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, 
					@Param("status") String status, @Param("type") String type);
	
	@Query(value = "select medinv from MedicalInventory medinv where medinv.date >= :dateFrom and medinv.date < :dateTo "
					+ "and medinv.status LIKE %:status% and medinv.type LIKE %:type%")
	Page<MedicalInventory> findInventoryByParamsPageable(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, 
					@Param("status") String status, @Param("type") String type, Pageable pageable);

}
