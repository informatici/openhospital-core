/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.admtype.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestAdmissionType testAdmissionType;

	@Autowired
	private AdmissionTypeIoOperation admissionTypeIoOperation;
	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;
	@Autowired
	private AdmissionTypeBrowserManager admissionTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testAdmissionType = new TestAdmissionType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testAdmissionTypeGets() throws Exception {
		String code = setupTestAdmissionType(false);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	public void testAdmissionTypeSets() throws Exception {
		String code = setupTestAdmissionType(true);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	public void testIoGetAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		List<AdmissionType> admissionTypes = admissionTypeIoOperation.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void testIoUpdateAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		foundAdmissionType.setDescription("Update");
		boolean result = admissionTypeIoOperation.updateAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		AdmissionType updateAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		assertThat(updateAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		boolean result = admissionTypeIoOperation.newAdmissionType(admissionType);
		assertThat(result).isTrue();
		checkAdmissionTypeIntoDb(admissionType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestAdmissionType(false);
		boolean result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		boolean result = admissionTypeIoOperation.deleteAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testMgrGetAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		List<AdmissionType> admissionTypes = admissionTypeBrowserManager.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void testMgrUpdateAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		foundAdmissionType.setDescription("Update");
		boolean result = admissionTypeBrowserManager.updateAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		AdmissionType updateAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		assertThat(updateAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testAdmissionTypeEqualHashToString() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).get();
		AdmissionType admissionType2 = new AdmissionType("someCode", "someDescription");
		assertThat(admissionType.equals(admissionType)).isTrue();
		assertThat(admissionType)
				.isNotEqualTo(admissionType2)
				.isNotEqualTo("xyzzy");
		admissionType2.setCode(code);
		assertThat(admissionType).isEqualTo(admissionType2);

		assertThat(admissionType.hashCode()).isPositive();

		assertThat(admissionType2).hasToString("someDescription");
	}

	@Test
	public void testMgrAdmissionValidation() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).get();

		// Empty string
		admissionType.setCode("");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// Code is too long
		admissionType.setCode("123456789ABCDEF");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// Description is empty
		admissionType.setCode(code);
		String description = admissionType.getDescription();
		admissionType.setDescription("");
		assertThatThrownBy(() -> admissionTypeBrowserManager.updateAdmissionType(admissionType))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
		// Code already exists
		admissionType.setDescription(description);
		assertThatThrownBy(() -> admissionTypeBrowserManager.newAdmissionType(admissionType))
				.isInstanceOf(OHServiceException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);

	}

	@Test
	public void testMgrNewAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		boolean result = admissionTypeBrowserManager.newAdmissionType(admissionType);
		assertThat(result).isTrue();
		checkAdmissionTypeIntoDb(admissionType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestAdmissionType(false);
		boolean result = admissionTypeBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testMgrDeleteAdmissionType() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		boolean result = admissionTypeBrowserManager.deleteAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		result = admissionTypeBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String setupTestAdmissionType(boolean usingSet) throws OHException {
		AdmissionType admissionType = testAdmissionType.setup(usingSet);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		return admissionType.getCode();
	}

	private void checkAdmissionTypeIntoDb(String code) throws OHException {
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).get();
		testAdmissionType.check(foundAdmissionType);
	}
}