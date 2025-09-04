/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.encounter.service;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.encounter.model.Encounter;
import org.isf.encounter.model.EncounterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EncounterIoRepository extends JpaRepository<Encounter, Integer> {
	
	@Query(value = "select e from Encounter e where e.patient.code = :patientId")
    List<Encounter> findByPatient(@Param("patientId") Integer patientId);

	Encounter findByCode(String code);

	Encounter findByPatientCodeAndStatus(Integer patientCode, EncounterStatus encounterStatus);

	/**
	 * Checks if there exists an active encounter for a given patient at a specific date.
	 * <p>
	 * An encounter is considered <b>active</b> at a given date if:
	 * <ul>
	 *   <li>{@code performedAt <= date}</li>
	 *   <li>and ({@code closedAt IS NULL} or {@code closedAt >= date})</li>
	 * </ul>
	 * <p>
	 * This ensures that the encounter started before or at the given date,
	 * and has not yet been closed at that date (or is still ongoing).
	 *
	 * @param patientId the unique identifier of the patient
	 * @param date the date to check against
	 * @return {@code true} if there is at least one active encounter for the patient at the given date,
	 *         {@code false} otherwise
	 */
	@Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM Encounter e
        WHERE e.patient.id = :patientId
          AND e.performedAt <= :date
          AND (e.closedAt IS NULL OR e.closedAt >= :date)
    """)
	boolean existsActiveEncounterAt(
		@Param("patientId") Integer patientId,
		@Param("date") LocalDateTime date
	);
}
