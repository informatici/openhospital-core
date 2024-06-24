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
package org.isf.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.TestAdmission;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperations;
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
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationIoOperations;
import org.isf.operation.service.OperationRowIoOperationRepository;
import org.isf.operation.service.OperationRowIoOperations;
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
import org.isf.utils.pagination.PagedResponse;
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

class Tests extends OHCoreTestCase {

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
	OperationIoOperations operationIoOperations;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	OperationBrowserManager operationBrowserManager;
	@Autowired
	OperationRowIoOperations operationRowIoOperations;
	@Autowired
	OperationRowIoOperationRepository operationRowIoOperationRepository;
	@Autowired
	OperationRowBrowserManager operationRowBrowserManager;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	AdmissionIoOperations admissionIoOperation;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	AdmissionBrowserManager admissionBrowserManager;
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
	void testOperationGets() throws Exception {
		String code = setupTestOperation(false);
		checkOperationIntoDb(code);
	}

	@Test
	void testOperationSets() throws Exception {
		String code = setupTestOperation(true);
		checkOperationIntoDb(code);
	}

	@Test
	void testOperationRowGets() throws Exception {
		int id = setupTestOperationRow(false);
		checkOperationRowIntoDb(id);
	}

	@Test
	void testOperationRowSets() throws Exception {
		int id = setupTestOperationRow(true);
		checkOperationRowIntoDb(id);
	}

	@Test
	void testIoGetOperationByTypeDescription() throws Exception {
		// given:
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);

		// when:
		List<Operation> operations = operationIoOperations.getOperationByTypeDescription(foundOperation.getType().getDescription());

		// then:
		assertThat(operations).isNotEmpty();
		assertThat(operations.get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void testIoGetOperationByTypeDescriptionNull() throws Exception {
		// given:
		String code = setupTestOperation(true);

		// when:
		List<Operation> operations = operationIoOperations.getOperationByTypeDescription(null);

		// then:
		assertThat(operations).isNotEmpty();
	}

	@Test
	void testIoGetOperationOpdOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	void testIoGetOperationOpdOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	void testIoGetOperationOpdNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isEmpty();
	}

	@Test
	void testIoGetOperationAdmOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	void testIoGetOperationAdmAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	void testIoGetOperationAdmNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isEmpty();
	}

	@Test
	void testIoNewOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationIoOperations.newOperation(operation)).isNotNull();
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	void testIoUpdateOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationIoOperations.updateOperation(foundOperation)).isNotNull();
		Operation updateOperation = operationIoOperations.findByCode(code);
		assertThat(updateOperation).isNotNull();
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	void testIoDeleteOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		operationIoOperations.deleteOperation(foundOperation);
		assertThat(operationIoOperations.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationIoOperations.isCodePresent(code)).isTrue();
	}

	@Test
	void testIoIsDescriptionPresent() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	void testIoIsDescriptionPresentNotFound() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent("someOtherDescription", foundOperation.getType().getCode())).isFalse();
	}

	@Test
	void testMgrGetOperationByTypeDescription() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(foundOperation.getType().getDescription());
		assertThat(operations).isNotEmpty();
		assertThat(operations.get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void testMgrGetOperationByTypeDescriptionNull() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(null);
		assertThat(operations).isNotEmpty();
	}

	@Test
	void testMgrGetOperation() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperation();
		assertThat(operations).isNotEmpty();
	}

	@Test
	void testGetOperationPageable() throws Exception {
		// given:
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);

		// when:
		PagedResponse<Operation> operations = operationBrowserManager.getOperationPageable(0, 10);

		// then:
		assertThat(operations.getData().get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	void testMgrGetOperationOpdOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	void testMgrGetOperationOpdOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	void testMgrGetOperationOpdNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isEmpty();
	}

	@Test
	void testMgrGetOperationAdmOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	void testMgrGetOperationAdmAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	void testMgrGetOperationAdmNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isEmpty();
	}

	@Test
	void testMgrNewOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationBrowserManager.newOperation(operation)).isNotNull();
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	void testMgrUpdateOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationBrowserManager.updateOperation(foundOperation)).isNotNull();
		Operation updateOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(updateOperation).isNotNull();
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	void testMgrDeleteOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		operationBrowserManager.deleteOperation(foundOperation);
		assertThat(operationBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrDescriptionControl() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	void testMgrDescriptionControlNotFound() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl("someOtherDescription", foundOperation.getType().getCode())).isFalse();
	}

	@Test
	void testMgrGetResultsList() throws Exception {
		assertThat(operationBrowserManager.getResultsList()).hasSize(3);
	}

	@Test
	void testMgrGetResultDescriptionKey() throws Exception {
		assertThat(operationBrowserManager.getResultDescriptionKey("angal.operation.result.failure.txt")).isEqualTo("failure");

		assertThat(operationBrowserManager.getResultDescriptionKey("the_description_is_not_there")).isEqualTo("");
	}

	@Test
	void testMgrGetResultDescriptionList() throws Exception {
		List<String> descriptionList = operationBrowserManager.getResultDescriptionList();
		assertThat(descriptionList).isNotEmpty();
	}

	@Test
	void testMgrGetResultDescriptionTranslated() throws Exception {
		assertThat(operationBrowserManager.getResultDescriptionTranslated("failure")).isEqualTo("angal.operation.result.failure.txt");

		assertThat(operationBrowserManager.getResultDescriptionTranslated("the_key_is_not_there")).isNull();
	}

	@Test
	void testRowIoGetRowOperation() throws Exception {
		setupTestOperationRow(false);
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRow();
		assertThat(operationRows).hasSize(1);
	}

	@Test
	void testRowIoCountAllActiveOperations() throws Exception {
		setupTestOperationRow(false);
		assertThat(operationRowIoOperations.countAllActiveOperations()).isEqualTo(1);
	}

	@Test
	void testRowMgrGetOperationRowByPatientCode() throws Exception {
		int id = setupTestOperationRowWithAdmission(true);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		Patient patient = operationRow.getAdmission().getPatient();
		List<OperationRow> operationRows = operationRowBrowserManager.getOperationRowByPatientCode(patient);
		assertThat(operationRows).isNotEmpty();
		assertThat(operationRows.get(0).getAdmission().getPatient()).isEqualTo(patient);
	}

	@Test
	void testRowIoGetOperationRowByPatient() throws Exception {
		int id = setupTestOperationRowWithAdmission(true);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		Patient patient = operationRow.getAdmission().getPatient();
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRowByPatient(patient);
		assertThat(operationRows).isNotEmpty();
		assertThat(operationRows.get(0).getAdmission().getPatient()).isEqualTo(patient);
	}

	@Test
	void testRowIoGetOperationRowByPatientNotFound() throws Exception {
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
	void testRowIoGetRowByAdmission() throws Exception {
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
	void testRowIoGetRowByAdmissionNotFound() throws Exception {
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
	void testRowIoGetOperationRowByOpdNotPersisted() throws Exception {
		setupTestOperationRow(true);
		Opd opd = testOpd.setup(new Patient(), new Disease(), new Ward(), new Visit(), false);
		assertThat(operationRowIoOperations.getOperationRowByOpd(opd)).isEmpty();
	}

	@Test
	void testRowIoGetOperationRowByOpd() throws Exception {
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
	void testRowIoDeleteOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		assertThatThrownBy(() -> operationRowIoOperations.deleteOperationRow(operationRow))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testRowIoDeleteOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		operationRowIoOperations.deleteOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(id)).isNull();
	}

	@Test
	void testRowIoUpdateOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		// The method is void so there is no way to tell if anything happened
		operationRowIoOperations.updateOperationRow(operationRow);
		// To prove that it doesn't exist
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNull();
	}

	@Test
	void testRowIoUpdateOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isNotEqualTo("someNewRemarks");
		operationRow.setRemarks("someNewRemarks");
		operationRowIoOperations.updateOperationRow(operationRow);
		operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isEqualTo("someNewRemarks");
	}

	@Test
	void testRowIoNewOperationRow() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);

		operationRowIoOperations.newOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNotNull();
	}

	@Test
	void testMgrRowGetRowByAdmission() throws Exception {
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

		List<OperationRow> operationRows = operationRowBrowserManager.getOperationRowByAdmission(admission);
		assertThat(operationRows).hasSize(1);
	}

	@Test
	void testMgrRowGetRowByAdmissionNotFound() throws Exception {
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

		List<OperationRow> operationRows = operationRowBrowserManager.getOperationRowByAdmission(admission);
		assertThat(operationRows).isEmpty();
	}

	@Test
	void testMgrRowGetOperationRowByOpdNotPersisted() throws Exception {
		setupTestOperationRow(true);
		Opd opd = testOpd.setup(new Patient(), new Disease(), new Ward(), new Visit(), false);
		assertThat(operationRowBrowserManager.getOperationRowByOpd(opd)).isEmpty();
	}

	@Test
	void testMgrRowGetOperationRowByOpd() throws Exception {
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

		assertThat(operationRowBrowserManager.getOperationRowByOpd(opd)).isNotEmpty();
	}

	@Test
	void testMgrRowDeleteOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		assertThatThrownBy(() -> operationRowBrowserManager.deleteOperationRow(operationRow))
			.isInstanceOf(OHServiceException.class);
	}

	@Test
	void testMgrRowDeleteOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		operationRowBrowserManager.deleteOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(id)).isNull();
	}

	@Test
	void testMgrRowUpdateOperationRowNotFound() throws Exception {
		int id = setupTestOperationRowWithAdmission(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		operationRowIoOperationRepository.delete(operationRow);
		operationRow.setId(-9999);
		assertThat(operationRowBrowserManager.updateOperationRow(operationRow)).isNull();
	}

	@Test
	void testMgrRowUpdateOperationRow() throws Exception {
		int id = setupTestOperationRowWithAdmission(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isNotEqualTo("someNewRemarks");
		operationRow.setRemarks("someNewRemarks");
		assertThat(operationRowBrowserManager.updateOperationRow(operationRow).getOperation().getDescription()).isEqualTo("TestDescription");
		assertThat(operationRowBrowserManager.updateOperationRow(operationRow).getAdmission().getUserID()).isEqualTo("TestUserId");
		operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isEqualTo("someNewRemarks");
	}

	@Test
	void testMgrRowNewOperationRow() throws Exception {
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

		assertThat(operationRowBrowserManager.newOperationRow(operationRow)).isEqualTo(operationRow);
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNotNull();
	}

	@Test
	void testOperationGettersSetters() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		operation.setMajor(-9);
		assertThat(operation.getMajor()).isEqualTo(-9);

		operation.setLock(-1);
		assertThat(operation.getLock()).isEqualTo(-1);

		operation.setOpeFor("some new string");
		assertThat(operation.getOpeFor()).isEqualTo("some new string");
	}

	@Test
	void testOperationEquals() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation)
				.isEqualTo(operation)
				.isNotNull()
				.isNotEqualTo("a string");

		OperationType operationType1 = testOperationType.setup(false);
		Operation operation1 = testOperation.setup(operationType, true);

		assertThat(operation).isEqualTo(operation1);

		operation1.setCode("some new code");
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setCode(operation.getCode());
		operation.setDescription("some new description");
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setDescription(operation.getDescription());
		operationType1.setCode("new code");
		operation1.setType(operationType1);
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setType(operation.getType());
		operation1.setMajor(-1);
		assertThat(operation).isNotEqualTo(operation1);

		operation1.setMajor(operation.getMajor());
		assertThat(operation).isEqualTo(operation1);
	}

	@Test
	void testOperationHashCode() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		int hashCode = operation.hashCode();
		// get stored value
		assertThat(operation.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testOperationToString() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation).hasToString(operation.getDescription());
	}

	@Test
	void testOperationRowConstructors() throws Exception {
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

		OperationRow operationRow1 = new OperationRow(operation, "prescriber", "opResult", LocalDateTime.of(2021, 1, 1, 0, 0, 0), "remarks", admission, new Opd(),
				null, 10.0f);

		OperationRow operationRow2 = new OperationRow(1, operation, "prescriber", "opResult", LocalDateTime.of(2021, 1, 1, 0, 0, 0), "remarks", admission,
				new Opd(), null, 10.0f);

		operationRow1.setId(1);
		assertThat(operationRow1).isEqualTo(operationRow2);
	}

	@Test
	void testOperationRowEquals() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, false);

		assertThat(operationRow)
				.isEqualTo(operationRow)
				.isNotNull()
				.isNotEqualTo("some string");
	}

	@Test
	void testOperationRowHasCode() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		Operation operation = testOperation.setup(operationType, false);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		int hashCode = operationRow.hashCode();
		// use computed value
		assertThat(operationRow.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testOperationRowToString() throws Exception {
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

	private String setupTestOperation(boolean usingSet) throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, usingSet);
		operation.setOpeFor("1");
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		return operation.getCode();
	}

	private void checkOperationIntoDb(String code) throws Exception {
		Operation foundOperation = operationIoOperations.findByCode(code);
		testOperation.check(foundOperation);
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
	
	private void checkOperationRowIntoDb(int id) throws Exception {
		OperationRow foundOperationRow = operationRowIoOperationRepository.findById(id);
		testOperationRow.check(foundOperationRow);
	}
}