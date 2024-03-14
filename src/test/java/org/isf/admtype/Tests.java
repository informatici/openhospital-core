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
package org.isf.admtype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.admtype.manager.AdmissionTypeBrowserManager;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperation;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestAdmissionType testAdmissionType;

	@Autowired
	private AdmissionTypeIoOperation admissionTypeIoOperation;
	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	private AdmissionTypeBrowserManager admissionTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testAdmissionType = new TestAdmissionType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testAdmissionTypeGets() throws Exception {
		String code = setupTestAdmissionType(false);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	void testAdmissionTypeSets() throws Exception {
		String code = setupTestAdmissionType(true);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	void testIoGetAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		List<AdmissionType> admissionTypes = admissionTypeIoOperation.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void testIoUpdateAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();
		admissionType.setDescription("Update");
		AdmissionType updatedAdmissionType = admissionTypeIoOperation.updateAdmissionType(admissionType);
		assertThat(updatedAdmissionType).isNotNull();
		assertThat(updatedAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		AdmissionType newAdmissionType = admissionTypeIoOperation.newAdmissionType(admissionType);
		assertThat(newAdmissionType).isNotNull();
		checkAdmissionTypeIntoDb(newAdmissionType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestAdmissionType(false);
		boolean result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundAdmissionType).isNotNull();
		admissionTypeIoOperation.deleteAdmissionType(foundAdmissionType);
		assertThat(admissionTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrGetAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		List<AdmissionType> admissionTypes = admissionTypeBrowserManager.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	void testMgrUpdateAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();
		admissionType.setDescription("Update");
		AdmissionType updatedAdmissionType = admissionTypeBrowserManager.updateAdmissionType(admissionType);
		assertThat(updatedAdmissionType).isNotNull();
		assertThat(updatedAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testAdmissionTypeEqualHashToString() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();
		AdmissionType admissionType2 = new AdmissionType("someCode", "someDescription");
		assertThat(admissionType)
			.isEqualTo(admissionType)
			.isNotEqualTo(admissionType2)
			.isNotEqualTo("xyzzy");
		admissionType2.setCode(code);
		assertThat(admissionType).isEqualTo(admissionType2);

		assertThat(admissionType.hashCode()).isPositive();

		assertThat(admissionType2).hasToString("someDescription");
	}

	@Test
	void testMgrAdmissionValidation() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();

		// Empty string
		admissionType.setCode("");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// Code is too long
		admissionType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// Description is empty
		admissionType.setCode(code);
		String description = admissionType.getDescription();
		admissionType.setDescription("");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// Code already exists
		admissionType.setDescription(description);
		assertThatThrownBy(() -> admissionTypeBrowserManager.newAdmissionType(admissionType))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);

	}

	@Test
	void testMgrNewAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		AdmissionType newAdmissionType = admissionTypeBrowserManager.newAdmissionType(admissionType);
		assertThat(newAdmissionType).isNotNull();
		checkAdmissionTypeIntoDb(newAdmissionType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestAdmissionType(false);
		boolean result = admissionTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testMgrDeleteAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundAdmissionType).isNotNull();
		admissionTypeBrowserManager.deleteAdmissionType(foundAdmissionType);
		assertThat(admissionTypeBrowserManager.isCodePresent(code)).isFalse();
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