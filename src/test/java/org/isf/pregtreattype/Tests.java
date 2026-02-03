/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.pregtreattype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestPregnantTreatmentType testPregnantTreatmentType;

	@Autowired
	PregnantTreatmentTypeIoOperation pregnantTreatmentTypeIoOperation;
	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;
	@Autowired
	PregnantTreatmentTypeBrowserManager pregnantTreatmentTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testPregnantTreatmentType = new TestPregnantTreatmentType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testPregnantTreatmentTypeGets() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	void testPregnantTreatmentTypeSets() throws Exception {
		String code = setupTestPregnantTreatmentType(true);
		checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	void testIoGetPregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		List<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeIoOperation.getPregnantTreatmentType();

		for (PregnantTreatmentType pregnantTreatmentType : pregnantTreatmentTypes) {
			if (pregnantTreatmentType.getCode().equals(code)) {
				assertThat(pregnantTreatmentType.getDescription()).isEqualTo(foundPregnantTreatmentType.getDescription());
			}
		}
	}

	@Test
	void testIoUpdatePregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		foundPregnantTreatmentType.setDescription("Update");
		PregnantTreatmentType updatedPregnantTreatmentType = pregnantTreatmentTypeIoOperation.updatePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(updatedPregnantTreatmentType).isNotNull();
		assertThat(updatedPregnantTreatmentType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewPregnantTreatmentType() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
		PregnantTreatmentType newPregnantTreatmentType = pregnantTreatmentTypeIoOperation.newPregnantTreatmentType(pregnantTreatmentType);
		checkPregnantTreatmentTypeIntoDb(newPregnantTreatmentType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		boolean result = pregnantTreatmentTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeletePregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		pregnantTreatmentTypeIoOperation.deletePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(pregnantTreatmentTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetPregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		PregnantTreatmentType pregnantTreatmentType2 = new PregnantTreatmentType("AA", "AA description");
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType2);
		PregnantTreatmentType pregnantTreatmentType3 = new PregnantTreatmentType("BB", "BB description");
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType3);

		List<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeBrowserManager.getPregnantTreatmentType();
		assertThat(pregnantTreatmentTypes).hasSize(3);

		for (PregnantTreatmentType pregnantTreatmentType : pregnantTreatmentTypes) {
			if (pregnantTreatmentType.getCode().equals(code)) {
				assertThat(pregnantTreatmentType.getDescription()).isEqualTo(foundPregnantTreatmentType.getDescription());
			} else {
				assertThat(pregnantTreatmentType.getDescription()).isNotEqualTo(foundPregnantTreatmentType.getDescription());
			}
		}
	}

	@Test
	void testMgrUpdatePregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		foundPregnantTreatmentType.setDescription("Update");
		PregnantTreatmentType updatedPregnantTreatmentType = pregnantTreatmentTypeBrowserManager.updatePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(updatedPregnantTreatmentType).isNotNull();
		assertThat(updatedPregnantTreatmentType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewPregnantTreatmentType() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
		PregnantTreatmentType newPregnantTreatmentType = pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		checkPregnantTreatmentTypeIntoDb(newPregnantTreatmentType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		assertThat(pregnantTreatmentTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrDeletePregnantTreatmentType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		pregnantTreatmentTypeBrowserManager.deletePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(pregnantTreatmentTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidationCodeTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("thisIsAVeryLongKey");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidationCodeAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			setupTestPregnantTreatmentType(true);
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("ZZ");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testMgrValidationDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("ZZ");
			pregnantTreatmentType.setDescription("");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	void testPregnantTreatmentTypeEquals() throws Exception {
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
	void testPregnantTreatmentTypeToString() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");
		assertThat(pregnantTreatmentType).hasToString("someDescription");
	}

	@Test
	void testPregnantTreatmentTypeHashCode() throws Exception {
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