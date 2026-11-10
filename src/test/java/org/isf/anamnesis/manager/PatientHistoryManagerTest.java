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
package org.isf.anamnesis.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.anamnesis.TestPatientHistory;
import org.isf.anamnesis.model.PatientHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientHistoryManagerTest extends OHCoreTestCase {

	private static TestPatientHistory testPatientHistory;

	@Autowired
	PatientHistoryManager patientHistoryManager;

	@BeforeAll
	static void setUpClass() {
		testPatientHistory = new TestPatientHistory();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void saveOrUpdate_shouldSaveHistory() throws Exception {
		PatientHistory patientHistory = testPatientHistory.setup();
		PatientHistory savedPatientHistory = patientHistoryManager.saveOrUpdate(patientHistory);
		assertThat(savedPatientHistory).isNotNull();
	}

	@Test
	void getByPatientId_shouldReturnSavedHistoryOrNull() throws Exception {
		PatientHistory patientHistory = testPatientHistory.setup();
		PatientHistory savedPatientHistory = patientHistoryManager.saveOrUpdate(patientHistory);
		int patientID = savedPatientHistory.getId();
		PatientHistory byIDPatientHistory = patientHistoryManager.getByPatientId(patientID);
		assertThat(byIDPatientHistory).isNotNull();
		assertThat(savedPatientHistory.compareTo(byIDPatientHistory)).isZero();

		assertThat(patientHistoryManager.getByPatientId(patientID + 99)).isNull();
	}

}
