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
package org.isf.visits.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.patient.test.TestPatient;
import org.isf.utils.exception.OHException;
import org.isf.visits.model.Visit;
import org.isf.visits.service.VisitsIoOperationRepository;
import org.isf.visits.service.VisitsIoOperations;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestVisit testVisit;
	private static TestPatient testPatient;
	private static TestWard testWard;

	@Autowired
	VisitsIoOperations visitsIoOperation;
	@Autowired
	VisitsIoOperationRepository visitsIoOperationRepository;
	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testVisit = new TestVisit();
		testPatient = new TestPatient();
		testWard = new TestWard();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testVisitGets() throws Exception {
		int id = _setupTestVisit(false);
		_checkVisitIntoDb(id);
	}

	@Test
	public void testVisitSets() throws Exception {
		int id = _setupTestVisit(true);
		_checkVisitIntoDb(id);
	}

	@Test
	public void testIoGetVisitShouldReturnVisitWhenPatientCodeProvided() throws Exception {
		// given:
		int id = _setupTestVisit(false);
		// when:
		Visit foundVisit = visitsIoOperationRepository.findOne(id);
		ArrayList<Visit> visits = visitsIoOperation.getVisits(foundVisit.getPatient().getCode());
		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isEqualTo(foundVisit.getDate());
	}

	@Test
	public void testIoGetVisitShouldReturnAllVisitsWhenZeroPatientCodeProvided() throws Exception {
		// given:
		int id = _setupTestVisit(false);
		Visit foundVisit = visitsIoOperationRepository.findOne(id);

		// when:
		ArrayList<Visit> visits = visitsIoOperation.getVisits(0);

		// then:
		assertThat(visits.get(visits.size() - 1).getDate()).isEqualTo(foundVisit.getDate());
	}

	@Test
	public void testIoNewVisit() throws Exception {
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		Visit visit = testVisit.setup(patient, true, ward);
		int id = visitsIoOperation.newVisit(visit).getVisitID();
		_checkVisitIntoDb(id);
	}

	@Test
	public void testIoDeleteVisit() throws Exception {
		int id = _setupTestVisit(false);
		Visit foundVisit = visitsIoOperation.findVisit(id);
		boolean result = visitsIoOperation.deleteAllVisits(foundVisit.getPatient().getCode());

		assertThat(result).isTrue();
		result = visitsIoOperation.isCodePresent(id);
		assertThat(result).isFalse();
	}

	@Test
	public void testFindVisit() throws Exception {
		int id = _setupTestVisit(false);
		Visit result = visitsIoOperation.findVisit(id);

		assertThat(result).isNotNull();
		assertThat(result.getVisitID()).isEqualTo(id);
	}

	private int _setupTestVisit(boolean usingSet) throws OHException {
		Visit visit;
		Patient patient = testPatient.setup(false);
		Ward ward = testWard.setup(false);
		patientIoOperationRepository.saveAndFlush(patient);
		wardIoOperationRepository.saveAndFlush(ward);
		visit = testVisit.setup(patient, usingSet, ward);
		visitsIoOperationRepository.saveAndFlush(visit);
		return visit.getVisitID();
	}

	private void _checkVisitIntoDb(int id) throws OHException {
		Visit foundVisit = visitsIoOperation.findVisit(id);
		testVisit.check(foundVisit);
	}
}