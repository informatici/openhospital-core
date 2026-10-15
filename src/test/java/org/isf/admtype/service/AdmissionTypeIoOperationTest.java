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
package org.isf.admtype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.model.AdmissionType;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdmissionTypeIoOperationTest extends OHCoreTestCase {

	private static TestAdmissionType testAdmissionType;

	@Autowired
	private AdmissionTypeIoOperation admissionTypeIoOperation;
	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testAdmissionType = new TestAdmissionType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getAdmissionType_shouldReturnSavedAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		List<AdmissionType> admissionTypes = admissionTypeIoOperation.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void updateAdmissionType_shouldSaveNewDescription() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();
		admissionType.setDescription("Update");
		AdmissionType updatedAdmissionType = admissionTypeIoOperation.updateAdmissionType(admissionType);
		assertThat(updatedAdmissionType).isNotNull();
		assertThat(updatedAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	void newAdmissionType_shouldSaveAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		AdmissionType newAdmissionType = admissionTypeIoOperation.newAdmissionType(admissionType);
		assertThat(newAdmissionType).isNotNull();
		checkAdmissionTypeIntoDb(newAdmissionType.getCode());
	}

	@Test
	void isCodePresent_shouldReturnTrue_whenCodeExists() throws Exception {
		String code = setupTestAdmissionType(false);
		boolean result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void deleteAdmissionType_shouldRemoveAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundAdmissionType).isNotNull();
		admissionTypeIoOperation.deleteAdmissionType(foundAdmissionType);
		assertThat(admissionTypeIoOperation.isCodePresent(code)).isFalse();
	}

	private String setupTestAdmissionType(boolean usingSet) throws OHException {
		AdmissionType admissionType = testAdmissionType.setup(usingSet);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		return admissionType.getCode();
	}

	private void checkAdmissionTypeIntoDb(String code) throws OHException {
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundAdmissionType).isNotNull();
		testAdmissionType.check(foundAdmissionType);
	}

}
