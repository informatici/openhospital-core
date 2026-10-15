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
package org.isf.anamnesis.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.anamnesis.TestPatientHistory;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientPatientHistoryTest extends OHCoreTestCase {

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetHistoryAndPatient() throws Exception {
		Patient patient = new Patient();
		TestPatient testPatient = new TestPatient();
		testPatient.setParameters(patient);
		TestPatientHistory testPatientHistory = new TestPatientHistory();
		PatientHistory patientHistory = testPatientHistory.setup();
		PatientPatientHistory patientPatientHistory = new PatientPatientHistory(patientHistory, patient);

		assertThat(patientPatientHistory.getPatientHistory()).isSameAs(patientHistory);
		assertThat(patientPatientHistory.getPatient()).isSameAs(patient);
	}

}
