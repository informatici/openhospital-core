/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.anamnesis;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.anamnesis.manager.PatientHistoryManager;
import org.isf.anamnesis.model.PatientHistory;
import org.isf.anamnesis.model.PatientPatientHistory;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

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
	void testPatientHistorySave() throws Exception {
		PatientHistory patientHistory = testPatientHistory.setup();
		PatientHistory savedPatientHistory = patientHistoryManager.saveOrUpdate(patientHistory);
		assertThat(savedPatientHistory).isNotNull();
	}

	@Test
	void testPatientHistoryGetByPatientId() throws Exception {
		PatientHistory patientHistory = testPatientHistory.setup();
		PatientHistory savedPatientHistory = patientHistoryManager.saveOrUpdate(patientHistory);
		int patientID = savedPatientHistory.getId();
		PatientHistory byIDPatientHistory = patientHistoryManager.getByPatientId(patientID);
		assertThat(byIDPatientHistory).isNotNull();
		assertThat(savedPatientHistory.compareTo(byIDPatientHistory)).isZero();

		assertThat(patientHistoryManager.getByPatientId(patientID + 99)).isNull();
	}

	@Test
	void testPatientHistoryGets() throws Exception {
		PatientHistory patientHistory = testPatientHistory.setup();

		assertThat(patientHistory.getId()).isZero();
		assertThat(patientHistory.getPatientId()).isOne();
		assertThat(patientHistory.isFamilyNothing()).isTrue();
		assertThat(patientHistory.isFamilyHypertension()).isFalse();
		assertThat(patientHistory.isFamilyDrugAddiction()).isTrue();
		assertThat(patientHistory.isFamilyCardiovascular()).isFalse();
		assertThat(patientHistory.isFamilyInfective()).isTrue();
		assertThat(patientHistory.isFamilyEndocrinometabol()).isFalse();
		assertThat(patientHistory.isFamilyRespiratory()).isTrue();
		assertThat(patientHistory.isFamilyCancer()).isFalse();
		assertThat(patientHistory.isFamilyOrto()).isTrue();
		assertThat(patientHistory.isFamilyGyno()).isFalse();
		assertThat(patientHistory.isFamilyOrto()).isTrue();
		assertThat(patientHistory.isFamilyOther()).isFalse();
		assertThat(patientHistory.getFamilyNote()).isEqualTo("Family Note");
		assertThat(patientHistory.isPatClosedNothing()).isFalse();
		assertThat(patientHistory.isPatClosedHypertension()).isTrue();
		assertThat(patientHistory.isPatClosedDrugaddiction()).isFalse();
		assertThat(patientHistory.isPatClosedCardiovascular()).isFalse();
		assertThat(patientHistory.isPatClosedInfective()).isTrue();
		assertThat(patientHistory.isPatClosedEndocrinometabol()).isFalse();
		assertThat(patientHistory.isPatClosedRespiratory()).isTrue();
		assertThat(patientHistory.isPatClosedCancer()).isFalse();
		assertThat(patientHistory.isPatClosedGyno()).isTrue();
		assertThat(patientHistory.isPatClosedOrto()).isTrue();
		assertThat(patientHistory.isPatClosedOther()).isFalse();
		assertThat(patientHistory.getPatClosedNote()).isEqualTo("Closed Note");
		assertThat(patientHistory.isPatOpenNothing()).isTrue();
		assertThat(patientHistory.isPatOpenHypertension()).isFalse();
		assertThat(patientHistory.isPatOpenDrugaddiction()).isTrue();
		assertThat(patientHistory.isPatOpenCardiovascular()).isFalse();
		assertThat(patientHistory.isPatOpenInfective()).isTrue();
		assertThat(patientHistory.isPatOpenEndocrinometabol()).isFalse();
		assertThat(patientHistory.isPatOpenRespiratory()).isTrue();
		assertThat(patientHistory.isPatOpenGyno()).isFalse();
		assertThat(patientHistory.isPatOpenOther()).isTrue();
		assertThat(patientHistory.isPatOpenCancer()).isFalse();
		assertThat(patientHistory.isPatOpenOrto()).isTrue();
		assertThat(patientHistory.getPatOpenNote()).isEqualTo("Open Note");
		assertThat(patientHistory.getPatNote()).isEqualTo("Patient Note");
		assertThat(patientHistory.getPatSurgery()).isEqualTo("Surgery");
		assertThat(patientHistory.getPatAllergy()).isEqualTo("Allergy");
		assertThat(patientHistory.getPatTherapy()).isEqualTo("Therapy");
		assertThat(patientHistory.getPatMedicine()).isEqualTo("Medicine");
		assertThat(patientHistory.getPatNote()).isEqualTo("Patient Note");
		assertThat(patientHistory.isPhyNutritionNormal()).isTrue();
		assertThat(patientHistory.getPhyNutritionAbnormal()).isEqualTo("Nutrition Abnormal");
		assertThat(patientHistory.isPhyBowelNormal()).isFalse();
		assertThat(patientHistory.getPhyBowelAbnormal()).isEqualTo("Bowel Abnormal");
		assertThat(patientHistory.isPhyDiuresisNormal()).isTrue();
		assertThat(patientHistory.getPhyDiuresisAbnormal()).isEqualTo("Diuresis Abnormal");
		assertThat(patientHistory.isPhyAlcohol()).isFalse();
		assertThat(patientHistory.isPhySmoke()).isTrue();
		assertThat(patientHistory.isPhyDrug()).isFalse();
		assertThat(patientHistory.isPhyPeriodNormal()).isTrue();
		assertThat(patientHistory.getPhyPeriodAbnormal()).isEqualTo("Period Abnormal");
		assertThat(patientHistory.isPhyMenopause()).isFalse();
		assertThat(patientHistory.getPhyMenopauseYears()).isZero();
		assertThat(patientHistory.isPhyHrtNormal()).isTrue();
		assertThat(patientHistory.getPhyHrtAbnormal()).isEqualTo("Hrt Abnormal");
		assertThat(patientHistory.isPhyPregnancy()).isFalse();
		assertThat(patientHistory.getPhyPregnancyNumber()).isZero();
		assertThat(patientHistory.getPhyPregnancyBirth()).isZero();
		assertThat(patientHistory.getPhyPregnancyAbort()).isZero();
	}

	@Test
	void testPatientPatientHistory() throws Exception {
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
