/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.cares;

import org.isf.cares.model.Care;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCare {

	private final List<String> TEAM = List.of("admin");
	private final String OBSERVATION = "Good observation";
	private final String PLANNED_CARE = "Planned care";
	private final String NOTE = "Good treatment";
	private final LocalDateTime CARE_DATE = LocalDateTime.of(2026, 3, 9, 9, 30);

	public Care setup(Patient patient, boolean usingSet) throws OHException {
		Care care;

		if (usingSet) {
			care = new Care();
			setParameters(care, patient);
		} else {
			care = new Care(null, TEAM, OBSERVATION, PLANNED_CARE, NOTE, CARE_DATE, patient);
		}

		return care;
	}

	public void setParameters(Care care, Patient patient) {
		care.setTeam(TEAM);
		care.setCareDate(CARE_DATE);
		care.setPlannedCare(PLANNED_CARE);
		care.setNote(NOTE);
		care.setObservation(OBSERVATION);
		care.setPatient(patient);
	}

	public void check(Care care) {
		assertThat(care.getCareDate()).isEqualTo(CARE_DATE);
		assertThat(care.getPlannedCare()).isEqualTo(PLANNED_CARE);
		assertThat(care.getNote()).isEqualTo(NOTE);
		assertThat(care.getObservation()).isEqualTo(OBSERVATION);
		assertThat(care.getTeam()).isEqualTo(TEAM);
	}
}
