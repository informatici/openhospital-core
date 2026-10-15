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
package org.isf.patconsensus.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patconsensus.TestPatientConsensus;
import org.isf.patconsensus.service.PatientConmsensusIoOperationRepository;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PatientConsensusTest extends OHCoreTestCase {

	private static TestPatientConsensus testPatientConsensus;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientConmsensusIoOperationRepository patientConmsensusIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatientConsensus = new TestPatientConsensus();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void toString_shouldUsePatientConsensusFields() throws Exception {
		PatientConsensus patientConsensus = setupTestPatientConsensus(true);
		assertThat(patientConsensus.toString())
						.isEqualTo("PatientConsensus [id=1, consensusFlag=true, serviceFlag=false, patient=TestFirstName TestSecondName]");
		patientConsensus.setId(patientConsensus.getId() + 2);
		assertThat(patientConsensus.toString())
						.isEqualTo("PatientConsensus [id=3, consensusFlag=true, serviceFlag=false, patient=TestFirstName TestSecondName]");
	}

	public PatientConsensus setupTestPatientConsensus(boolean usingSet) throws OHException {
		PatientConsensus patientConsensus = testPatientConsensus.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patientConsensus.getPatient());
		return patientConmsensusIoOperationRepository.saveAndFlush(patientConsensus);
	}

}
