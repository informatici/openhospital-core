/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.operation.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperations;
import org.isf.admission.test.TestAdmission;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.admtype.test.TestAdmissionType;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.disctype.test.TestDischargeType;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.dlvrrestype.model.DeliveryResultType;
import org.isf.dlvrrestype.service.DeliveryResultIoOperationRepository;
import org.isf.dlvrrestype.test.TestDeliveryResultType;
import org.isf.dlvrtype.model.DeliveryType;
import org.isf.dlvrtype.service.DeliveryTypeIoOperationRepository;
import org.isf.dlvrtype.test.TestDeliveryType;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.test.TestOpd;
import org.isf.operation.manager.OperationBrowserManager;
import org.isf.operation.manager.OperationRowBrowserManager;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationIoOperations;
import org.isf.operation.service.OperationRowIoOperationRepository;
import org.isf.operation.service.OperationRowIoOperations;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.opetype.test.TestOperationType;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.pregtreattype.test.TestPregnantTreatmentType;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.test.TestVisit;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() {
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

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testOperationGets() throws Exception {
		String code = setupTestOperation(false);
		checkOperationIntoDb(code);
	}

	@Test
	public void testOperationSets() throws Exception {
		String code = setupTestOperation(true);
		checkOperationIntoDb(code);
	}

	@Test
	public void testOperationRowGets() throws Exception {
		int id = setupTestOperationRow(false);
		checkOperationRowIntoDb(id);
	}

	@Test
	public void testOperationRowSets() throws Exception {
		int id = setupTestOperationRow(true);
		checkOperationRowIntoDb(id);
	}

	@Test
	public void testIoGetOperationByTypeDescription() throws Exception {
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
	public void testIoGetOperationByTypeDescriptionNull() throws Exception {
		// given:
		String code = setupTestOperation(true);

		// when:
		List<Operation> operations = operationIoOperations.getOperationByTypeDescription(null);

		// then:
		assertThat(operations).isNotEmpty();
	}

	@Test
	public void testIoGetOperationOpdOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	public void testIoGetOperationOpdOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isNotEmpty();
	}

	@Test
	public void testIoGetOperationOpdNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationOpd()).isEmpty();
	}

	@Test
	public void testIoGetOperationAdmOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	public void testIoGetOperationAdmAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isNotEmpty();
	}

	@Test
	public void testIoGetOperationAdmNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationIoOperations.getOperationAdm()).isEmpty();
	}

	@Test
	public void testIoNewOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationIoOperations.newOperation(operation));
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	public void testIoUpdateOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationIoOperations.updateOperation(foundOperation));
		Operation updateOperation = operationIoOperations.findByCode(code);
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	public void testIoDeleteOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.deleteOperation(foundOperation)).isTrue();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationIoOperations.isCodePresent(code)).isTrue();
	}

	@Test
	public void testIoIsDescriptionPresent() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	public void testIoIsDescriptionPresentNotFound() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationIoOperations.findByCode(code);
		assertThat(operationIoOperations.isDescriptionPresent("someOtherDescription", foundOperation.getType().getCode())).isFalse();
	}

	@Test
	public void testMgrGetOperationByTypeDescription() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(foundOperation.getType().getDescription());
		assertThat(operations).isNotEmpty();
		assertThat(operations.get(0).getType().getDescription()).isEqualTo(foundOperation.getType().getDescription());
	}

	@Test
	public void testMgrGetOperationByTypeDescriptionNull() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperationByTypeDescription(null);
		assertThat(operations).isNotEmpty();
	}

	@Test
	public void testMgrGetOperation() throws Exception {
		setupTestOperation(true);
		List<Operation> operations = operationBrowserManager.getOperation();
		assertThat(operations).isNotEmpty();
	}

	@Test
	public void testMgrGetOperationOpdOpdAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	public void testMgrGetOperationOpdOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isNotEmpty();
	}

	@Test
	public void testMgrGetOperationOpdNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationOpd()).isEmpty();
	}

	@Test
	public void testMgrGetOperationAdmOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("1"); // "1" = OPD / ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	public void testMgrGetOperationAdmAdmission() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("2"); // "2" = ADMISSION
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isNotEmpty();
	}

	@Test
	public void testMgrGetOperationAdmNotOpd() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		operation.setOpeFor("3"); // "3" = OPD
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		assertThat(operationBrowserManager.getOperationAdm()).isEmpty();
	}

	@Test
	public void testMgrNewOperation() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		Operation operation = testOperation.setup(operationType, true);
		assertThat(operationBrowserManager.newOperation(operation));
		checkOperationIntoDb(operation.getCode());
	}

	@Test
	public void testMgrUpdateOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		int lock = foundOperation.getLock();
		foundOperation.setDescription("Update");
		assertThat(operationBrowserManager.updateOperation(foundOperation));
		Operation updateOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(updateOperation.getDescription()).isEqualTo("Update");
		assertThat(updateOperation.getLock().intValue()).isEqualTo(lock + 1);
	}

	@Test
	public void testMgrDeleteOperation() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.deleteOperation(foundOperation)).isTrue();
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestOperation(false);
		assertThat(operationBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	public void testMgrDescriptionControl() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl(foundOperation.getDescription(), foundOperation.getType().getCode())).isTrue();
	}

	@Test
	public void testMgrDescriptionControlNotFound() throws Exception {
		String code = setupTestOperation(false);
		Operation foundOperation = operationBrowserManager.getOperationByCode(code);
		assertThat(operationBrowserManager.descriptionControl("someOtherDescription", foundOperation.getType().getCode())).isFalse();
	}

	@Test
	public void testMgrGetResultsList() throws Exception {
		assertThat(operationBrowserManager.getResultsList()).hasSize(3);
	}

	@Test
	public void testRowIoGetRowOperation() throws Exception {
		setupTestOperationRow(false);
		List<OperationRow> operationRows = operationRowIoOperations.getOperationRow();
		assertThat(operationRows).hasSize(1);
	}

	@Test
	public void testRowIoGetRowByAdmission() throws Exception {
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
	public void testRowIoGetRowByAdmissionNotFound() throws Exception {
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
	public void testRowIoGetOperationRowByOpdNotPersisted() throws Exception {
		setupTestOperationRow(true);
		Opd opd = testOpd.setup(new Patient(), new Disease(), new Ward(), new Visit(), false);
		assertThat(operationRowIoOperations.getOperationRowByOpd(opd)).isEmpty();
	}

	@Test
	public void testRowIoGetOperationRowByOpd() throws Exception {
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
	public void testRowIoDeleteOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		assertThat(operationRowIoOperations.deleteOperationRow(operationRow)).isFalse();
	}

	@Test
	public void testRowIoDeleteOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRowIoOperations.deleteOperationRow(operationRow)).isTrue();
		assertThat(operationRowIoOperationRepository.findById(id)).isNull();
	}

	@Test
	public void testRowIoUpdateOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);
		// The method is void so there is no way to tell if anything happened
		operationRowIoOperations.updateOperationRow(operationRow);
		// To prove that it doesn't exist
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNull();
	}

	@Test
	public void testRowIoUpdateOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isNotEqualTo("someNewRemarks");
		operationRow.setRemarks("someNewRemarks");
		operationRowIoOperations.updateOperationRow(operationRow);
		operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isEqualTo("someNewRemarks");
	}

	@Test
	public void testRowIoNewOperationRow() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);

		operationRowIoOperations.newOperationRow(operationRow);
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNotNull();
	}

	@Test
	public void testMgrRowGetRowByAdmission() throws Exception {
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
	public void testMgrRowGetRowByAdmissionNotFound() throws Exception {
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
	public void testMgrRowGetOperationRowByOpdNotPersisted() throws Exception {
		setupTestOperationRow(true);
		Opd opd = testOpd.setup(new Patient(), new Disease(), new Ward(), new Visit(), false);
		assertThat(operationRowBrowserManager.getOperationRowByOpd(opd)).isEmpty();
	}

	@Test
	public void testMgrRowGetOperationRowByOpd() throws Exception {
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
	public void testMgrRowDeleteOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		assertThat(operationRowBrowserManager.deleteOperationRow(operationRow)).isFalse();
	}

	@Test
	public void testMgrRowDeleteOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRowBrowserManager.deleteOperationRow(operationRow)).isTrue();
		assertThat(operationRowIoOperationRepository.findById(id)).isNull();
	}

	@Test
	public void testMgrRowUpdateOperationRowNotFound() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		assertThat(operationRowBrowserManager.updateOperationRow(operationRow)).isTrue();
	}

	@Test
	public void testMgrRowUpdateOperationRow() throws Exception {
		int id = setupTestOperationRow(false);
		OperationRow operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isNotEqualTo("someNewRemarks");
		operationRow.setRemarks("someNewRemarks");
		assertThat(operationRowBrowserManager.updateOperationRow(operationRow)).isTrue();
		operationRow = operationRowIoOperationRepository.findById(id);
		assertThat(operationRow.getRemarks()).isEqualTo("someNewRemarks");
	}

	@Test
	public void testMgrRowNewOperationRow() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);

		assertThat(operationRowBrowserManager.newOperationRow(operationRow)).isTrue();
		assertThat(operationRowIoOperationRepository.findById(operationRow.getId())).isNotNull();
	}

	@Test
	public void testOperationGettersSetters() throws Exception {
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
	public void testOperationEquals() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation).isEqualTo(operation);
		assertThat(operation)
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
	public void testOperationHashCode() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		int hashCode = operation.hashCode();
		// get stored value
		assertThat(operation.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testOperationToString() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);

		assertThat(operation).hasToString(operation.getDescription());
	}

	@Test
	public void testOperationRowConstructors() throws Exception {
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
				null, 10.0F);

		OperationRow operationRow2 = new OperationRow(1, operation, "prescriber", "opResult", LocalDateTime.of(2021, 1, 1, 0, 0, 0), "remarks", admission,
				new Opd(), null, 10.0F);

		operationRow1.setId(1);
		assertThat(operationRow1).isEqualTo(operationRow2);
	}

	@Test
	public void testOperationRowEquals() throws Exception {
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, true);
		OperationRow operationRow = testOperationRow.setup(operation, false);

		assertThat(operationRow.equals(operationRow)).isTrue();
		assertThat(operationRow)
				.isNotNull()
				.isNotEqualTo("some string");
	}

	@Test
	public void testOperationRowHasCode() throws Exception {
		OperationType operationType = testOperationType.setup(true);
		Operation operation = testOperation.setup(operationType, false);
		OperationRow operationRow = testOperationRow.setup(operation, true);

		int hashCode = operationRow.hashCode();
		// use computed value
		assertThat(operationRow.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testOperationRowToString() throws Exception {
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

	private void checkOperationRowIntoDb(int id) throws Exception {
		OperationRow foundOperationRow = operationRowIoOperationRepository.findById(id);
		testOperationRow.check(foundOperationRow);
	}
}