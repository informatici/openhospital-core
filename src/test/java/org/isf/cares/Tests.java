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
package org.isf.cares;

import org.apache.logging.log4j.core.util.Assert;
import org.isf.OHCoreTestCase;
import org.isf.cares.manager.CareManager;
import org.isf.cares.model.Care;
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

	private static TestCare testCare;

	private static TestPatient testPatient;

	@Autowired
	PatientIoOperationRepository patientIoOperationRepository;

	@Autowired
	CareManager careManager;


	@BeforeAll
	static void setUpClass() {
		testCare = new TestCare();
		testPatient = new TestPatient();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void saveCare_shouldSaveAndReturnCare() throws Exception {
		Care care = setupCare(true);
		Care saved = careManager.saveCare(care);
		assertThat(saved).isNotNull();
		assertThat(saved.getPatient().getCode()).isEqualTo(care.getPatient().getCode());
	}

	@Test
	void getCareByPatient_shouldReturnList() throws Exception {
		Care first = setupCare(true);
		Care saved = careManager.saveCare(first);
		Patient patient = saved.getPatient();
		Care second = testCare.setup(patient, true);
		careManager.saveCare(second);
		List<Care> list = careManager.getCaresByPatient(patient.getCode());
		assertThat(list).isNotNull();
		assertThat(2).isEqualTo(list.size());
	}


	@Test
	void getCareById_shouldReturnCare() throws Exception {
		Care care = setupCare(true);
		Care saved = careManager.saveCare(care);
		Care found = careManager.getCareById(saved.getId());
		assertThat(found).isNotNull();
		assertThat(saved.getId()).isEqualTo(found.getId());
	}

	@Test
	void updateCare_shouldUpdateFields() throws Exception {
		Care care = setupCare(true);
		Care saved = careManager.saveCare(care);
		saved.setNote("Treatment don't worked");
		Care updated = careManager.updateCare(saved);
		assertThat("Treatment don't worked").isEqualTo(updated.getNote());
	}

	private Care setupCare(boolean usingSet) throws OHException {
		Patient patient = testPatient.setup(false);
		Patient savedPatient = patientIoOperationRepository.saveAndFlush(patient);

		Care care = testCare.setup(savedPatient, usingSet);
		Assert.isNonEmpty(care);
		return care;
	}
}
