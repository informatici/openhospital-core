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
package org.isf.therapy.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.sms.manager.SmsManager;
import org.isf.sms.service.SmsOperations;
import org.isf.therapy.manager.TherapyManager;
import org.isf.therapy.model.Therapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.therapy.service.TherapyIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.time.TimeTools;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class Tests extends OHCoreTestCase {

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
	ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	SmsOperations smsOperations;

	@BeforeClass
	public static void setUpClass() {
		testTherapyRow = new TestTherapy();
		testPatient = new TestPatient();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testTherapyRowGets() throws Exception {
		int id = _setupTestTherapyRow(false);
		_checkTherapyRowIntoDb(id);
	}

	@Test
	public void testTherapyRowSets() throws Exception {
		int id = _setupTestTherapyRow(true);
		_checkTherapyRowIntoDb(id);
	}

	@Test
	public void testIoGetTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testIoGetTherapyRowWithZeroAsIdentifierProvided() throws Exception {
		// given:
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);

		// when:
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(0);

		// then:
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testIoNewTherapyRow() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyIoOperation.newTherapy(therapyRow);
		_checkTherapyRowIntoDb(therapyRow.getTherapyID());
	}

	@Test
	public void testIoDeleteTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		boolean result = therapyIoOperation.deleteAllTherapies(foundTherapyRow.getPatient());
		assertThat(result).isTrue();
		result = therapyIoOperation.isCodePresent(id);
		assertThat(result).isFalse();
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestTherapyRow(false);
		TherapyRow found = therapyIoOperationRepository.findOne(id);
		Patient obsoletePatient = found.getPatient();
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(obsoletePatient, mergedPatient));

		// then:
		TherapyRow result = therapyIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	public void testMgrGetTherapiesNull() throws Exception {
		assertThat(therapyManager.getTherapies(null)).isNull();
	}

	@Test
	public void testMgrGetTherapies() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow therapyRow = therapyIoOperationRepository.findOne(id);
		List<TherapyRow> therapyRows = new ArrayList<>();
		therapyRows.add(therapyRow);
		List<Therapy> therapies = therapyManager.getTherapies(therapyRows);
		assertThat(therapies).hasSize(1);
		assertThat(therapies.get(0).getNote()).isEqualTo("TestNote");
	}

	@Test
	public void testMgrNewTherapiesEmpty() throws Exception {
		assertThat(therapyManager.newTherapies(new ArrayList<>())).isTrue();
	}

	@Test
	public void testMgrNewTherapiesWithSMSDateBeforeToday() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow therapyRow = therapyIoOperationRepository.findOne(id);
		ArrayList<TherapyRow> therapyRows = new ArrayList<>();
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).isEmpty();
	}

	@Test
	public void testMgrNewTherapiesWithSMSDateAfterToday() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyRow.setStartDate(new GregorianCalendar());
		therapyRow.setEndDate(TimeTools.getBeginningOfNextDay(therapyRow.getStartDate()));
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		ArrayList<TherapyRow> therapyRows = new ArrayList<>();
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).hasSize(1);
	}

	@Test
	public void testMgrNewTherapiesWithSMSDateAfterTodayTruncateMessage() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		String longText = "This is a very long note that should cause the SMS code to truncate the entire message to 160 characters.";
		TherapyRow therapyRow = therapyManager.newTherapy(1, patient.getCode(), new GregorianCalendar(),
				TimeTools.getBeginningOfNextDay(new GregorianCalendar()), medical, 10.0, 1, 1, 1,
				longText + ' ' + longText, true, true);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		ArrayList<TherapyRow> therapyRows = new ArrayList<>();
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).hasSize(1);
		assertThat(smsOperations.getList().get(0).getSmsText()).hasSize(SmsManager.MAX_LENGHT);
	}

	@Test
	public void testMgrNewTherapiesWithNoSMS() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyRow.setSms(false);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		ArrayList<TherapyRow> therapyRows = new ArrayList<>();
		therapyRows.add(therapyRow);
		assertThat(therapyManager.newTherapies(therapyRows)).isTrue();
		assertThat(smsOperations.getList()).isEmpty();
	}

	@Test
	public void testMgrGetTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		List<TherapyRow> therapyRows = therapyIoOperation.getTherapyRows(foundTherapyRow.getPatient().getCode());
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testMgrGetTherapyRowWithZeroAsIdentifierProvided() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		List<TherapyRow> therapyRows = therapyManager.getTherapyRows(0);
		assertThat(therapyRows.get(therapyRows.size() - 1).getNote()).isEqualTo(foundTherapyRow.getNote());
	}

	@Test
	public void testMgrNewTherapyRow() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		TherapyRow therapyRow = testTherapyRow.setup(patient, medical, true);
		therapyManager.newTherapy(therapyRow);
		_checkTherapyRowIntoDb(therapyRow.getTherapyID());
	}

	@Test
	public void testMgrDeleteTherapyRow() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		assertThat(therapyManager.deleteAllTherapies(foundTherapyRow.getPatient().getCode())).isTrue();
		assertThat(therapyIoOperation.isCodePresent(id)).isFalse();
	}

	@Test
	public void testMgrGetMedicalsOutOfStockDayCountGreaterThanZero() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		GregorianCalendar[] dates = new GregorianCalendar[] { new GregorianCalendar(), new GregorianCalendar() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		ArrayList<Therapy> therapies = new ArrayList<>();
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).hasSize(1);
	}

	@Test
	public void testMgrGetMedicalsOutOfStockDayActualGreaterThanNeed() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		medical.setInitialqty(10);
		medical.setInqty(10);
		medical.setOutqty(0);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		GregorianCalendar[] dates = new GregorianCalendar[] { new GregorianCalendar(), new GregorianCalendar() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 1.0, "", 1, "TestNote", true, true);

		ArrayList<Therapy> therapies = new ArrayList<>();
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).isEmpty();
	}

	@Test
	public void testMgrGetMedicalsOutOfStockDayCountEqualToZero() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		GregorianCalendar yesterday = TimeTools.getDateToday0();
		yesterday.set(Calendar.DATE, -1);

		GregorianCalendar[] dates = new GregorianCalendar[] { yesterday, yesterday };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		ArrayList<Therapy> therapies = new ArrayList<>();
		therapies.add(therapy);
		List<Medical> medicals = therapyManager.getMedicalsOutOfStock(therapies);
		assertThat(medicals).isEmpty();
	}

	@Test
	public void testTherapyRowToString() throws Exception {
		int id = _setupTestTherapyRow(false);
		TherapyRow therapyRow = therapyIoOperationRepository.findOne(id);
		assertThat(therapyRow).hasToString("2 - 10 9.9/11/12");
	}

	@Test
	public void testTherapyRowEquals() throws Exception {
		int id = _setupTestTherapyRow(true);
		TherapyRow therapyRow = therapyIoOperationRepository.findOne(id);

		assertThat(therapyRow.equals(therapyRow)).isTrue();
		assertThat(therapyRow)
				.isNotNull()
				.isNotEqualTo("someString");

		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		TherapyRow therapyRow2 = testTherapyRow.setup(patient, medical, true);

		assertThat(therapyRow).isNotEqualTo(therapyRow2);
		therapyRow2.setTherapyID(therapyRow.getTherapyID());
		assertThat(therapyRow).isEqualTo(therapyRow2);
	}

	@Test
	public void testTherapyRowHashCode() throws Exception {
		int id = _setupTestTherapyRow(true);
		TherapyRow therapyRow = therapyIoOperationRepository.findOne(id);
		therapyRow.setTherapyID(99);
		// compute
		int hashCode = therapyRow.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 99);
		// use computed value
		assertThat(therapyRow.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testTherapySettersGetters() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		GregorianCalendar[] dates = new GregorianCalendar[] { new GregorianCalendar(), new GregorianCalendar() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		assertThat(therapy.getTherapyID()).isEqualTo(1);
		therapy.setTherapyID(-99);
		assertThat(therapy.getTherapyID()).isEqualTo(-99);

		assertThat(therapy.getDates()).isNotNull();
		GregorianCalendar yesterday = TimeTools.getDateToday0();
		yesterday.set(Calendar.DATE, -1);
		GregorianCalendar[] newDates = new GregorianCalendar[] { yesterday, yesterday };
		therapy.setDates(newDates);
		assertThat(therapy.getDates()).isEqualTo(newDates);

		assertThat(therapy.getQty()).isEqualTo(10.0);
		therapy.setQty(-99.0);
		assertThat(therapy.getQty()).isEqualTo(-99.0);

		assertThat(therapy.getFreqInDay()).isEqualTo(1);
		therapy.setFreqInDay(-98);
		assertThat(therapy.getFreqInDay()).isEqualTo(-98);

		assertThat(therapy.getPatID()).isEqualTo(patient.getCode());
		therapy.setPatID(-97);
		assertThat(therapy.getPatID()).isEqualTo(-97);

		assertThat(therapy.getUnits()).isEmpty();
		therapy.setUnits("-96");
		assertThat(therapy.getUnits()).isEqualTo("-96");

		assertThat(therapy.isNotify()).isTrue();
		therapy.setNotify(false);
		assertThat(therapy.isNotify()).isFalse();

		assertThat(therapy.getNote()).isEqualTo("TestNote");
		therapy.setNote("someNewNote");
		assertThat(therapy.getNote()).isEqualTo("someNewNote");

		assertThat(therapy.isSms()).isTrue();
		therapy.setSms(false);
		assertThat(therapy.isSms()).isFalse();

		MedicalType medicalType2 = testMedicalType.setup(false);
		medicalType2.setCode("someOtherCode");
		Medical medical2 = testMedical.setup(medicalType2, false);
		medical2.setCode(-99);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType2);
		medicalsIoOperationRepository.saveAndFlush(medical2);

		assertThat(therapy.getMedical()).isEqualTo(medical);
		therapy.setMedical(medical2);
		assertThat(therapy.getMedical()).isNotEqualTo(medical);
	}

	@Test
	public void testTherapyToString() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		GregorianCalendar[] dates = new GregorianCalendar[] { new GregorianCalendar(), new GregorianCalendar() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);
		assertThat(therapy).hasToString("10.0 of TestDescription - 1 per day");
	}

	private Patient _setupTestPatient(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int _setupTestTherapyRow(boolean usingSet) throws OHException {
		TherapyRow therapyRow;
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);
		therapyRow = testTherapyRow.setup(patient, medical, usingSet);
		therapyIoOperationRepository.saveAndFlush(therapyRow);
		return therapyRow.getTherapyID();
	}

	private void _checkTherapyRowIntoDb(int id) throws OHException {
		TherapyRow foundTherapyRow = therapyIoOperationRepository.findOne(id);
		testTherapyRow.check(foundTherapyRow);
	}
}