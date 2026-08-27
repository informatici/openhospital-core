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
package org.isf.patient;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.accounting.TestBill;
import org.isf.accounting.TestBillItems;
import org.isf.accounting.TestBillPayments;
import org.isf.accounting.model.Bill;
import org.isf.accounting.model.BillItems;
import org.isf.accounting.model.BillPayments;
import org.isf.accounting.service.AccountingBillIoOperationRepository;
import org.isf.accounting.service.AccountingBillItemsIoOperationRepository;
import org.isf.accounting.service.AccountingBillPaymentIoOperationRepository;
import org.isf.admission.TestAdmission;
import org.isf.admission.model.Admission;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.disease.TestDisease;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.exa.TestExam;
import org.isf.exa.model.Exam;
import org.isf.exa.service.ExamIoOperationRepository;
import org.isf.examination.TestPatientExamination;
import org.isf.examination.model.PatientExamination;
import org.isf.examination.service.ExaminationIoOperationRepository;
import org.isf.exatype.TestExamType;
import org.isf.exatype.model.ExamType;
import org.isf.exatype.service.ExamTypeIoOperationRepository;
import org.isf.generaldata.GeneralData;
import org.isf.lab.TestLaboratory;
import org.isf.lab.model.Laboratory;
import org.isf.lab.service.LabIoOperationRepository;
import org.isf.medicals.TestMedical;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medtype.TestMedicalType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.opd.TestOpd;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.operation.TestOperation;
import org.isf.operation.TestOperationRow;
import org.isf.operation.model.Operation;
import org.isf.operation.model.OperationRow;
import org.isf.operation.service.OperationIoOperationRepository;
import org.isf.operation.service.OperationRowIoOperationRepository;
import org.isf.opetype.TestOperationType;
import org.isf.opetype.model.OperationType;
import org.isf.opetype.service.OperationTypeIoOperationRepository;
import org.isf.patient.dto.PatientExport;
import org.isf.patient.manager.PatientExportManager;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patvac.TestPatientVaccine;
import org.isf.patvac.model.PatientVaccine;
import org.isf.patvac.service.PatVacIoOperationRepository;
import org.isf.priceslist.TestPriceList;
import org.isf.priceslist.model.PriceList;
import org.isf.priceslist.service.PricesListIoOperationRepository;
import org.isf.therapy.TestTherapy;
import org.isf.therapy.model.TherapyRow;
import org.isf.therapy.service.TherapyIoOperationRepository;
import org.isf.vaccine.TestVaccine;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vactype.TestVaccineType;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.isf.ward.TestWard;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TestPatientExport extends OHCoreTestCase {

	private static TestPatient testPatient;
	private static TestWard testWard;
	private static TestAdmissionType testAdmissionType;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;
	private static TestOperationType testOperationType;
	private static TestOperation testOperation;
	private static TestOperationRow testOperationRow;
	private static TestAdmission testAdmission;
	private static TestOpd testOpd;
	private static TestExamType testExamType;
	private static TestExam testExam;
	private static TestLaboratory testLaboratory;
	private static TestMedicalType testMedicalType;
	private static TestMedical testMedical;
	private static TestTherapy testTherapy;
	private static TestVaccineType testVaccineType;
	private static TestVaccine testVaccine;
	private static TestPatientVaccine testPatientVaccine;
	private static TestPatientExamination testPatientExamination;
	private static TestPriceList testPriceList;
	private static TestBill testBill;
	private static TestBillItems testBillItems;
	private static TestBillPayments testBillPayments;

	@Autowired
	PatientExportManager patientExportManager;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
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
	OperationRowIoOperationRepository operationRowIoOperationRepository;
	@Autowired
	AdmissionIoOperationRepository admissionIoOperationRepository;
	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;
	@Autowired
	ExamTypeIoOperationRepository examTypeIoOperationRepository;
	@Autowired
	ExamIoOperationRepository examIoOperationRepository;
	@Autowired
	LabIoOperationRepository labIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	TherapyIoOperationRepository therapyIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	PatVacIoOperationRepository patVacIoOperationRepository;
	@Autowired
	ExaminationIoOperationRepository examinationIoOperationRepository;
	@Autowired
	PricesListIoOperationRepository priceListIoOperationRepository;
	@Autowired
	AccountingBillIoOperationRepository accountingBillIoOperationRepository;
	@Autowired
	AccountingBillItemsIoOperationRepository accountingBillItemsIoOperationRepository;
	@Autowired
	AccountingBillPaymentIoOperationRepository accountingBillPaymentIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
		testPatient = new TestPatient();
		testWard = new TestWard();
		testAdmissionType = new TestAdmissionType();
		testDiseaseType = new TestDiseaseType();
		testDisease = new TestDisease();
		testOperationType = new TestOperationType();
		testOperation = new TestOperation();
		testOperationRow = new TestOperationRow();
		testAdmission = new TestAdmission();
		testOpd = new TestOpd();
		testExamType = new TestExamType();
		testExam = new TestExam();
		testLaboratory = new TestLaboratory();
		testMedicalType = new TestMedicalType();
		testMedical = new TestMedical();
		testTherapy = new TestTherapy();
		testVaccineType = new TestVaccineType();
		testVaccine = new TestVaccine();
		testPatientVaccine = new TestPatientVaccine();
		testPatientExamination = new TestPatientExamination();
		testPriceList = new TestPriceList();
		testBill = new TestBill();
		testBillItems = new TestBillItems();
		testBillPayments = new TestBillPayments();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testExportPatientDataAllGroupsPresent() throws Exception {
		Patient patient = setupFullyPopulatedPatient();

		PatientExport export = patientExportManager.exportPatientData(patient.getCode());

		assertThat(export).isNotNull();
		assertThat(export.getPatient()).isNotNull();
		assertThat(export.getPatient().getCode()).isEqualTo(patient.getCode());
		assertThat(export.getAdmissions()).hasSize(1);
		assertThat(export.getOpds()).hasSize(1);
		assertThat(export.getLaboratories()).hasSize(1);
		assertThat(export.getTherapies()).hasSize(1);
		assertThat(export.getOperations()).hasSize(1);
		// vaccines are asserted explicitly: they are reported missing from the clinical sheet report
		assertThat(export.getVaccines()).hasSize(1);
		assertThat(export.getVaccines().get(0).getPatient().getCode()).isEqualTo(patient.getCode());
		assertThat(export.getExaminations()).hasSize(1);
		assertThat(export.getBills()).hasSize(1);
		assertThat(export.getBillItems()).hasSize(1);
		assertThat(export.getBillPayments()).hasSize(1);
	}

	@Test
	void testExportPatientDataDoesNotIncludeOtherPatientsRecords() throws Exception {
		Patient patient = setupFullyPopulatedPatient();

		Patient otherPatient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));
		Vaccine vaccine = vaccineIoOperationRepository.findAll().get(0);
		PatientVaccine otherPatientVaccine = testPatientVaccine.setup(otherPatient, vaccine, false);
		patVacIoOperationRepository.saveAndFlush(otherPatientVaccine);
		PatientExamination otherPatientExamination = testPatientExamination.setup(otherPatient, false);
		examinationIoOperationRepository.saveAndFlush(otherPatientExamination);

		PatientExport export = patientExportManager.exportPatientData(patient.getCode());
		assertThat(export).isNotNull();
		assertThat(export.getVaccines()).hasSize(1);
		assertThat(export.getVaccines().get(0).getPatient().getCode()).isEqualTo(patient.getCode());
		assertThat(export.getExaminations()).hasSize(1);
		assertThat(export.getExaminations().get(0).getPatient().getCode()).isEqualTo(patient.getCode());

		PatientExport otherExport = patientExportManager.exportPatientData(otherPatient.getCode());
		assertThat(otherExport).isNotNull();
		assertThat(otherExport.getVaccines()).hasSize(1);
		assertThat(otherExport.getVaccines().get(0).getPatient().getCode()).isEqualTo(otherPatient.getCode());
		assertThat(otherExport.getAdmissions()).isEmpty();
		assertThat(otherExport.getBills()).isEmpty();
	}

	@Test
	void testExportPatientDataPatientNotFound() throws Exception {
		assertThat(patientExportManager.exportPatientData(999999)).isNull();
	}

	@Test
	void testExportPatientDataNonPositivePatientId() throws Exception {
		// 0 must not be forwarded to the aggregated managers: some of them return ALL records for id 0
		setupFullyPopulatedPatient();
		assertThat(patientExportManager.exportPatientData(0)).isNull();
		assertThat(patientExportManager.exportPatientData(-1)).isNull();
	}

	@Test
	void testExportPatientDataPatientWithNoConnectedRecords() throws Exception {
		Patient patient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));

		PatientExport export = patientExportManager.exportPatientData(patient.getCode());

		assertThat(export).isNotNull();
		assertThat(export.getPatient().getCode()).isEqualTo(patient.getCode());
		assertThat(export.getAdmissions()).isEmpty();
		assertThat(export.getOpds()).isEmpty();
		assertThat(export.getLaboratories()).isEmpty();
		assertThat(export.getTherapies()).isEmpty();
		assertThat(export.getOperations()).isEmpty();
		assertThat(export.getVaccines()).isEmpty();
		assertThat(export.getExaminations()).isEmpty();
		assertThat(export.getBills()).isEmpty();
		assertThat(export.getBillItems()).isEmpty();
		assertThat(export.getBillPayments()).isEmpty();
	}

	private Patient setupFullyPopulatedPatient() throws Exception {
		Patient patient = patientIoOperationRepository.saveAndFlush(testPatient.setup(false));

		// admission (with operation row attached to it)
		Ward ward = testWard.setup(false, false);
		AdmissionType admissionType = testAdmissionType.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease diseaseIn = testDisease.setup(diseaseType, false);
		Disease diseaseOut1 = testDisease.setup(diseaseType, false);
		diseaseOut1.setCode("888");
		OperationType operationType = testOperationType.setup(false);
		Operation operation = testOperation.setup(operationType, false);
		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
			null, null, operation, null, null, null, null, false);
		wardIoOperationRepository.saveAndFlush(ward);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(diseaseIn);
		diseaseIoOperationRepository.saveAndFlush(diseaseOut1);
		operationTypeIoOperationRepository.saveAndFlush(operationType);
		operationIoOperationRepository.saveAndFlush(operation);
		admissionIoOperationRepository.saveAndFlush(admission);

		OperationRow operationRow = testOperationRow.setup(operation, true);
		operationRow.setAdmission(admission);
		operationRowIoOperationRepository.saveAndFlush(operationRow);

		// OPD
		Opd opd = testOpd.setup(patient, diseaseIn, ward, null, false);
		opdIoOperationRepository.saveAndFlush(opd);

		// laboratory
		ExamType examType = testExamType.setup(false);
		Exam exam = testExam.setup(examType, 1, false);
		Laboratory laboratory = testLaboratory.setup(exam, patient, false);
		examTypeIoOperationRepository.saveAndFlush(examType);
		examIoOperationRepository.saveAndFlush(exam);
		labIoOperationRepository.saveAndFlush(laboratory);

		// therapy
		MedicalType medicalType = testMedicalType.setup(false);
		Medical medical = testMedical.setup(medicalType, true);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		TherapyRow therapyRow = testTherapy.setup(patient, medical, false);
		therapyIoOperationRepository.saveAndFlush(therapyRow);

		// vaccine
		VaccineType vaccineType = testVaccineType.setup(false);
		Vaccine vaccine = testVaccine.setup(vaccineType, false);
		PatientVaccine patientVaccine = testPatientVaccine.setup(patient, vaccine, false);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		vaccineIoOperationRepository.saveAndFlush(vaccine);
		patVacIoOperationRepository.saveAndFlush(patientVaccine);

		// examination
		PatientExamination patientExamination = testPatientExamination.setup(patient, false);
		examinationIoOperationRepository.saveAndFlush(patientExamination);

		// bill with one item and one payment
		PriceList priceList = testPriceList.setup(false);
		priceListIoOperationRepository.saveAndFlush(priceList);
		Bill bill = testBill.setup(priceList, patient, null, false);
		accountingBillIoOperationRepository.saveAndFlush(bill);
		BillItems billItems = testBillItems.setup(bill, false);
		accountingBillItemsIoOperationRepository.saveAndFlush(billItems);
		BillPayments billPayments = testBillPayments.setup(bill, false);
		accountingBillPaymentIoOperationRepository.saveAndFlush(billPayments);

		return patient;
	}
}
