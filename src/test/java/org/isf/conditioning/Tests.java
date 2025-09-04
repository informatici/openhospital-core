/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.conditioning;

import org.apache.logging.log4j.core.util.Assert;
import org.isf.OHCoreTestCase;
import org.isf.conditioning.manager.ConditioningBrowserManager;
import org.isf.conditioning.model.Conditioning;
import org.isf.menu.service.UserGroupIoOperationRepository;
import org.isf.menu.service.UserIoOperationRepository;
import org.isf.patient.TestPatient;
import org.isf.patient.model.Patient;
import org.isf.patient.service.PatientIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Tests extends OHCoreTestCase {

	private static TestConditioning testConditioning;

	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	UserGroupIoOperationRepository userGroupIoOperationRepository;

	@Autowired
	UserIoOperationRepository userIoOperationRepository;

	@Autowired
	ConditioningBrowserManager conditioningBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testConditioning = new TestConditioning();
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void saveConditioning_shouldSaveAndReturnConditioning() throws Exception {
		Conditioning conditioning = setupConditioning(false);
		Conditioning saved = conditioningBrowserManager.newConditioning(conditioning);
		assertThat(saved).isNotNull();
		assertThat(saved.getPatient().getCode()).isEqualTo(conditioning.getPatient().getCode());
	}

	@Test
	void getConditioningByPatient_shouldReturnList() throws Exception {
		Conditioning first = setupConditioning(false);
		Conditioning saved = conditioningBrowserManager.newConditioning(first);
		Patient patient = saved.getPatient();
		Conditioning second = testConditioning.setup(patient, true);
		conditioningBrowserManager.newConditioning(second);
		List<Conditioning> list = conditioningBrowserManager.getConditioningByPatientCode(patient.getCode());
		assertThat(list).isNotNull();
		assertThat(2).isEqualTo(list.size());
	}

	@Test
	void getConditioningById_shouldReturnConditioning() throws Exception {
		Conditioning conditioning = setupConditioning(false);
		Conditioning saved = conditioningBrowserManager.newConditioning(conditioning);
		Conditioning found = conditioningBrowserManager.getConditioningById(saved.getId());
		assertThat(found).isNotNull();
		assertThat(saved.getId()).isEqualTo(found.getId());
	}

	@Test
	void updateConditioning_shouldUpdateFields() throws Exception {
		Conditioning conditioning = setupConditioning(false);
		Conditioning saved = conditioningBrowserManager.newConditioning(conditioning);
		saved.setMce(99);
		saved.setSngNumber("SN-02");
		Conditioning updated = conditioningBrowserManager.updateConditioning(saved);
		assertThat(99).isEqualTo(updated.getMce());
		assertThat("SN-02").isEqualTo(updated.getSngNumber());
	}

	private Conditioning setupConditioning(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		Patient patientSaved = patientIoOperationRepository.saveAndFlush(patient);
		Conditioning conditioning = testConditioning.setup(patientSaved, usingSet);
		Assert.isNonEmpty(conditioning);
		return conditioning;
	}

}
