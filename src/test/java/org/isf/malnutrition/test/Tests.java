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
package org.isf.malnutrition.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.LocalDateTime;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
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
import org.isf.malnutrition.manager.MalnutritionManager;
import org.isf.malnutrition.model.Malnutrition;
import org.isf.malnutrition.service.MalnutritionIoOperation;
import org.isf.malnutrition.service.MalnutritionIoOperationRepository;
import org.isf.operation.model.Operation;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.test.TestOperation;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.opetype.test.TestOperationType;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.pregtreattype.test.TestPregnantTreatmentType;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestAdmission testAdmission;
	private static TestWard testWard;
	private static TestPatient testPatient;
	private static TestAdmissionType testAdmissionType;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestOperationType testOperationType;
	private static TestOperation testOperation;
	private static TestDischargeType testDischargeType;
	private static TestPregnantTreatmentType testPregnantTreatmentType;
	private static TestDeliveryType testDeliveryType;
	private static TestDeliveryResultType testDeliveryResultType;
	private static TestMalnutrition testMalnutrition;

	@Autowired
	MalnutritionIoOperation malnutritionIoOperation;
	@Autowired
	MalnutritionIoOperationRepository malnutritionIoOperationRepository;
	@Autowired
	MalnutritionManager malnutritionManager;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	OperationTypeIoOperationRepository operationTypeIoOperationRepository;
	@Autowired
	OperationIoOperationRepository operationIoOperationRepository;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;
	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;
	@Autowired
	DeliveryTypeIoOperationRepository deliveryTypeIoOperationRepository;
	@Autowired
	DeliveryResultIoOperationRepository deliveryResultIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testAdmission = new TestAdmission();
		testWard = new TestWard();
		testPatient = new TestPatient();
		testAdmissionType = new TestAdmissionType();
		testDiseaseType = new TestDiseaseType();
		testDisease = new TestDisease();
		testOperationType = new TestOperationType();
		testOperation = new TestOperation();
		testDischargeType = new TestDischargeType();
		testPregnantTreatmentType = new TestPregnantTreatmentType();
		testDeliveryType = new TestDeliveryType();
		testDeliveryResultType = new TestDeliveryResultType();
		testMalnutrition = new TestMalnutrition();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testMalnutritionGets() throws Exception {
		int code = setupTestMalnutrition(false);
		checkMalnutritionIntoDb(code);
	}

	@Test
	public void testMalnutritionSets() throws Exception {
		int code = setupTestMalnutrition(true);
		checkMalnutritionIntoDb(code);
	}

	@Test
	public void testIoGetMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		List<Malnutrition> malnutritions = malnutritionIoOperation.getMalnutritions(String.valueOf(foundMalnutrition.getAdmission().getId()));
		assertThat(malnutritions.get(malnutritions.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	public void testIoGetLastMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		Malnutrition malnutrition = malnutritionIoOperation.getLastMalnutrition(foundMalnutrition.getAdmission().getId());
		assertThat(malnutrition.getCode()).isEqualTo(code);
	}

	@Test
	public void testIoUpdateMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		foundMalnutrition.setHeight(200);
		Malnutrition result = malnutritionIoOperation.updateMalnutrition(foundMalnutrition);
		assertThat(result).isNotNull();
		Malnutrition updateMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		assertThat(updateMalnutrition.getHeight()).isCloseTo(200.0F, within(0.000001F));
	}

	@Test
	public void testIoNewMalnutrition() throws Exception {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);

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

		Malnutrition malnutrition = testMalnutrition.setup(admission, true);
		Malnutrition result = malnutritionIoOperation.newMalnutrition(malnutrition);
		assertThat(result);
		checkMalnutritionIntoDb(malnutrition.getCode());
	}

	@Test
	public void testIoDeleteMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		boolean result = malnutritionIoOperation.deleteMalnutrition(foundMalnutrition);
		assertThat(result).isTrue();
		result = malnutritionIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	// ===================================
	@Test
	public void testMgrGetMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		List<Malnutrition> malnutritions = malnutritionManager.getMalnutrition(String.valueOf(foundMalnutrition.getAdmission().getId()));
		assertThat(malnutritions.get(malnutritions.size() - 1).getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrGetLastMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		Malnutrition malnutrition = malnutritionManager.getLastMalnutrition(foundMalnutrition.getAdmission().getId());
		assertThat(malnutrition.getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrUpdateMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		foundMalnutrition.setHeight(200);
		Malnutrition result = malnutritionManager.updateMalnutrition(foundMalnutrition);
		assertThat(result).isNotNull();
		Malnutrition updateMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		assertThat(updateMalnutrition.getHeight()).isCloseTo(200.0F, within(0.000001F));
	}

	@Test
	public void testMgrNewMalnutrition() throws Exception {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);

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

		Malnutrition malnutrition = testMalnutrition.setup(admission, true);
		Malnutrition result = malnutritionManager.newMalnutrition(malnutrition);
		assertThat(result);
		checkMalnutritionIntoDb(malnutrition.getCode());
	}

	@Test
	public void testMgrNewMalnutritionValidateNoDateSupp() throws Exception {
		assertThatThrownBy(() ->
		{
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false);
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
					deliveryType, deliveryResult, true);

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

			Malnutrition malnutrition = testMalnutrition.setup(admission, true);

			malnutrition.setDateSupp(null);

			malnutritionManager.newMalnutrition(malnutrition);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewMalnutritionValidateNoDateConf() throws Exception {
		assertThatThrownBy(() ->
		{
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false);
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
					deliveryType, deliveryResult, true);

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

			Malnutrition malnutrition = testMalnutrition.setup(admission, true);

			malnutrition.setDateConf(null);

			malnutritionManager.newMalnutrition(malnutrition);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewMalnutritionValidateDatesOutOfOrder() throws Exception {
		assertThatThrownBy(() ->
		{
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false);
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
					deliveryType, deliveryResult, true);

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

			Malnutrition malnutrition = testMalnutrition.setup(admission, true);

			malnutrition.setDateConf(LocalDateTime.of(1, 1, 1, 0, 0, 0));
			malnutrition.setDateSupp(LocalDateTime.of(2, 2, 2, 0, 0, 0));

			malnutritionManager.newMalnutrition(malnutrition);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewMalnutritionValidateDatesNoWeight() throws Exception {
		assertThatThrownBy(() ->
		{
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false);
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
					deliveryType, deliveryResult, true);

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

			Malnutrition malnutrition = testMalnutrition.setup(admission, true);

			malnutrition.setWeight(0);

			malnutritionManager.newMalnutrition(malnutrition);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrNewMalnutritionValidateDatesNoHeight() throws Exception {
		assertThatThrownBy(() ->
		{
			Ward ward = testWard.setup(false);
			Patient patient = testPatient.setup(true);
			AdmissionType admissionType = testAdmissionType.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease diseaseIn = testDisease.setup(diseaseType, false);
			Disease diseaseOut1 = testDisease.setup(diseaseType, false);
			diseaseOut1.setCode("888");
			Disease diseaseOut2 = testDisease.setup(diseaseType, false);
			diseaseOut2.setCode("777");
			Disease diseaseOut3 = testDisease.setup(diseaseType, false);
			diseaseOut3.setCode("666");
			OperationType operationType = testOperationType.setup(false);
			Operation operation = testOperation.setup(operationType, false);
			DischargeType dischargeType = testDischargeType.setup(false);
			PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
			DeliveryType deliveryType = testDeliveryType.setup(false);
			DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
			Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
					diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
					deliveryType, deliveryResult, true);

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

			Malnutrition malnutrition = testMalnutrition.setup(admission, true);

			malnutrition.setHeight(0);

			malnutritionManager.newMalnutrition(malnutrition);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMalnutritionConstructor() throws Exception {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);

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

		Malnutrition malnutrition = new Malnutrition(0, LocalDateTime.of(1, 1, 1, 0, 0, 0),
				LocalDateTime.of(1, 10, 11, 0, 0, 0), admission, patient, 185.47f, 70.70f);
		assertThat(malnutrition).isNotNull();
		assertThat(malnutrition.getCode()).isZero();
	}

	@Test
	public void testMalnutritionGetterSetter() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition malnutrition = malnutritionIoOperationRepository.findById(code).get();

		malnutrition.setCode(-1);
		assertThat(malnutrition.getCode()).isEqualTo(-1);

		assertThat(malnutrition.getLock()).isZero();
		malnutrition.setLock(-1);
		assertThat(malnutrition.getLock()).isEqualTo(-1);
	}

	@Test
	public void testMalnutritionEquals() throws Exception {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);

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

		Malnutrition malnutrition1 = new Malnutrition(0, LocalDateTime.of(1, 1, 1, 0, 0, 0),
				LocalDateTime.of(1, 10, 11, 0, 0, 0), admission, patient, 185.47f, 70.70f);

		// matches itself
		assertThat(malnutrition1.equals(malnutrition1)).isTrue();

		// does not match because wrong class
		assertThat(malnutrition1)
				.isNotNull()
				.isNotEqualTo("xyzzy");

		Malnutrition malnutrition2 = new Malnutrition(0, LocalDateTime.of(11, 1, 1, 0, 0, 0),
				LocalDateTime.of(11, 10, 11, 0, 0, 0), admission, patient, 1185.47f, 170.70f);

		// does not match because dates do not match
		assertThat(malnutrition1).isNotEqualTo(malnutrition2);

		Malnutrition malnutrition3 = new Malnutrition(0, LocalDateTime.of(111, 1, 1, 0, 0, 0),
				LocalDateTime.of(111, 10, 11, 0, 0, 0), admission, patient, 4185.47f, 470.70f);

		malnutrition2.setDateConf(null);
		malnutrition2.setDateSupp(null);

		malnutrition3.setDateConf(null);
		malnutrition3.setDateSupp(null);

		// dates are null but the height and weight do not match
		assertThat(malnutrition2).isNotEqualTo(malnutrition3);

		Malnutrition malnutrition4 = new Malnutrition(0, LocalDateTime.of(1, 1, 1, 0, 0, 0),
				LocalDateTime.of(1, 10, 11, 0, 0, 0), admission, patient, 185.47f, 70.70f);

		// matches because all the same values
		assertThat(malnutrition4).isEqualTo(malnutrition1);
	}

	@Test
	public void testMalnutritionHasCode() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition malnutrition = malnutritionIoOperationRepository.findById(code).get();
		// compute first time
		int hashCode = malnutrition.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + code);
		// use stored value
		assertThat(malnutrition.hashCode()).isEqualTo(23 * 133 + code);
	}

	@Test
	public void testMgrDeleteMalnutrition() throws Exception {
		int code = setupTestMalnutrition(false);
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		boolean result = malnutritionManager.deleteMalnutrition(foundMalnutrition);
		assertThat(result).isTrue();
		result = malnutritionIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private int setupTestMalnutrition(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(false);
		Patient patient = testPatient.setup(true);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		Disease diseaseOut2 = testDisease.setup(diseaseType, false);
		diseaseOut2.setCode("777");
		Disease diseaseOut3 = testDisease.setup(diseaseType, false);
		diseaseOut3.setCode("666");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		DischargeType dischargeType = testDischargeType.setup(false);
		PregnantTreatmentType pregTreatmentType = testPregnantTreatmentType.setup(false);
		DeliveryType deliveryType = testDeliveryType.setup(false);
		DeliveryResultType deliveryResult = testDeliveryResultType.setup(false);
		Admission admission = testAdmission
				.setup(ward, patient, admissionType, diseaseIn, diseaseOut1, diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
						deliveryType, deliveryResult, false);

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

		Malnutrition malnutrition = testMalnutrition.setup(admission, usingSet);
		malnutritionIoOperationRepository.saveAndFlush(malnutrition);
		return malnutrition.getCode();
	}

	private void checkMalnutritionIntoDb(int code) throws OHException {
		Malnutrition foundMalnutrition = malnutritionIoOperationRepository.findById(code).get();
		testMalnutrition.check(foundMalnutrition);
	}
}