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
package org.isf.opd.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.isf.OHCoreTestCase;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.test.TestDisease;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.isf.generaldata.GeneralData;
import org.isf.opd.model.Opd;
import org.isf.opd.service.OpdIoOperationRepository;
import org.isf.opd.service.OpdIoOperations;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientMergedEvent;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;
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

	@Test
	public void testOpdGets() throws Exception {
		int code = _setupTestOpd(false);
		_checkOpdIntoDb(code);
	}

	@Parameterized.Parameters(name = "Test with OPDEXTENDED={0}")
	public static Collection<Object[]> opdExtended() {
		return Arrays.asList(new Object[][] {
				{ false },
				{ true }
		});
	}

	@Test
	public void testOpdSets() throws Exception {
		int code = _setupTestOpd(true);
		_checkOpdIntoDb(code);
	}

	@Test
	public void testIoGetOpd() throws Exception {
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		ArrayList<Opd> opds = opdIoOperation.getOpdList(
				foundOpd.getDisease().getType().getCode(),
				foundOpd.getDisease().getCode(),
				foundOpd.getVisitDate(),
				foundOpd.getVisitDate(),
				foundOpd.getAge() - 1,
				foundOpd.getAge() + 1,
				foundOpd.getSex(),
				foundOpd.getNewPatient());
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(foundOpd.getCode());
	}

	@Test
	public void testIoGetOpdListToday() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldisease");
		disease.getType().setCode("angal.opd.alltype");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar now = new GregorianCalendar();
		opd.setVisitDate(now);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		ArrayList<Opd> opds = opdIoOperation.getOpdList(false);
		assertThat(opds.get(opds.size() - 1).getCode()).isEqualTo(opd.getCode());
	}

	@Test
	public void testIoGetOpdListLastWeek() throws Exception {
		Patient patient = testPatient.setup(false);
		DiseaseType diseaseType = testDiseaseType.setup(false);

		Disease disease = testDisease.setup(diseaseType, false);
		disease.setCode("angal.opd.alldisease");
		disease.getType().setCode("angal.opd.alltype");

		Opd opd = testOpd.setup(patient, disease, true);
		GregorianCalendar date = new GregorianCalendar();
		date.add(Calendar.DAY_OF_MONTH, -3);
		opd.setVisitDate(date);

		patientIoOperationRepository.saveAndFlush(patient);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		opdIoOperationRepository.saveAndFlush(opd);

		ArrayList<Opd> opds = opdIoOperation.getOpdList(true);
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
		opd.setDate(new Date());
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
	public void testIoGetProgYear() throws Exception {
		int code = _setupTestOpd(false);
		int progYear = opdIoOperation.getProgYear(0);
		Opd foundOpd = opdIoOperationRepository.findOne(code);
		assertThat(progYear).isEqualTo(foundOpd.getProgYear());
	}

	@Test
	public void testIoIsExistsOpdNumShouldReturnTrueWhenOpdWithGivenOPDProgressiveYearAndVisitYearExists() throws Exception {
		// given:
		int code = _setupTestOpd(false);
		Opd foundOpd = opdIoOperationRepository.findOne(code);

		// when:
		Boolean result = opdIoOperation.isExistOpdNum(foundOpd.getProgYear(), foundOpd.getVisitDate().get(Calendar.YEAR));

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
		opd.setDate(new Date());
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