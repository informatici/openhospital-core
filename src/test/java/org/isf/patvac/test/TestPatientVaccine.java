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
package org.isf.patvac.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;

import org.isf.patient.model.Patient;
import org.isf.patvac.model.PatientVaccine;
import org.isf.utils.exception.OHException;
import org.isf.vaccine.model.Vaccine;

public class TestPatientVaccine {

	private int code = 0;
	private int progr = 10;
	private LocalDateTime vaccineDate = LocalDateTime.of(1984, Month.AUGUST, 14, 9, 0, 0);
	private int lock = 0;

	public PatientVaccine setup(Patient patient, Vaccine vaccine, boolean usingSet) throws OHException {
		PatientVaccine patientVaccine;

		if (usingSet) {
			patientVaccine = new PatientVaccine();
			setParameters(patient, vaccine, patientVaccine);
		} else {
			// Create PatientVaccine with all parameters 
			patientVaccine = new PatientVaccine(code, progr, vaccineDate, patient, vaccine, lock);
		}

		return patientVaccine;
	}

	public void setParameters(Patient patient, Vaccine vaccine, PatientVaccine patientVaccine) {
		patientVaccine.setCode(code);
		patientVaccine.setProgr(progr);
		patientVaccine.setVaccineDate(vaccineDate);
		patientVaccine.setPatient(patient);
		patientVaccine.setVaccine(vaccine);
		patientVaccine.setLock(lock);
	}

	public void check(PatientVaccine patientVaccine) {
		assertThat(patientVaccine.getLock()).isEqualTo(lock);
		assertThat(patientVaccine.getProgr()).isEqualTo(progr);
		assertThat(patientVaccine.getVaccineDate()).isEqualTo(vaccineDate);
	}
}
