/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.opd.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.generaldata.GeneralData;
import org.isf.opd.manager.OpdBrowserManager;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.service.OpdIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@RunWith(Parameterized.class)
public class Tests extends OHCoreTestCase {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	private static TestOpd testOpd;
	private static TestPatient testPatient;
	private static TestDiseaseType testDiseaseType;
	private static TestDisease testDisease;

	@Autowired
	OpdIoOperations opdIoOperation;
	@Autowired
	OpdIoOperationRepository opdIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	OpdBrowserManager opdBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	public Tests(boolean opdExtended) {
		GeneralData.OPDEXTENDED = opdExtended;
	}

	@BeforeClass
	public static void setUpClass() {
		testOpd = new TestOpd();
		testPatient = new TestPatient();
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Parameterized.Parameters(name = "Test with OPDEXTENDED={0}")
	public static Collection<Object[]> opdExtended() {
		return Arrays.asList(new Object[][] {
				{ false },
				{ true }
		});
	}
	
	@Test
	public void testOpdGets() throws Exception {
		int code = _setupTestOpd(false);
		_checkOpdIntoDb(code);
	}

	@Test
	public void testOpdSets() throws Exception {
		int code = _setupTestOpd(true);
		_checkOpdIntoDb(code);
	}

	@Test
	public void testIoGetOpdList() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		List<Opd> opds = opdIoOperation.getOpdList(
				foundOpd.getDisease().getType().getCode(),
				foundOpd.getDisease().getCode(),
				foundOpd.getDate(),
				foundOpd.getDate(),
				foundOpd.getAge() - 1,
				foundOpd.getAge() + 1,
				foundOpd.getSex(),
				foundOpd.getNewPatient());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testIoGetOpdListPatientId() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		List<Opd> opds = opdIoOperation.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testIoGetOpdListPatientIdZero() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar now = new GregorianCalendar();
		opd.setVisitDate(now);
		opd.setDate(now);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		List<Opd> opds = opdIoOperation.getOpdList(0);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoGetOpdListToday() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		// set date to be today
		GregorianCalendar today = new GregorianCalendar();
		opd.setVisitDate(today);
		opd.setDate(today);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);
		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Opd opd2 = testOpd.setup(patient2, disease2, true);
		GregorianCalendar now = new GregorianCalendar();
		// set date to be 14 days ago (not within the TODAY test)
		now.add(Calendar.DAY_OF_MONTH, -14);
		opd2.setDate(now);

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdIoOperation.getOpdList(false);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoGetOpdListLastWeek() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar date = new GregorianCalendar();
		// set date to be 3 days ago (within last week)
		date.add(Calendar.DAY_OF_MONTH, -3);
		opd.setVisitDate(date);
		opd.setDate(date);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Opd opd2 = testOpd.setup(patient2, disease2, true);
		GregorianCalendar date2 = new GregorianCalendar();
		// set date to be 13 days aga (not within last week)
		date2.add(Calendar.DAY_OF_MONTH, -13);
		opd2.setVisitDate(date2);
		opd2.setDate(date2);

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdIoOperation.getOpdList(true);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoNewOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		Opd opd = testOpd.setup(patient, disease, false);
		boolean result = opdIoOperation.newOpd(opd);
		assertThat(result).isTrue();
		_checkOpdIntoDb(opd.getCode());
	}

	@Test
	public void testIoUpdateOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		foundOpd.setNote("Update");
		Opd result = opdIoOperation.updateOpd(foundOpd);
		Opd updateOpd = opdIoOperationRepository.findOne(code);
		assertThat(result).isNotNull();
		assertThat(updateOpd.getNote()).isEqualTo("Update");
	}

	@Test
	public void testIoDeleteOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		boolean result = opdIoOperation.deleteOpd(foundOpd);
		assertThat(result).isTrue();
		result = opdIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoGetProgYearZero() throws Exception {
		int code = _setupTestOpd(false);
		int progYear = opdIoOperation.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@Test
	public void testIoGetProgYear() throws Exception {
		int code = _setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findOne(code);
		int progYear = opdIoOperation.getProgYear(opd.getDate().get(Calendar.YEAR));
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() throws Exception {
		// given:
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), foundOpd.getDate().get(Calendar.YEAR));

		// then:
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided() throws Exception {
		// given:
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 0);

		// then:
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect() throws Exception {
		// given:
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), 1488);

		// then:
		assertThat(result).isFalse();
	}

	@Test
	public void testIoGetLastOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		Opd lastOpd = opdIoOperation.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testListenerShouldUpdatePatientToMergedWhenPatientMergedEventArrive() throws Exception {
		// given:
		int id = _setupTestOpd(false);
		Opd found = opdIoOperationRepository.findOne(id);
		Patient mergedPatient = _setupTestPatient(false);

		// when:
		applicationEventPublisher.publishEvent(new PatientMergedEvent(found.getPatient(), mergedPatient));

		// then:
		Opd result = opdIoOperationRepository.findOne(id);
		assertThat(result.getPatient().getCode()).isEqualTo(mergedPatient.getCode());
	}

	@Test
	public void testMgrGetOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		List<Opd> opds = opdBrowserManager.getOpd(
				foundOpd.getDisease().getType().getCode(),
				foundOpd.getDisease().getCode(),
				foundOpd.getDate(),
				foundOpd.getDate(),
				foundOpd.getAge() - 1,
				foundOpd.getAge() + 1,
				foundOpd.getSex(),
				foundOpd.getNewPatient());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrGetOpdListPatientId() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		List<Opd> opds = opdBrowserManager.getOpdList(foundOpd.getPatient().getCode());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrGetOpdListPatientIdZero() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar now = new GregorianCalendar();
		opd.setVisitDate(now);
		opd.setDate(now);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		List<Opd> opds = opdBrowserManager.getOpdList(0);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrGetOpdToday() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		// set date to be today
		GregorianCalendar today = new GregorianCalendar();
		opd.setVisitDate(today);
		opd.setDate(today);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Opd opd2 = testOpd.setup(patient2, disease2, true);
		GregorianCalendar now = new GregorianCalendar();
		// set date to be 14 days ago (not within the TODAY test)
		now.add(Calendar.DAY_OF_MONTH, -14);
		opd2.setVisitDate(now);
		opd2.setDate(now);

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdBrowserManager.getOpd(false);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrGetOpdLastWeek() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldiseases.txt");
		disease.getType().setCode("angal.common.alltypes.txt");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar date = new GregorianCalendar();
		// set date to be 3 days ago (within last week)
		date.add(Calendar.DAY_OF_MONTH, -3);
		opd.setVisitDate(date);
		opd.setDate(date);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		Patient patient2 = testPatient.setup(false);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);

		Disease disease2 = testDisease.setup(diseaseType2, false);

		Opd opd2 = testOpd.setup(patient2, disease2, true);
		GregorianCalendar date2 = new GregorianCalendar();
		// set date to be 13 days ago (not within last week)
		date2.add(Calendar.DAY_OF_MONTH, -13);
		opd2.setVisitDate(date2);
		opd2.setDate(date2);

		patientIoOperationRepository.saveAndFlush(patient2);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType2);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		opdIoOperationRepository.saveAndFlush(opd2);

		List<Opd> opds = opdBrowserManager.getOpd(true);
		assertThat(opds).hasSize(1);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testMgrNewOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		Opd opd = testOpd.setup(patient, disease, false);
		// Need this to pass validation checks in manager
		Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		Disease disease3 = new Disease("997", "TestDescription 3", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease3);
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		assertThat(opdBrowserManager.newOpd(opd)).isTrue();
		_checkOpdIntoDb(opd.getCode());
	}

	@Test
	public void testMgrUpdateOpd() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		Opd opd = testOpd.setup(patient, disease, false);
		// Need this to pass validation checks in manager
		Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease2);
		Disease disease3 = new Disease("997", "TestDescription 3", diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease3);
		opd.setDisease2(disease2);
		opd.setDisease3(disease3);
		opd.setDate(new GregorianCalendar());
		assertThat(opdBrowserManager.newOpd(opd)).isTrue();
		opd.setNote("Update");
		assertThat(opdBrowserManager.updateOpd(opd)).isNotNull();
		Opd updateOpd = opdIoOperationRepository.findOne(opd.getCode());
		assertThat(updateOpd.getNote()).isEqualTo("Update");
	}

	@Test
	public void testMgrDeleteOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		boolean result = opdBrowserManager.deleteOpd(foundOpd);
		assertThat(result).isTrue();
		result = opdIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetProgYearZero() throws Exception {
		int code = _setupTestOpd(false);
		int progYear = opdBrowserManager.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@Test
	public void testMgrGetProgYear() throws Exception {
		int code = _setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findOne(code);
		int progYear = opdBrowserManager.getProgYear(opd.getDate().get(Calendar.YEAR));
		assertThat(progYear).isEqualTo(opd.getProgYear());
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() throws Exception {
		int code = _setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findOne(code);
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), opd.getDate().get(Calendar.YEAR))).isTrue();
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnTrueWhenOpdNumExistsAndVisitYearIsNotProvided() throws Exception {
		int code = _setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findOne(code);
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 0)).isTrue();
	}

	@Test
	public void testMgrIsExistsOpdNumShouldReturnFalseWhenOpdNumExistsAndVisitYearIsIncorrect() throws Exception {
		int code = _setupTestOpd(false);
		Opd opd = opdIoOperationRepository.findOne(code);
		assertThat(opdBrowserManager.isExistOpdNum(opd.getProgYear(), 1488)).isFalse();
	}

	@Test
	public void testMgrGetLastOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		Opd lastOpd = opdBrowserManager.getLastOpd(foundOpd.getPatient().getCode());
		assertThat(lastOpd.getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testMgrValidationVisitDateNull() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			// also let validation set userID
			opd.setUserID(null);

			opd.setVisitDate(null);
			opd.setDate(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationOPDEXTENDEDPatientNull() throws Exception {
		if (!GeneralData.OPDEXTENDED) {
			return;
		}
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setPatient(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationAgeIsLessThanZero() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setAge(-99);
			opdBrowserManager.updateOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationSexIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			patient.setSex(' ');
			opd.setSex(' ');
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);
			// Need this to pass validation checks in manager
			opd.setDisease2(null);
			opd.setDisease3(null);

			opd.setDisease(null);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEqualToDisease2() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);

			Disease disease2 = new Disease("997", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDiseaseIsEqualToDisease3() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);

			Disease disease3 = new Disease("998", "TestDescription 3", diseaseType);
			opd.setDisease3(disease3);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDisease2IsEqualToDisease3() throws Exception {
		assertThatThrownBy(() ->
		{
			Patient patient = testPatient.setup(false);
			DiseaseType diseaseType = testDiseaseType.setup(false);
			Disease disease = testDisease.setup(diseaseType, false);
			Opd opd = testOpd.setup(patient, disease, false);

			Disease disease2 = new Disease("998", "TestDescription 2", diseaseType);
			opd.setDisease2(disease2);
			opd.setDisease3(disease2);
			opdBrowserManager.newOpd(opd);
		})
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testOpdGetSet() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Opd opd = testOpd.setup(patient, disease, false);

		Disease disease2 = new Disease("997", "TestDescription 2", diseaseType);
		opd.setDisease2(disease2);

		Disease disease3 = new Disease("998", "TestDescription 3", diseaseType);
		opd.setDisease3(disease3);

		opd.getPatient().setFirstName("John");
		opd.getPatient().setSecondName("Smith");
		assertThat(opd.getFullName()).isEqualTo("John Smith");

		assertThat(opd.getfirstName()).isEqualTo("John");
		assertThat(opd.getsecondName()).isEqualTo("Smith");

		opd.getPatient().setFirstName(null);
		opd.getPatient().setSecondName(null);

		assertThat(opd.getFullName()).isEqualTo("null null");
		assertThat(opd.getfirstName()).isNull();
		assertThat(opd.getsecondName()).isNull();

		opd.getPatient().setNextKin("nextOfKin");
		opd.getPatient().setCity("city");
		opd.getPatient().setAddress("address");

		assertThat(opd.getnextKin()).isEqualTo("nextOfKin");
		assertThat(opd.getcity()).isEqualTo("city");
		assertThat(opd.getaddress()).isEqualTo("address");

		opd.getPatient().setNextKin(null);
		opd.getPatient().setCity(null);
		opd.getPatient().setAddress(null);

		assertThat(opd.getnextKin()).isNull();
		assertThat(opd.getcity()).isNull();
		assertThat(opd.getaddress()).isNull();

		opd.setPatient(null);

		assertThat(opd.getFullName()).isEmpty();
		assertThat(opd.getfirstName()).isEmpty();
		assertThat(opd.getsecondName()).isEmpty();
		assertThat(opd.getnextKin()).isEmpty();
		assertThat(opd.getcity()).isEmpty();
		assertThat(opd.getaddress()).isEmpty();

		opd.setCode(32000);
		assertThat(opd.getCode()).isEqualTo(32000);

		assertThat(opd.getDisease2()).isEqualTo(disease2);
		assertThat(opd.getDisease3()).isEqualTo(disease3);

		opd.setLock(-1);
		assertThat(opd.getLock()).isEqualTo(-1);

		opd.setNextVisitDate(new GregorianCalendar(9999, 0, 1));
		assertThat(opd.getNextVisitDate()).isEqualTo(new GregorianCalendar(9999, 0, 1));
	}

	@Test
	public void testOpdHashCode() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Opd opd = testOpd.setup(patient, disease, false);
		// compute
		int hashCode = opd.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + opd.getCode());
		// used computed value
		assertThat(opd.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testOpdEquals() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);
		Opd opd = testOpd.setup(patient, disease, false);

		assertThat(opd.equals(opd)).isTrue();
		assertThat(opd)
				.isNotNull()
				.isNotEqualTo("someString");
	}

	private Patient _setupTestPatient(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		return patient;
	}

	private int _setupTestOpd(boolean usingSet) throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, false);

		Opd opd = testOpd.setup(patient, disease, usingSet);
		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);
		return opd.getCode();
	}

	private void _checkOpdIntoDb(int code) throws OHException {
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		testOpd.check(foundOpd);
	}
}