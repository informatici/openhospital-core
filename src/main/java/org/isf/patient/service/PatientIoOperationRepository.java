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
package org.isf.patient.service;

import java.util.List;
import java.util.Map;

import org.isf.patient.model.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientIoOperationRepository extends JpaRepository<Patient, Integer>, PatientIoOperationRepositoryCustom {

	List<Patient> findByDeletedOrDeletedIsNull(char deletionStatus);

	List<Patient> findAllByDeletedIsNullOrDeletedEqualsOrderByName(char patDeleted, Pageable pageable);

	@Query("select p from Patient p where p.name = :name and (p.deleted = :deletedStatus or p.deleted is null) order by p.secondName, p.firstName")
	List<Patient> findByNameAndDeletedOrderByName(@Param("name") String name, @Param("deletedStatus") char deletedStatus);

	@Query("select p from Patient p where p.code = :id and (p.deleted = :deletedStatus or p.deleted is null)")
	List<Patient> findAllWhereIdAndDeleted(@Param("id") Integer id, @Param("deletedStatus") char deletedStatus);

	@Modifying
	@Query(value = "update Patient p set p.deleted = 'Y' where p.code = :id")
	int updateDeleted(@Param("id") Integer id);

	List<Patient> findByNameAndDeleted(String name, char deletedStatus);

	@Query(value = "select max(p.code) from Patient p")
	Integer findMaxCode();
	
	@Query(value = "select distinct p.city from Patient p")
	List<String> findCities();

	List<Patient> getPatientsByParams(Map<String, Object> params);

}
