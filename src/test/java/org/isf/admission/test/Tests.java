/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.admission.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperations;
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
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	AdmissionIoOperations admissionIoOperation;
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
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testAdmissionGets() throws Exception {
		int id = _setupTestAdmission(false);
		_checkAdmissionIntoDb(id);
	}

	@Test
	public void testAdmissionSets() throws Exception {
		int id = _setupTestAdmission(true);
		_checkAdmissionIntoDb(id);
	}

	@Test
	public void test_simple_getAdmittedPatients() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void test_getAdmittedPatient_with_dateRanges() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		final GregorianCalendar admissionDate = foundAdmission.getAdmDate();
		final GregorianCalendar dischargeDate = foundAdmission.getDisDate();
		{
			List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null, null, null);
			assertThat(searchResult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		}
		final GregorianCalendar beforeAdmissionDate = copyFrom(admissionDate);
		beforeAdmissionDate.add(Calendar.DATE, -1);

		final GregorianCalendar oneDayAfterAdmissionDate = copyFrom(admissionDate);
		oneDayAfterAdmissionDate.add(Calendar.DATE, 1);

		final GregorianCalendar twoDaysAfterAdmissionDate = copyFrom(admissionDate);
		twoDaysAfterAdmissionDate.add(Calendar.DATE, 2);

		final GregorianCalendar beforeDischargeDate = copyFrom(dischargeDate);
		beforeDischargeDate.add(Calendar.DATE, -1);

		final GregorianCalendar oneDayAfterDischargeDate = copyFrom(dischargeDate);
		oneDayAfterDischargeDate.add(Calendar.DATE, 1);

		final GregorianCalendar twoDaysAfterDischargeDate = copyFrom(dischargeDate);
		twoDaysAfterDischargeDate.add(Calendar.DATE, 2);
		{
			// search by admission date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
					null
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[] { oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate },
					null
			);
			assertThat(searchTwoResult).isEmpty();
		}
		{
			// search by discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[] { beforeDischargeDate, oneDayAfterDischargeDate }
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

			final List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null, null,
					new GregorianCalendar[] { oneDayAfterDischargeDate, twoDaysAfterDischargeDate }
			);
			assertThat(searchTwoResult).isEmpty();
		}
		{
			// complex search by both admission and discharge date
			final List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
					new GregorianCalendar[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
					new GregorianCalendar[] { beforeDischargeDate, oneDayAfterDischargeDate }
			);
			assertThat(searchOneresult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		}
	}

	private GregorianCalendar copyFrom(final GregorianCalendar source) {
		return new GregorianCalendar(source.get(Calendar.YEAR), source.get(Calendar.MONTH), source.get(Calendar.DATE));
	}

	@Test
	public void testIoGetAdmittedPatients() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionIoOperation.getAdmittedPatients(null);
		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeId() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		GregorianCalendar[] admissionRange = {
				new DateTime(foundAdmission.getAdmDate()).minusDays(2).toGregorianCalendar(),
				new DateTime(foundAdmission.getAdmDate()).minusDays(1).toGregorianCalendar()
		};
		GregorianCalendar[] dischargeRange = {
				new DateTime(foundAdmission.getDisDate()).minusDays(1).toGregorianCalendar(),
				new DateTime(foundAdmission.getDisDate()).plusDays(1).toGregorianCalendar()
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString(), admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange() throws Exception {
		// given:
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		GregorianCalendar[] admissionRange = {
				new DateTime(foundAdmission.getAdmDate()).minusDays(1).toGregorianCalendar(),
				new DateTime(foundAdmission.getAdmDate()).plusDays(1).toGregorianCalendar()
		};
		GregorianCalendar[] dischargeRange = {
				new DateTime(foundAdmission.getDisDate()).minusDays(2).toGregorianCalendar(),
				new DateTime(foundAdmission.getDisDate()).minusDays(1).toGregorianCalendar()
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(foundPatient.getCode().toString(), admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetCurrentAdmission() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@Test
	public void testIoGetAdmission() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		testAdmission.check(foundAdmission);
	}

	@Test
	public void testIoGetAdmissions() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoNewAdmission() throws Exception {
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

		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);
		boolean result = admissionIoOperation.newAdmission(admission);
		assertThat(result).isTrue();
		_checkAdmissionIntoDb(admission.getId());
	}

	@Test
	public void testIoNewAdmissionReturnKey() throws Exception {
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

		Admission admission = testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);
		admissionIoOperationRepository.saveAndFlush(admission);
		int id = admissionIoOperation.newAdmissionReturnKey(admission);
		_checkAdmissionIntoDb(id);
	}

	@Test
	public void testIoUpdateAdmission() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setNote("Update");
		boolean result = admissionIoOperation.updateAdmission(foundAdmission);
		Admission updateAdmission = admissionIoOperation.getAdmission(id);

		assertThat(result).isTrue();
		assertThat(updateAdmission.getNote()).isEqualTo("Update");
	}

	@Test
	public void testIoGetAdmissionType() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@Test
	public void testIoGetDischargeType() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@Test
	public void testIoGetNextYProg() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
	}

	@Test
	public void testIoSetDeleted() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		boolean result = admissionIoOperation.setDeleted(foundAdmission.getId());
		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetUsedWardBed() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		int result = admissionIoOperation.getUsedWardBed(foundAdmission.getWard().getCode());
		assertThat(result).isEqualTo(1);
	}

	@Test
	@Transactional // requires active session because of lazy loading of patient photo
	public void testIoDeletePatientPhoto() throws Exception {
		int id = _setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		boolean result = admissionIoOperation.deletePatientPhoto(foundAdmission.getPatient().getCode());
		assertThat(result).isTrue();
		assertThat(foundAdmission.getPatient().getPatientProfilePhoto().getPhoto()).isNull();
	}

	private int _setupTestAdmission(boolean usingSet) throws OHException, InterruptedException {
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
				deliveryType, deliveryResult, usingSet);

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
		return admission.getId();
	}

	private void _checkAdmissionIntoDb(int id) throws OHServiceException {
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		testAdmission.check(foundAdmission);
	}
}
