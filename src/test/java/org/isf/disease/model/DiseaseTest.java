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
package org.isf.disease.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.disease.TestDisease;
import org.isf.disease.manager.DiseaseBrowserManager;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DiseaseTest extends OHCoreTestCase {

	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	DiseaseBrowserManager diseaseBrowserManager;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestDisease(false);
		checkDiseaseIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestDisease(true);
		checkDiseaseIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseDiseaseFields() throws Exception {
		String code = setupTestDisease(false);
		Disease disease = diseaseBrowserManager.getDiseaseByCode(code);
		DiseaseType diseaseType2 = testDiseaseType.setup(false);
		Disease disease2 = new Disease("998", "someDescription", diseaseType2);
		assertThat(disease)
				.isEqualTo(disease)
				.isNotEqualTo(disease2)
				.isNotEqualTo("xyzzy");
		disease2.setCode(disease.getCode());
		disease2.setType(disease.getType());
		disease2.setDescription(disease.getDescription());
		assertThat(disease).isEqualTo(disease2);

		assertThat(disease.hashCode()).isPositive();

		assertThat(disease2).hasToString(disease.getDescription());
	}

	@Test
	void setLock_shouldUpdateLock() throws Exception {
		String code = setupTestDisease(false);
		Disease disease = diseaseBrowserManager.getDiseaseByCode(code);
		disease.setLock(-99);
		assertThat(disease.getLock()).isEqualTo(-99);
	}

	private String setupTestDisease(boolean usingSet) throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		return disease.getCode();
	}

	private void checkDiseaseIntoDb(String code) throws Exception {
		Disease foundDisease = diseaseIoOperationRepository.getReferenceById(code);
		testDisease.check(foundDisease);
	}

}
