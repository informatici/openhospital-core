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
package org.isf.medicalstockward.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicalstock.TestLot;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstockward.TestMovementWard;
import org.isf.medicalstockward.model.MovementWard;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class MovementWardPatientMergedEventListenerTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestMovementWard testMovementWard;
	private static TestLot testLot;

	@Autowired
	MovementWardIoOperationRepository movementWardIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@BeforeAll
	static void setUpClass() {
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testWard = new TestWard();
		testPatient = new TestPatient();
		testMovementWard = new TestMovementWard();
		testLot = new TestLot();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void handle_shouldUpdatePatientToMergedPatient_whenPatientMergedEventArrives() throws Exception {
		// given:
		int id = setupTestMovementWard(false);
		MovementWard foundMovementWard = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(foundMovementWard).isNotNull();
		Patient mergedPatient = setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(foundMovementWard.getPatient(), mergedPatient));

		// then:
		MovementWard result = movementWardIoOperationRepository.findById(id).orElse(null);
		assertThat(result).isNotNull();
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	private Patient setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int setupTestMovementWard(boolean usingSet) throws OHException {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(false);
		Lot lot = testLot.setup(medical, false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		lotIoOperationRepository.saveAndFlush(lot);
		MovementWard movementWard = testMovementWard.setup(ward, patient, medical, ward, ward, lot, usingSet);
		movementWardIoOperationRepository.saveAndFlush(movementWard);
		return movementWard.getCode();
	}

}
