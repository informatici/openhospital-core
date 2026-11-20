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
package org.isf.ward.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WardBrowserManagerTest extends OHCoreTestCase {

	private static TestWard testWard;

	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	WardBrowserManager wardBrowserManager;
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
		assertThat(wardBrowserManager.getCurrentOccupation(ward)).isZero();
	}

	@Test
	void getCurrentOccupation_shouldCountAdmissionsInWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		Patient patient = new Patient();
		patient.setBirthDate(LocalDate.now().minusYears(45));
		patientIoOperationRepository.save(patient);
		AdmissionType admissionType = new AdmissionType("ZZ", "TestDescription");
		Admission admission1 = new Admission(0, 1, "N", ward, 1, patient, LocalDateTime.now(), admissionType, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		Admission admission2 = new Admission(0, 1, "N", ward, 1, patient, LocalDateTime.now(), admissionType, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, 'N');
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThat(wardBrowserManager.getCurrentOccupation(ward)).isEqualTo(2);
	}

	@Test
	void getWardsNoMaternity_shouldReturnNonMaternityWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		List<Ward> wards = wardBrowserManager.getWardsNoMaternity();
		assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	void getIpdWards_shouldReturnEmpty_whenWardHasNoBeds() throws Exception {
		String code = setupTestWard(false);
		Ward opdWard = wardBrowserManager.findWard(code);
		opdWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(opdWard);

		List<Ward> wards = wardBrowserManager.getIpdWards();
		assertThat(wards).isEmpty();
	}

	@Test
	void getOpdWards_shouldReturnOpdWard_whenWardHasNoBeds() throws Exception {
		String code = setupTestWard(false);
		Ward opdWard = wardBrowserManager.findWard(code);
		opdWard.setBeds(0);
		wardIoOperationRepository.saveAndFlush(opdWard);

		List<Ward> wards = wardBrowserManager.getOpdWards();
		assertThat(wards).hasSize(1);
	}

	@Test
	void getWards_shouldReturnSavedWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		List<Ward> wards = wardBrowserManager.getWards();
		assertThat(wards).hasSize(1);
		assertThat(wards.get(0).getDescription()).isEqualTo(ward.getDescription());
	}

	@Test
	void newWard_shouldSaveWard() throws Exception {
		Ward ward = testWard.setup(true);
		Ward newWard = wardBrowserManager.newWard(ward);
		assertThat(newWard.getDescription()).isEqualTo("TestDescription");
		checkWardIntoDb(newWard.getCode());
	}

	@Test
	void updateWard_shouldSaveNewDescription() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		ward.setDescription("Update");
		Ward updatedWard = wardBrowserManager.updateWard(ward);
		assertThat(updatedWard.getDescription()).isEqualTo("Update");
	}

	@Test
	void updateWard_shouldReturnWard_whenCodeIsNotPresent() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("X");
		Ward updateWard = wardBrowserManager.updateWard(ward);
		assertThat(updateWard.getCode()).isEqualTo("X");
	}

	@Test
	void deleteWard_shouldRemoveWard() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		wardBrowserManager.deleteWard(ward);
		assertThat(wardBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void deleteWard_shouldThrow_whenWardIsMaternity() throws Exception {
		wardBrowserManager.maternityControl(true);
		List<Ward> wards = wardBrowserManager.getWards();
		assertThatThrownBy(() -> wardBrowserManager.deleteWard(wards.get(0)))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void deleteWard_shouldThrow_whenWardHasPatients() throws Exception {
		String code = setupTestWard(false);
		Ward ward = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(ward).isNotNull();
		Patient patient = new Patient();
		patient.setBirthDate(LocalDate.now().minusYears(45));
		patientIoOperationRepository.save(patient);
		AdmissionType admissionType = new AdmissionType("ZZ", "TestDescription");
		Admission admission1 = new Admission(0, 1, "N", ward, 1, patient, LocalDateTime.now(), admissionType, null, null,
			null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, 'N');
		Admission admission2 = new Admission(0, 1, "N", ward, 1, patient, LocalDateTime.now(), admissionType, null, null,
			null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, 'N');
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		admissionIoOperationRepository.saveAndFlush(admission1);
		admissionIoOperationRepository.saveAndFlush(admission2);
		assertThatThrownBy(() -> wardBrowserManager.deleteWard(ward))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestWard(false);
		assertThat(wardBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void isCodePresent_shouldReturnFalse_whenCodeDoesNotExist() throws Exception {
		assertThat(wardBrowserManager.isCodePresent("X")).isFalse();
	}

	@Test
	void maternityControl_shouldReturnTrue_whenMaternityWardExists() throws Exception {
		boolean result = wardBrowserManager.maternityControl(false);

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardBrowserManager.maternityControl(false);
		}

		assertThat(result).isTrue();
	}

	@Test
	void maternityControl_shouldReturnTrue_whenCreateIfNotExistsIsTrue() throws Exception {
		assertThat(wardBrowserManager.maternityControl(true)).isTrue();
	}

	@Test
	void opdControl_shouldReturnTrue_whenOpdWardExists() throws Exception {
		boolean result = wardBrowserManager.opdControl(false);

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("OPD");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardBrowserManager.opdControl(false);
		}

		assertThat(result).isTrue();
	}

	@Test
	void opdControl_shouldReturnTrue_whenCreateIfNotExistsIsTrue() throws Exception {
		assertThat(wardBrowserManager.opdControl(true)).isTrue();
	}

	@Test
	void findWard_shouldReturnWard_whenCodeExists() throws Exception {
		String code = setupTestWard(false);
		Ward result = wardBrowserManager.findWard(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	void findWard_shouldThrow_whenCodeIsNull() throws Exception {
		assertThatThrownBy(() -> wardBrowserManager.findWard(null))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void newWard_shouldThrow_whenCodeIsEmpty() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenCodeIsTooLong() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("thisIsACodeThatShouldBeTooLong");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenDescriptionIsEmpty() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setDescription("");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenNumberOfBedsIsNegative() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setBeds(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenNumberOfNursesIsNegative() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setNurs(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenNumberOfDoctorsIsNegative() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setDocs(-99);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrow_whenEmailIsNotValid() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setEmail("badFormatForEmailAddress");
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
	}

	@Test
	void newWard_shouldThrowDataIntegrityViolation_whenCodeExists() throws Exception {
		Ward ward = testWard.setup(true);
		wardBrowserManager.newWard(ward);
		assertThatThrownBy(() -> wardBrowserManager.newWard(ward))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error"));
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
