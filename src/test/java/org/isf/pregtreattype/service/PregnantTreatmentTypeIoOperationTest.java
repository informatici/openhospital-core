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
package org.isf.pregtreattype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.pregtreattype.TestPregnantTreatmentType;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PregnantTreatmentTypeIoOperationTest extends OHCoreTestCase {

	private static TestPregnantTreatmentType testPregnantTreatmentType;

	@Autowired
	PregnantTreatmentTypeIoOperation pregnantTreatmentTypeIoOperation;
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
	void getPregnantTreatmentType_shouldReturnStoredDescription() throws Exception {
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
	void updatePregnantTreatmentType_shouldSaveNewDescription() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		foundPregnantTreatmentType.setDescription("Update");
		PregnantTreatmentType updatedPregnantTreatmentType = pregnantTreatmentTypeIoOperation.updatePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(updatedPregnantTreatmentType).isNotNull();
		assertThat(updatedPregnantTreatmentType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newPregnantTreatmentType_shouldSaveType() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
		PregnantTreatmentType newPregnantTreatmentType = pregnantTreatmentTypeIoOperation.newPregnantTreatmentType(pregnantTreatmentType);
		checkPregnantTreatmentTypeIntoDb(newPregnantTreatmentType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		boolean result = pregnantTreatmentTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deletePregnantTreatmentType_shouldRemoveType() throws Exception {
		String code = setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundPregnantTreatmentType).isNotNull();
		pregnantTreatmentTypeIoOperation.deletePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(pregnantTreatmentTypeIoOperation.isCodePresent(code)).isFalse();
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
