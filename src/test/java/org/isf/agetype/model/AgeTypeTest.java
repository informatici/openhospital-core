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
package org.isf.agetype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.agetype.TestAgeType;
import org.isf.agetype.service.AgeTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AgeTypeTest extends OHCoreTestCase {

	private static TestAgeType testAgeType;

	@Autowired
	AgeTypeIoOperationRepository ageTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testAgeType = new TestAgeType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestAgeType(false);
		checkAgeTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestAgeType(true);
		checkAgeTypeIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseAgeTypeFields() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType ageType2 = new AgeType(ageType.getCode(), ageType.getDescription());
		ageType2.setFrom(ageType.getFrom());
		ageType2.setTo(ageType.getTo());
		assertThat(ageType)
				.isEqualTo(ageType)
				.isEqualTo(ageType2)
				.isNotEqualTo("xyzzy");
		ageType2.setCode("xxxx");
		assertThat(ageType).isNotEqualTo(ageType2);

		assertThat(ageType.hashCode()).isPositive();

		assertThat(ageType2).hasToString(ageType.getDescription());
	}

	private String setupTestAgeType(boolean usingSet) throws Exception {
		AgeType ageType = testAgeType.setup(usingSet);
		ageTypeIoOperationRepository.saveAndFlush(ageType);
		return ageType.getCode();
	}

	private void checkAgeTypeIntoDb(String code) throws Exception {
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		testAgeType.check(foundAgeType);
	}

}
