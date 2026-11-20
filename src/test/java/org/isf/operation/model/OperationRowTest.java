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
package org.isf.operation.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.isf.OHCoreTestCase;
import org.isf.admission.TestAdmission;
import org.isf.admission.model.Admission;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.model.AdmissionType;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.model.DischargeType;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.dlvrrestype.TestDeliveryResultType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrtype.TestDeliveryType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.opd.model.Opd;
import org.isf.operation.TestOperation;
import org.isf.operation.TestOperationRow;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationRowIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationRowTest extends OHCoreTestCase {

	private static TestOperation testOperation;
	private static TestOperationType testOperationType;
	private static TestOperationRow testOperationRow;
	private static TestAdmission testAdmission;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestAdmissionType testAdmissionType;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestDischargeType testDischargeType;
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryResultType testDeliveryResultType;

	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationRowIoOperationRepository operationRowIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testOperation = new TestOperation();
		testOperationType = new TestOperationType();
		testOperationRow = new TestOperationRow();
		testAdmission = new TestAdmission();
		testWard = new TestWard();
		testPatient = new TestPatient();
		testAdmissionType = new TestAdmissionType();
		testDiseaseType = new TestDiseaseType();
		testDisease = new TestDisease();
		testDischargeType = new TestDischargeType();
		testPregnantTreatmentType = new TestPregnantTreatmentType();
		testDeliveryType = new TestDeliveryType();
		testDeliveryResultType = new TestDeliveryResultType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void setters_shouldSetFields_whenOperationIsCreatedWithConstructor() throws Exception {
		int id = setupTestOperationRow(false);
		checkOperationRowIntoDb(id);
	}

	@Test
	void setters_shouldSetFields_whenOperationIsCreatedWithSetters() throws Exception {
		int id = setupTestOperationRow(true);
		checkOperationRowIntoDb(id);
	}

	@Test
	void constructors_shouldCreateEqualRows_whenGivenSameFields() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		Ward ward = testWard.setup(false, false);
		Patient patient = testPatient.setup(false);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);

		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
			diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
			deliveryType, deliveryResult, false);

		OperationRow operationRow1 = new OperationRow(operation, "prescriber", "opResult", LocalDateTime.of(2021, 1, 1, 0, 0, 0), "remarks", admission,
			new Opd(),
			null, 10.0f);

		OperationRow operationRow2 = new OperationRow(1, operation, "prescriber", "opResult", LocalDateTime.of(2021, 1, 1, 0, 0, 0), "remarks", admission,
			new Opd(), null, 10.0f);

		operationRow1.setId(1);
		assertThat(operationRow1).isEqualTo(operationRow2);
	}

	@Test
	void equals_shouldReturnTrueForSameInstanceAndFalseForOtherType() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, false);

		assertThat(operationRow)
			.isEqualTo(operationRow)
			.isNotNull()
			.isNotEqualTo("some string");
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		Operation operation = testOperation.setup(operationType, false);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		int hashCode = operationRow.hashCode();
		// use computed value
		assertThat(operationRow.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void toString_shouldReturnOperationDescriptionAndAdmissionUserId() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		Operation operation = testOperation.setup(operationType, false);

		operation.setDescription("aDescription");
		OperationRow operationRow = testOperationRow.setup(operation, true);

		Patient patient = testPatient.setup(false);
		Admission admission = testAdmission.setup(null, patient, null, null, null,
			null, null, operation, null, null,
			null, null, false);
		admission.setUserID("UserID");
		operationRow.setAdmission(admission);

		assertThat(operationRow).hasToString("aDescription UserID");
	}

	private int setupTestOperationRow(boolean usingSet) throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, usingSet);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		operationRowIoOperationRepository.saveAndFlush(operationRow);
		return operationRow.getId();
	}

	private void checkOperationRowIntoDb(int id) throws Exception {
		OperationRow foundOperationRow = operationRowIoOperationRepository.findById(id);
		testOperationRow.check(foundOperationRow);
	}

}
