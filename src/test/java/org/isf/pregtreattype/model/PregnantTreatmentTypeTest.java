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
package org.isf.pregtreattype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PregnantTreatmentTypeTest extends OHCoreTestCase {

	private static TestPregnantTreatmentType testPregnantTreatmentType;

	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPregnantTreatmentType = new TestPregnantTreatmentType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestPregnantTreatmentType(true);
		checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	void equals_shouldCompareCodeAndDescriptionIgnoringCase() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");

		assertThat(pregnantTreatmentType)
				.isEqualTo(pregnantTreatmentType)
				.isNotNull()
				.isNotEqualTo("someString");

		PregnantTreatmentType pregnantTreatmentType2 = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("XX");
		pregnantTreatmentType.setDescription("someDescription");

		assertThat(pregnantTreatmentType).isNotEqualTo(pregnantTreatmentType2);
		pregnantTreatmentType2.setCode(pregnantTreatmentType.getCode());
		pregnantTreatmentType2.setDescription("someOtherDescription");
		assertThat(pregnantTreatmentType).isNotEqualTo(pregnantTreatmentType2);

		pregnantTreatmentType2.setDescription(pregnantTreatmentType.getDescription().toUpperCase());
		assertThat(pregnantTreatmentType).isEqualTo(pregnantTreatmentType2);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");
		assertThat(pregnantTreatmentType).hasToString("someDescription");
	}

	@Test
	void hashCode_shouldReturnSameValue_whenCalledTwice() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");

		int hashCode = pregnantTreatmentType.hashCode();
		// use computed value
		assertThat(pregnantTreatmentType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestPregnantTreatmentType(boolean usingSet) throws OHException {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setDescription("Test Description");
		pregnantTreatmentType = testPregnantTreatmentType.setup(usingSet);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType);
		return pregnantTreatmentType.getCode();
	}

	private void checkPregnantTreatmentTypeIntoDb(String code) {
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		testPregnantTreatmentType.check(foundPregnantTreatmentType);
	}

}
