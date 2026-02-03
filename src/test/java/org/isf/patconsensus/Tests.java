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
package org.isf.patconsensus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.patconsensus.manager.PatientConsensusBrowserManager;
import org.isf.patconsensus.model.PatientConsensus;
import org.isf.patconsensus.service.PatientConmsensusIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestPatientConsensus testPatientConsensus;
	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	PatientConmsensusIoOperationRepository patientConmsensusIoOperationRepository;
	@Autowired
	PatientConsensusBrowserManager patientConsensusBrowserManager;

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
	void testIsPresent() throws Exception {
		PatientConsensus patientConsensus = setupTestPatientConsensus(false);
		Integer userId = patientConsensus.getPatient().getCode();
		Optional<PatientConsensus> foundPatientConsensus = patientConsensusBrowserManager.getPatientConsensusByUserId(userId);
		assertThat(foundPatientConsensus.isPresent()).isTrue();
	}

	@Test
	void testExistsByPatientCode() throws Exception {
		PatientConsensus patientConsensus = setupTestPatientConsensus(true);
		Integer userId = patientConsensus.getPatient().getCode();
		assertThat(patientConsensusBrowserManager.existsByPatientCode(userId)).isTrue();
	}

	@Test
	void testUpdatePatientConsensus() throws Exception {
		PatientConsensus patientConsensus = setupTestPatientConsensus(true);
		patientConsensus.setServiceFlag(true);
		PatientConsensus updatedPatientConsensus = patientConsensusBrowserManager.updatePatientConsensus(patientConsensus);
		assertThat(updatedPatientConsensus.isServiceFlag()).isTrue();
	}

	@Test
	void testToString() throws Exception {
		PatientConsensus patientConsensus = setupTestPatientConsensus(true);
		assertThat(patientConsensus.toString())
						.isEqualTo("PatientConsensus [id=1, consensusFlag=true, serviceFlag=false, patient=TestFirstName TestSecondName]");
		patientConsensus.setId(patientConsensus.getId() + 2);
		assertThat(patientConsensus.toString())
						.isEqualTo("PatientConsensus [id=3, consensusFlag=true, serviceFlag=false, patient=TestFirstName TestSecondName]");
	}

	@Test
	void testUpdatePatientConsensusNotFoundException() throws Exception {
		assertThatThrownBy(() -> {
			PatientConsensus patientConsensus = setupTestPatientConsensus(true);
			patientConsensus.getPatient().setCode(-1);
			patientConsensusBrowserManager.updatePatientConsensus(patientConsensus);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
						(e -> ((OHServiceException) e).getMessages().size() == 1), "Patient not found."));
	}

	@Test
	void testUpdatePatientConsensusValidationException() throws Exception {
		assertThatThrownBy(() -> {
			PatientConsensus patientConsensus = setupTestPatientConsensus(true);
			patientConsensus.setConsensusFlag(false);
			patientConsensus.setServiceFlag(true);
			patientConsensusBrowserManager.updatePatientConsensus(patientConsensus);
		}).isInstanceOf(OHServiceException.class).has(new Condition<Throwable>(
						(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error"));
	}

	public PatientConsensus setupTestPatientConsensus(boolean usingSet) throws OHException {
		PatientConsensus patientConsensus = testPatientConsensus.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patientConsensus.getPatient());
		return patientConmsensusIoOperationRepository.saveAndFlush(patientConsensus);
	}
}
