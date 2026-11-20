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
package org.isf.operation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.TestAdmission;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.dlvrrestype.TestDeliveryResultType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrtype.TestDeliveryType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.opd.TestOpd;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.operation.TestOperation;
import org.isf.operation.TestOperationRow;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHServiceException;
import org.isf.visits.TestVisit;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OperationRowIoOperationsTest extends OHCoreTestCase {

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
	private static TestOpd testOpd;
	private static TestVisit testVisit;

	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationRowIoOperations operationRowIoOperations;
	@Autowired
	OperationRowIoOperationRepository operationRowIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;
	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;

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
		testOpd = new TestOpd();
		testVisit = new TestVisit();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getOperationRow_shouldReturnSavedRow() throws Exception {
		setupTestOperationRow(false);
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRow();
		assertThat(operationRows).hasSize(1);
	}

	@Test
	void countAllActiveOperations_shouldCountSavedOperationRow() throws Exception {
		setupTestOperationRow(false);
		assertThat(operationRowIoOperations.countAllActiveOperations()).isEqualTo(1);
	}

	@Test
	void getOperationRowByPatient_shouldReturnRowsOfPatient() throws Exception {
		int id = setupTestOperationRowWithAdmission(true);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		Patient patient = operationRow.getAdmission().getPatient();
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRowByPatient(patient);
		assertThat(operationRows).isNotEmpty();
		assertThat(operationRows.get(0).getAdmission().getPatient()).isEqualTo(patient);
	}

	@Test
	void getOperationRowByPatient_shouldReturnEmpty_whenPatientHasNoRows() throws Exception {
		int id = setupTestOperationRowWithAdmission(true);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		Patient patient = testPatient.setup(false);
		patient.setSex('M');
		patient.setFirstName("firstName");
		patient.setSecondName("secondName");
		patientIoOperationRepository.saveAndFlush(patient);
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRowByPatient(patient);
		assertThat(operationRows).isEmpty();
	}

	@Test
	void getOperationRowByAdmission_shouldReturnRowsOfAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

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
		operationRow.setAdmission(admission);

		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut2);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut3);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregTreatmentType);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResult);
		admissionIoOperationRepository.saveAndFlush(admission);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		operationRowIoOperationRepository.saveAndFlush(operationRow);

		List<OperationRow> operationRows = operationRowIoOperations.getOperationRowByAdmission(admission);
		assertThat(operationRows).hasSize(1);
	}

	@Test
	void getOperationRowByAdmission_shouldReturnEmpty_whenRowHasNoAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

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
		// Don't set admission; leave it as default value of null;  operationRow.setAdmission(admission);

		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut2);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut3);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregTreatmentType);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResult);
		admissionIoOperationRepository.saveAndFlush(admission);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		operationRowIoOperationRepository.saveAndFlush(operationRow);

		List<OperationRow> operationRows = operationRowIoOperations.getOperationRowByAdmission(admission);
		assertThat(operationRows).isEmpty();
	}

	@Test
	void getOperationRowByOpd_shouldReturnEmpty_whenOpdIsNotPersisted() throws Exception {
		setupTestOperationRow(true);
		Opd opd = testOpd.setup(new Patient(), new Disease(), new Ward(), new Visit(), false);
		assertThat(operationRowIoOperations.getOperationRowByOpd(opd)).isEmpty();
	}

	@Test
	void getOperationRowByOpd_shouldReturnRowsOfOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Ward ward = testWard.setup(false);
		Visit nextVisit = testVisit.setup(patient, true, ward);
		Opd opd = testOpd.setup(patient, disease, ward, nextVisit, false);
		operationRow.setOpd(opd);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		wardIoOperationRepository.saveAndFlush(ward);
		visitsIoOperationRepository.saveAndFlush(nextVisit);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		opdIoOperationRepository.saveAndFlush(opd);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		operationRowIoOperationRepository.saveAndFlush(operationRow);

		assertThat(operationRowIoOperations.getOperationRowByOpd(opd)).isNotEmpty();
	}

	@Test
	void deleteOperationRow_shouldThrow_whenOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		assertThatThrownBy(() -> operationRowIoOperations.deleteOperationRow(operationRow))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void deleteOperationRow_shouldRemoveOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		operationRowIoOperations.deleteOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(id)).isNull();
	}

	@Test
	void updateOperationRow_shouldNotSaveRow_whenOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		// The method is void so there is no way to tell if anything happened
		operationRowIoOperations.updateOperationRow(operationRow);
		// To prove that it doesn't exist
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNull();
	}

	@Test
	void updateOperationRow_shouldSaveNewRemarks() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isNotEqualTo("someNewRemarks");
		operationRow.setRemarks("someNewRemarks");
		operationRowIoOperations.updateOperationRow(operationRow);
		operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isEqualTo("someNewRemarks");
	}

	@Test
	void newOperationRow_shouldSaveOperationRow() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);

		operationRowIoOperations.newOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNotNull();
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

	private int setupTestOperationRowWithAdmission(boolean usingSet) throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

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
		operationRow.setAdmission(admission);

		wardIoOperationRepository.saveAndFlush(ward);
		patientIoOperationRepository.saveAndFlush(patient);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut2);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut3);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregTreatmentType);
		deliveryTypeIoOperationRepository.saveAndFlush(deliveryType);
		deliveryResultIoOperationRepository.saveAndFlush(deliveryResult);
		admissionIoOperationRepository.saveAndFlush(admission);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		operationRowIoOperationRepository.saveAndFlush(operationRow);
		return operationRow.getId();
	}

}
