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
package org.isf.therapy.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.menu.model.User;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.sessionaudit.model.UserSession;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.service.SmsOperations;
import org.isf.therapy.TestTherapy;
import org.isf.therapy.model.Therapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TherapyManagerTest extends OHCoreTestCase {

	private static TestTherapy testTherapyRow;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestPatient testPatient;

	@Autowired
	TherapyIoOperations therapyIoOperation;
	@Autowired
	TherapyIoOperationRepository therapyIoOperationRepository;
	@Autowired
	TherapyManager therapyManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	SmsOperations smsOperations;

	@BeforeAll
	static void setUpClass() {
		UserSession.setUser(new User("TestUser", null, "testpass", "test"));
		testTherapyRow = new TestTherapy();
		testPatient = new TestPatient();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getTherapies_shouldReturnNull_whenRowsAreNull() throws Exception {
		assertThat(therapyManager.getTherapies(null)).isNull();
	}

	@Test
	void getTherapies_shouldCreateTherapyFromRow() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		List<TherapyRow> therapyRows = new ArrayList<>(1);
		therapyRows.add(foundTherapyRow);
		List<Therapy> therapies = therapyManager.getTherapies(therapyRows);
		assertThat(therapies).hasSize(1);
		assertThat(therapies.get(0).getNote()).isEqualTo("TestNote");
	}

	@Test
	void newTherapies_shouldReturnTrue_whenListIsEmpty() throws Exception {
		assertThat(therapyManager.newTherapies(new ArrayList<>())).isTrue();
	}

	@Test
	void newTherapies_shouldNotScheduleSms_whenDatesArePast() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		List<TherapyRow> therapyRows = new ArrayList<>(1);
		therapyRows.add(foundTherapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).isEmpty();
	}

	@Test
	void newTherapies_shouldScheduleSms_whenDateIsAfterToday() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyRow.setStartDate(TimeTools.getNow());
		therapyRow.setEndDate(TimeTools.getBeginningOfNextDay(therapyRow.getStartDate()));
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		List<TherapyRow> therapyRows = new ArrayList<>(1);
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).hasSize(1);
	}

	@Test
	void newTherapies_shouldTruncateSmsText_whenNoteIsTooLong() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		String longText = "This is a very long note that should cause the SMS code to truncate the entire message to 160 characters.";
		TherapyRow therapyRow = therapyManager.newTherapy(0, patient.getCode(), TimeTools.getNow(),
						TimeTools.getBeginningOfNextDay(TimeTools.getNow()), medical, 10.0, 1, 1, 1,
						longText + ' ' + longText, true, true);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		List<TherapyRow> therapyRows = new ArrayList<>(1);
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).hasSize(1);
		assertThat(smsOperations.getList().get(0).getSmsText()).hasSize(SmsManager.MAX_LENGTH);
	}

	@Test
	void newTherapies_shouldNotScheduleSms_whenSmsIsFalse() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyRow.setSms(false);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		List<TherapyRow> therapyRows = new ArrayList<>(1);
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).isEmpty();
	}

	@Test
	void getTherapyRows_shouldReturnAllRows_whenPatientCodeIsZero() throws Exception {
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		List<TherapyRow> therapyRows = therapyManager.getTherapyRows(0);
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	void newTherapy_shouldSaveTherapyRow() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		TherapyRow newTherapyRow = therapyManager.newTherapy(therapyRow);
		checkTherapyRowIntoDb(newTherapyRow.getTherapyID());
	}

	@Test
	void deleteAllTherapies_shouldDeletePatientTherapies() throws Exception {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		int id = setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		therapyManager.deleteAllTherapies(foundTherapyRow.getPatient().getCode());
		assertThat(therapyIoOperation.isCodePresent(id)).isFalse();
	}

	@Test
	void getMedicalsOutOfStock_shouldReturnMedical_whenStockIsInsufficient() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		LocalDateTime[] dates = { TimeTools.getNow(), TimeTools.getNow() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		List<Therapy> therapies = new ArrayList<>(1);
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).hasSize(1);
	}

	@Test
	void getMedicalsOutOfStock_shouldReturnEmpty_whenStockIsSufficient() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		medical.setInqty(10);
		medical.setOutqty(0);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		LocalDateTime[] dates = { TimeTools.getNow(), TimeTools.getNow() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 1.0, "", 1, "TestNote", true, true);

		List<Therapy> therapies = new ArrayList<>(1);
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).isEmpty();
	}

	@Test
	void getMedicalsOutOfStock_shouldReturnEmpty_whenDatesArePast() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		LocalDateTime yesterday = TimeTools.getDateToday0().minusDays(1);

		LocalDateTime[] dates = { yesterday, yesterday };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		List<Therapy> therapies = new ArrayList<>(1);
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).isEmpty();
	}

	private int setupTestTherapyRow(boolean usingSet) throws OHException {
		TherapyRow therapyRow;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, true);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		therapyRow = testTherapyRow.setup(patient, medical, usingSet);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		return therapyRow.getTherapyID();
	}

	private void checkTherapyRowIntoDb(int id) throws OHException {
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findById(id).orElse(null);
		assertThat(foundTherapyRow).isNotNull();
		testTherapyRow.check(foundTherapyRow);
	}

}
