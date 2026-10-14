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
package org.isf.therapy.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
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
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TherapyTest extends OHCoreTestCase {

	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		UserSession.setUser(new User("TestUser", null, "testpass", "test"));
		testPatient = new TestPatient();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void setters_shouldUpdateFields() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		Medical medical = testMedical.setup(medicalType, false);
		medicalsIoOperationRepository.saveAndFlush(medical);
		Patient patient = testPatient.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);

		LocalDateTime[] dates = { TimeTools.getNow(), TimeTools.getNow() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);

		assertThat(therapy.getTherapyID()).isEqualTo(1);
		therapy.setTherapyID(-99);
		assertThat(therapy.getTherapyID()).isEqualTo(-99);

		assertThat(therapy.getDates()).isNotNull();
		LocalDateTime yesterday = TimeTools.getDateToday0();
		yesterday = yesterday.minusDays(1);
		LocalDateTime[] newDates = { yesterday, yesterday };
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
		medical2.setCode(null);
		medical2.setLock(1);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType2);
		Medical savedMedical2 = medicalsIoOperationRepository.saveAndFlush(medical2);

		assertThat(therapy.getMedical()).isEqualTo(medical);
		therapy.setMedical(savedMedical2);
		assertThat(therapy.getMedical()).isNotEqualTo(medical);
	}

	@Test
	void toString_shouldUseTherapyFields() throws Exception {
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, false);
		Patient patient = testPatient.setup(false);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		patientIoOperationRepository.saveAndFlush(patient);

		LocalDateTime[] dates = { TimeTools.getNow(), TimeTools.getNow() };
		Therapy therapy = new Therapy(1, patient.getCode(), dates, medical, 10.0, "", 1, "TestNote", true, true);
		assertThat(therapy).hasToString("10.0 of TestDescription - 1 per day");
	}

}
