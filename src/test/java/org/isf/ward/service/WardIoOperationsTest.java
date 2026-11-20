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
package org.isf.ward.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WardIoOperationsTest extends OHCoreTestCase {

	private static TestWard testWard;

	@Autowired
	WardIoOperations wardIoOperation;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getCurrentOccupation_shouldReturnZero_whenWardHasNoAdmissions() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		assertThat(wardIoOperation.getCurrentOccupation(ward)).isZero();
	}

	@Test
	void getCurrentOccupation_shouldCountAdmissionsInWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		Patient patient = new Patient();
		patient.setBirthDate(LocalDate.now().minusYears(45));
		patientIoOperationRepository.save(patient);
		LocalDateTime admDate = TimeTools.getNow();
		AdmissionType admissionType = new AdmissionType("ZZ", "TestDescription");
		Admission admission1 = new Admission(0, 1, "N", ward, 0, patient, admDate, admissionType,
			"TestFHU", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			"TestUserId", 'N');
		Admission admission2 = new Admission(0, 1, "N", ward, 0, patient, admDate, admissionType,
			"TestFHU", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			"TestUserId", 'N');
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThat(wardIoOperation.getCurrentOccupation(ward)).isEqualTo(2);
	}

	@Test
	void getWardsNoMaternity_shouldReturnNonMaternityWard() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();

		// when:
		List<Ward> wards = wardIoOperation.getWardsNoMaternity();

		// then:
		assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(foundWard.getDescription());
	}

	@Test
	void getIpdWards_shouldReturnOnlyWardsWithBeds() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();

		// when:
		List<Ward> wards = wardIoOperation.getIpdWards();

		// then:
		assertThat(wards).hasSize(1);

		// given:
		foundWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(foundWard);

		// when:
		wards = wardIoOperation.getIpdWards();

		// then:
		assertThat(wards).isEmpty();
	}

	@Test
	void getOpdWards_shouldReturnOpdWardRegardlessOfBeds() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();

		// when:
		List<Ward> wards = wardIoOperation.getOpdWards();

		// then:
		assertThat(wards).hasSize(1);

		// given:
		foundWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(foundWard);

		// when:
		wards = wardIoOperation.getOpdWards();

		// then:
		assertThat(wards).hasSize(1);
	}

	@Test
	void findAll_shouldReturnSavedWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		List<Ward> wards = wardIoOperation.findAll();
		assertThat(wards).hasSize(1);
		assertThat(wards.get(0).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	void newWard_shouldSaveWard() throws Exception {
		Ward ward = testWard.setup(true);
		Ward newWard = wardIoOperation.newWard(ward);
		assertThat(newWard.getDescription()).isEqualTo("TestDescription");
		checkWardIntoDb(newWard.getCode());
	}

	@Test
	void updateWard_shouldSaveNewDescription() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();
		foundWard.setDescription("Update");

		// when:
		Ward updatedWard = wardIoOperation.updateWard(foundWard);

		// then:
		assertThat(updatedWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void updateWard_shouldReturnWard_whenCodeIsNotPresent() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("X");
		Ward updatedWard = wardIoOperation.updateWard(ward);
		assertThat(updatedWard.getCode()).isEqualTo("X");
	}

	@Test
	void deleteWard_shouldRemoveWard() throws Exception {
		// given:
		String code = setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();

		// when:
		wardIoOperation.deleteWard(foundWard);

		// then:
		assertThat(wardIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestWard(false);
		boolean result = wardIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void isCodePresent_shouldReturnFalse_whenCodeDoesNotExist() throws Exception {
		boolean result = wardIoOperation.isCodePresent("X");
		assertThat(result).isFalse();
	}

	@Test
	void isMaternityPresent_shouldReturnTrue_whenMaternityWardExists() throws Exception {
		boolean result = wardIoOperation.isMaternityPresent();

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardIoOperation.isMaternityPresent();
		}

		assertThat(result).isTrue();
	}

	@Test
	void isOpdPresent_shouldReturnTrue_whenOpdWardExists() throws Exception {
		boolean result = wardIoOperation.isOpdPresent();

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("OPD");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardIoOperation.isOpdPresent();
		}

		assertThat(result).isTrue();
	}

	@Test
	void findWard_shouldReturnWard_whenCodeExists() throws Exception {
		String code = setupTestWard(false);
		Ward result = wardIoOperation.findWard(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	void findWard_shouldThrow_whenCodeIsNull() throws Exception {
		assertThatThrownBy(() -> wardIoOperation.findWard(null))
			.isInstanceOf(OHServiceException.class);
	}

	private String setupTestWard(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		return ward.getCode();
	}

	private void checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();
		testWard.check(foundWard);
	}

}
