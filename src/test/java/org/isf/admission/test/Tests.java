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
package org.isf.admission.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.admission.manager.AdmissionBrowserManager;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.admission.service.AdmissionIoOperationRepository;
import org.isf.admission.service.AdmissionIoOperationRepositoryCustom;
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
import org.isf.generaldata.GeneralData;
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
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@RunWith(Parameterized.class)
public class Tests extends OHCoreTestCase {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

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
	AdmissionBrowserManager admissionBrowserManager;
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

	public Tests(boolean maternityRestartInJune) {
		GeneralData.MATERNITYRESTARTINJUNE = maternityRestartInJune;
	}

	@BeforeClass
	public static void setUpClass() {
		GeneralData.PATIENTPHOTOSTORAGE = "DB";
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

	@Parameterized.Parameters(name = "Test with MATERNITYRESTARTINJUNE={0}")
	public static Collection<Object[]> maternityRestartInJune() {
		return Arrays.asList(new Object[][] {
				{ false },
				{ true }
		});
	}

	@Test
	public void testAdmissionGets() throws Exception {
		int id = setupTestAdmission(false);
		checkAdmissionIntoDb(id);
	}

	@Test
	public void testAdmissionSets() throws Exception {
		int id = setupTestAdmission(true);
		checkAdmissionIntoDb(id);
	}

	@Test
	public void testSimpleGetAdmittedPatients() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testGetAdmittedPatientWithDateRanges() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		final LocalDateTime admissionDate = foundAdmission.getAdmDate();
		final LocalDateTime dischargeDate = foundAdmission.getDisDate();
		{
			List<AdmittedPatient> searchResult = admissionIoOperation.getAdmittedPatients(null, null, null);
			assertThat(searchResult).hasSameSizeAs(patients);
			assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		}
		final LocalDateTime beforeAdmissionDate = admissionDate.minusDays(1);

		final LocalDateTime oneDayAfterAdmissionDate = admissionDate.plusDays(1);

		final LocalDateTime twoDaysAfterAdmissionDate = admissionDate.plusDays(2);

		final LocalDateTime beforeDischargeDate = dischargeDate.minusDays(1);

		final LocalDateTime oneDayAfterDischargeDate = dischargeDate.plusDays(1);

		final LocalDateTime twoDaysAfterDischargeDate = dischargeDate.plusDays(2);

		// search by admission date
		List<AdmittedPatient> searchOneresult = admissionIoOperation.getAdmittedPatients(null,
				new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
				null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		List<AdmittedPatient> searchTwoResult = admissionIoOperation.getAdmittedPatients(null,
				new LocalDateTime[] { oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate },
				null);
		assertThat(searchTwoResult).isEmpty();

		// search by discharge date
		searchOneresult = admissionIoOperation.getAdmittedPatients(null, null,
				new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate });
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		searchTwoResult = admissionIoOperation.getAdmittedPatients(null, null,
				new LocalDateTime[] { oneDayAfterDischargeDate, twoDaysAfterDischargeDate });
		assertThat(searchTwoResult).isEmpty();

		// complex search by both admission and discharge date
		searchOneresult = admissionIoOperation.getAdmittedPatients(null,
				new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
				new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate });
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatients() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionIoOperation.getAdmittedPatients(null);
		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		int id = setupTestAdmission(false);
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
		int id = setupTestAdmission(false);
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
		int id = setupTestAdmission(false);
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
		int id = setupTestAdmission(false);
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
		int id = setupTestAdmission(false);
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
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
				foundAdmission.getAdmDate().minusDays(2),
				foundAdmission.getAdmDate().minusDays(1)
		};
		LocalDateTime[] dischargeRange = {
				foundAdmission.getDisDate().minusDays(1),
				foundAdmission.getDisDate().plusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(null, admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
				foundAdmission.getAdmDate().minusDays(1),
				foundAdmission.getAdmDate().plusDays(1)
		};
		LocalDateTime[] dischargeRange = {
				foundAdmission.getDisDate().minusDays(2),
				foundAdmission.getDisDate().minusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients(null, admissionRange, dischargeRange);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testIoGetCurrentAdmission() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionIoOperation.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@Test
	public void testIoGetAdmission() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);

		testAdmission.check(foundAdmission);
	}

	@Test
	public void testIoGetAdmissions() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<Admission> admissions = admissionIoOperation.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testIoNewAdmission() throws Exception {
		Admission admission = buildNewAdmission();
		Admission result = admissionIoOperation.newAdmission(admission);
		assertThat(result);
		admission = admissionBrowserManager.getAdmission(admission.getId());
		testAdmission.check(admission);
	}

	@Test
	public void testIoNewAdmissionReturnKey() throws Exception {
		int id = admissionIoOperation.newAdmissionReturnKey(buildNewAdmission());
		Admission admission = admissionBrowserManager.getAdmission(id);
		testAdmission.check(admission);
	}

	@Test
	public void testIoUpdateAdmission() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setNote("Update");
		Admission result = admissionIoOperation.updateAdmission(foundAdmission);
		Admission updateAdmission = admissionIoOperation.getAdmission(id);

		assertThat(result);
		assertThat(updateAdmission.getNote()).isEqualTo("Update");
	}

	@Test
	public void testIoGetAdmissionType() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionIoOperation.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@Test
	public void testIoGetDischargeType() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@Test
	public void testIoGetNextYProg() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(TimeTools.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
	}

	@Test
	public void testIoSetDeleted() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		boolean result = admissionIoOperation.setDeleted(foundAdmission.getId());
		assertThat(result).isTrue();
	}

	@Test
	public void testIoGetUsedWardBed() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		int result = admissionIoOperation.getUsedWardBed(foundAdmission.getWard().getCode());
		assertThat(result).isEqualTo(1);
	}

	@Test
	@Transactional // requires active session because of lazy loading of patient photo
	public void testIoDeletePatientPhoto() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		boolean result = admissionIoOperation.deletePatientPhoto(foundAdmission.getPatient().getCode());
		assertThat(result).isTrue();
		assertThat(foundAdmission.getPatient().getPatientProfilePhoto().getPhoto()).isNull();
	}

	@Test
	public void testIoLoadAdmittedPatientNotThere() throws Exception {
		assertThat(admissionIoOperation.loadAdmittedPatient(-1)).isNull();
	}

	@Ignore
	@Test
	@Transactional
	public void testIoLoadAdmittedPatient() throws Exception {
		int id = setupTestAdmission(false);
		Admission admission = admissionIoOperation.getAdmission(id);
		assertThat(admission).isNotNull();
		assertThat(admissionIoOperation.loadAdmittedPatient(id)).isNotNull();
	}

	@Test
	public void testIoGetNextYProgMaternityWardBeforeJune() throws Exception {
		// set date before June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = false;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);

		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@Test
	public void testIoGetNextYProgMaternityWardAfterJune() throws Exception {
		// set date after June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = true;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);

		int next = admissionIoOperation.getNextYProg(foundAdmission.getWard().getCode());
		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@Test
	public void testAdmissionGettersSetters() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		foundAdmission.setPatient(null);
		assertThat(foundAdmission.getPatient()).isNull();
		foundAdmission.setPatient(foundPatient);
		assertThat(foundAdmission.getPatient()).isEqualToComparingFieldByField(foundPatient);
		int lock = foundAdmission.getLock();
		foundAdmission.setLock(-1);
		assertThat(foundAdmission.getLock()).isEqualTo(-1);
		foundAdmission.setLock(lock);
		assertThat(foundAdmission.getLock()).isEqualTo(lock);

		Admission admission = buildNewAdmission();
		int admissionId = admission.getId();
		admission.setId(-1);
		assertThat(admission.getId()).isEqualTo(-1);
		admission.setId(admissionId);
		assertThat(admission.getId()).isEqualTo(admissionId);
	}

	@Test
	public void testAdmittedPatientGettersSetters() throws Exception {
		int id = setupTestAdmission(false);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		assertThat(patients).hasSize(1);
		AdmittedPatient admittedPatient = patients.get(0);
		Patient patient = admittedPatient.getPatient();
		admittedPatient.setPatient(null);
		assertThat(admittedPatient.getPatient()).isNull();
		admittedPatient.setPatient(patient);
		assertThat(admittedPatient.getPatient()).isEqualToComparingFieldByField(patient);
		Admission admission = admittedPatient.getAdmission();
		admittedPatient.setAdmission(null);
		assertThat(admittedPatient.getAdmission()).isNull();
		admittedPatient.setAdmission(admission);
		assertThat(admittedPatient.getAdmission()).isEqualToComparingFieldByField(admission);
	}

	@Test
	public void testMgrSimpleGetAdmittedPatients() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		List<AdmittedPatient> patients = admissionIoOperation.getAdmittedPatients();
		List<AdmittedPatient> searchResult = admissionBrowserManager.getAdmittedPatients(null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientWithDateRanges() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients();
		LocalDateTime admissionDate = foundAdmission.getAdmDate();
		LocalDateTime dischargeDate = foundAdmission.getDisDate();
		List<AdmittedPatient> searchResult = admissionBrowserManager.getAdmittedPatients(null, null, null);
		assertThat(searchResult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
		LocalDateTime beforeAdmissionDate = admissionDate.minusDays(1);

		LocalDateTime oneDayAfterAdmissionDate = admissionDate.plusDays(1);

		LocalDateTime twoDaysAfterAdmissionDate = admissionDate.plusDays(2);

		LocalDateTime beforeDischargeDate = dischargeDate.minusDays(1);

		LocalDateTime oneDayAfterDischargeDate = dischargeDate.plusDays(1);

		LocalDateTime twoDaysAfterDischargeDate = dischargeDate.plusDays(2);

		// search by admission date
		List<AdmittedPatient> searchOneresult = admissionBrowserManager.getAdmittedPatients(
				new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate }, null, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		List<AdmittedPatient> searchTwoResult = admissionBrowserManager.getAdmittedPatients(null,
				new LocalDateTime[] { oneDayAfterAdmissionDate, twoDaysAfterAdmissionDate }, null);
		assertThat(searchTwoResult).isEmpty();

		// search by discharge date
		searchOneresult = admissionBrowserManager.getAdmittedPatients(null,
				new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate }, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());

		searchTwoResult = admissionBrowserManager.getAdmittedPatients(
				new LocalDateTime[] { oneDayAfterDischargeDate, twoDaysAfterDischargeDate }, null, null);
		assertThat(searchTwoResult).isEmpty();

		// complex search by both admission and discharge date
		searchOneresult = admissionBrowserManager.getAdmittedPatients(
				new LocalDateTime[] { beforeAdmissionDate, oneDayAfterAdmissionDate },
				new LocalDateTime[] { beforeDischargeDate, oneDayAfterDischargeDate }, null);
		assertThat(searchOneresult).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatients() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients();
		List<AdmittedPatient> patientsNull = admissionBrowserManager.getAdmittedPatients(null);
		assertThat(patientsNull).hasSameSizeAs(patients);
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeFirstName() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getFirstName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeLastName() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getName());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeNote() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getNote());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeTaxCode() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getTaxCode());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldFindByOneOfFieldsLikeId() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(foundPatient.getCode().toString());

		// then:
		assertThat(patients.get(0).getAdmission().getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldNotFindAnythingWhenNotExistingWordProvided() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients("dupsko");

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldNotFindWhenAdmissionOutsideOfDateRange() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
				foundAdmission.getAdmDate().minusDays(2),
				foundAdmission.getAdmDate().minusDays(1)
		};
		LocalDateTime[] dischargeRange = {
				foundAdmission.getDisDate().minusDays(1),
				foundAdmission.getDisDate().plusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(admissionRange, dischargeRange, null);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testMgrGetAdmittedPatientsShouldNotFindWhenDischargeOutsideOfDateRange() throws Exception {
		// given:
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		Patient foundPatient = foundAdmission.getPatient();
		LocalDateTime[] admissionRange = {
				foundAdmission.getAdmDate().minusDays(1),
				foundAdmission.getAdmDate().plusDays(1)
		};
		LocalDateTime[] dischargeRange = {
				foundAdmission.getDisDate().minusDays(2),
				foundAdmission.getDisDate().minusDays(1)
		};

		// when:
		List<AdmittedPatient> patients = admissionBrowserManager.getAdmittedPatients(admissionRange, dischargeRange, null);

		// then:
		assertThat(patients).isEmpty();
	}

	@Test
	public void testMgrLoadAdmittedPatientsNotThere() throws Exception {
		assertThat(admissionBrowserManager.loadAdmittedPatients(-1)).isNull();
	}

	@Test
	public void testMgrGetCurrentAdmission() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setDisDate(null);
		Admission ioAdmission = admissionBrowserManager.getCurrentAdmission(foundAdmission.getPatient());
		assertThat(ioAdmission.getNote()).isEqualTo(foundAdmission.getNote());
	}

	@Test
	public void testMgrGetAdmissions() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<Admission> admissions = admissionBrowserManager.getAdmissions(foundAdmission.getPatient());
		assertThat(admissions.get(admissions.size() - 1).getId()).isEqualTo(foundAdmission.getId());
	}

	@Test
	public void testMgrGetNextYProg() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(TimeTools.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
	}

	@Test
	public void testMgrGetNextYProgMaternityWardBeforeJune() throws Exception {
		// set date before June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = false;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());

		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@Test
	public void testMgrGetNextYProgMaternityWardAfterJune() throws Exception {
		// set date after June
		AdmissionIoOperations.testing = true;
		AdmissionIoOperations.afterJune = true;

		int id = setupTestAdmission(false, true);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		foundAdmission.setYProg(99);
		foundAdmission.setAdmDate(AdmissionIoOperations.getNow());
		admissionIoOperation.updateAdmission(foundAdmission);
		foundAdmission = admissionIoOperation.getAdmission(id);
		int next = admissionBrowserManager.getNextYProg(foundAdmission.getWard().getCode());
		assertThat(next).isEqualTo(foundAdmission.getYProg() + 1);
		// reset
		AdmissionIoOperations.testing = false;
		AdmissionIoOperations.afterJune = false;
	}

	@Test
	public void testMgrGetAdmissionType() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<AdmissionType> admissionTypes = admissionBrowserManager.getAdmissionType();
		assertThat(admissionTypes.get(admissionTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getAdmType().getDescription());
	}

	@Test
	public void testMgrGetDischargeType() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		List<DischargeType> dischargeTypes = admissionBrowserManager.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundAdmission.getDisType().getDescription());
	}

	@Test
	public void testMgrNewAdmission() throws Exception {
		Admission admission = buildNewAdmission();
		Admission result = admissionBrowserManager.newAdmission(admission);
		assertThat(result);
		admission = admissionBrowserManager.getAdmission(admission.getId());
		testAdmission.check(admission);
	}

	@Test
	public void testMgrNewAdmissionReturnKey() throws Exception {
		GeneralData.LANGUAGE = "en";
		int id = admissionBrowserManager.newAdmissionReturnKey(buildNewAdmission());
		Admission admission = admissionBrowserManager.getAdmission(id);
		testAdmission.check(admission);
	}

	@Test
	public void testMgrUpdateAdmission() throws Exception {
		GeneralData.LANGUAGE = "en";
		Admission admission = buildNewAdmission();
		admissionBrowserManager.newAdmission(admission);
		int id = admission.getId();
		admission.setNote("Update");
		Admission result = admissionBrowserManager.updateAdmission(admission);
		assertThat(result);
		Admission updateAdmission = admissionBrowserManager.getAdmission(id);
		assertThat(updateAdmission.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrSetDeleted() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		boolean result = admissionBrowserManager.setDeleted(foundAdmission.getId());
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrGetUsedWardBed() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		int result = admissionBrowserManager.getUsedWardBed(foundAdmission.getWard().getCode());
		assertThat(result).isEqualTo(1);
	}

	@Test
	@Transactional // requires active session because of lazy loading of patient photo
	public void testMgrDeletePatientPhoto() throws Exception {
		int id = setupTestAdmission(false);
		Admission foundAdmission = admissionBrowserManager.getAdmission(id);
		boolean result = admissionBrowserManager.deletePatientPhoto(foundAdmission.getPatient().getCode());
		assertThat(result).isTrue();
		assertThat(foundAdmission.getPatient().getPatientProfilePhoto().getPhoto()).isNull();
	}

	@Test
	public void testMgrValidate() throws Exception {
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Bad progressive id
		admission.setYProg(-1);
		LocalDateTime disDate = admission.getDisDate();
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setYProg(0);
		admission.setDisDate(disDate);

		// Admission date future date
		LocalDateTime admDate = admission.getAdmDate();
		disDate = admission.getDisDate();
		admission.setAdmDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		admission.setAdmDate(admDate);
		admission.setDisDate(disDate);

		// Discharge date is after today
		disDate = admission.getDisDate();
		admission.setDisDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// is null
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setDisDate(disDate);

		// DiseaseOut1() == null && DisDate() != null
		Disease disease = admission.getDiseaseOut1();
		admission.setDiseaseOut1(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class);
		admission.setDiseaseOut1(disease);

		// DiseaseOut1() != null && DisDate() == null
		admDate = admission.getDisDate();
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setDisDate(admDate);
	}
	
	@Test
	public void testMgrValidateMaternity() throws Exception {
		int id = setupTestAdmission(false, true);
		Admission admission = admissionBrowserManager.getAdmission(id);
		GeneralData.LANGUAGE = "en";

		// Bad progressive id
		admission.setYProg(-1);
		LocalDateTime disDate = admission.getDisDate();
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setYProg(0);
		admission.setDisDate(disDate);

		// Admission date future date
		LocalDateTime admDate = admission.getAdmDate();
		disDate = admission.getDisDate();
		admission.setAdmDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

		admission.setAdmDate(admDate);
		admission.setDisDate(disDate);

		// Discharge date is after today
		disDate = admission.getDisDate();
		admission.setDisDate(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// is null
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setDisDate(disDate);

		// DiseaseOut1() == null && DisDate() != null
		Disease disease = admission.getDiseaseOut1();
		admission.setDiseaseOut1(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class);
		admission.setDiseaseOut1(disease);

		// DiseaseOut1() != null && DisDate() == null
		admDate = admission.getDisDate();
		admission.setDisDate(null);
		assertThatThrownBy(() -> admissionBrowserManager.newAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setDisDate(admDate);

		// control dates after discharge dates
		LocalDateTime ctrlDate = admission.getCtrlDate1();
		admission.setCtrlDate1(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class);
		admission.setCtrlDate1(ctrlDate);
		ctrlDate = admission.getCtrlDate2();
		admission.setCtrlDate2(LocalDateTime.of(9999, 1, 1, 0, 0, 0));
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setCtrlDate2(ctrlDate);

		// controlDate2 != null && controlDate1 == null
		admDate = admission.getCtrlDate1();
		admission.setCtrlDate1(null);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setCtrlDate1(admDate);

		// abort date before visit date
		LocalDateTime abortDate = admission.getAbortDate();
		LocalDateTime changeDate = admission.getVisitDate().minusMonths(1);
		admission.setAbortDate(changeDate);
		assertThatThrownBy(() -> admissionBrowserManager.updateAdmission(admission))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		admission.setAbortDate(abortDate);
	}

	@Test
	public void testAdmissionEqualHash() throws Exception {
		int id = setupTestAdmission(false);
		Admission admission = admissionBrowserManager.getAdmission(id);
		Admission admission2 = buildNewAdmission();
		admission2.setId(id);   // no really legal but needed for these tests
		assertThat(admission.equals(admission)).isTrue();
		assertThat(admission)
				.isEqualTo(admission2)
				.isNotEqualTo("xyzzy");

		assertThat(admission.compareTo(admission2)).isZero();
		admission2.setId(9999);
		assertThat(admission.compareTo(admission2)).isEqualTo(admission.getId() - 9999);

		assertThat(admission.hashCode()).isPositive();
	}

	@Test
	public void testIoAdmissionIoOperationRepositoryCustom() throws Exception {
		AdmissionIoOperationRepositoryCustom.PatientAdmission patientAdmission =
				new AdmissionIoOperationRepositoryCustom.PatientAdmission(1, 2);
		assertThat(patientAdmission.getPatientId()).isEqualTo(1);
		assertThat(patientAdmission.getAdmissionId()).isEqualTo(2);
	}

	class MyAdmissionIoOperationRepositoryCustom implements AdmissionIoOperationRepositoryCustom {

		@Override
		public List<AdmittedPatient> findPatientAdmissionsBySearchAndDateRanges(String searchTerms, LocalDateTime[] admissionRange,
				LocalDateTime[] dischargeRange) throws OHServiceException {
			return null;
		}
	}

	private int setupTestAdmission(boolean usingSet) throws OHException, InterruptedException {
		return setupTestAdmission(usingSet, false);
	}

	private int setupTestAdmission(boolean usingSet, boolean maternity) throws OHException, InterruptedException {
		Ward ward = testWard.setup(false, maternity);
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

	private void checkAdmissionIntoDb(int id) throws OHServiceException {
		Admission foundAdmission = admissionIoOperation.getAdmission(id);
		testAdmission.check(foundAdmission);
	}

	// Typically used to build a second admission record thus the need to set new codes because of database key values
	private Admission buildNewAdmission() throws Exception {
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

		ward.setCode("A");
		diseaseIn.setCode("555");
		diseaseOut1.setCode("889");
		diseaseOut2.setCode("778");
		diseaseOut3.setCode("667");
		operation.setCode("9999");
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

		return testAdmission.setup(ward, patient, admissionType, diseaseIn, diseaseOut1,
				diseaseOut2, diseaseOut3, operation, dischargeType, pregTreatmentType,
				deliveryType, deliveryResult, true);
	}

}

