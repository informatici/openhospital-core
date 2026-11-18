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
package org.isf.distype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.distype.TestDiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class DiseaseTypeTest extends OHCoreTestCase {

	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDiseaseType = new TestDiseaseType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestDiseaseType(false);
		checkDiseaseTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestDiseaseType(true);
		checkDiseaseTypeIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseDiseaseTypeFields() throws Exception {
		String code = setupTestDiseaseType(false);
		DiseaseType diseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		DiseaseType diseaseType2 = new DiseaseType("code", "description");
		assertThat(diseaseType)
				.isEqualTo(diseaseType)
				.isNotEqualTo(diseaseType2)
				.isNotEqualTo("xyzzy");
		diseaseType2.setCode(diseaseType.getCode());
		diseaseType2.setDescription(diseaseType.getDescription());
		assertThat(diseaseType).isEqualTo(diseaseType2);

		assertThat(diseaseType.hashCode()).isPositive();

		assertThat(diseaseType).hasToString(diseaseType.getDescription());
	}

	private String setupTestDiseaseType(boolean usingSet) throws OHException {
		DiseaseType diseaseType = testDiseaseType.setup(usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		return diseaseType.getCode();
	}

	private void checkDiseaseTypeIntoDb(String code) throws OHException {
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getReferenceById(code);
		testDiseaseType.check(foundDiseaseType);
	}

}
