/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2025 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.profession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.profession.manager.ProfessionBrowserManager;
import org.isf.profession.model.Profession;
import org.isf.profession.service.ProfessionIoOperation;
import org.isf.profession.service.ProfessionIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class Tests extends OHCoreTestCase {

	private static TestProfession testProfession;

	@Autowired
	ProfessionIoOperation professionIoOperation;
	@Autowired
	ProfessionIoOperationRepository professionIoOperationRepository;
	@Autowired
	ProfessionBrowserManager professionBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testProfession = new TestProfession();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testProfessionGets() throws Exception {
		String code = setupTestProfession(false);
		checkProfessionIntoDb(code);
	}

	@Test
	void testProfessionSets() throws Exception {
		String code = setupTestProfession(true);
		checkProfessionIntoDb(code);
	}

	@Test
	void testIoGetProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		List<Profession> professions = professionIoOperation.getProfessions();
		assertThat(professions).contains(foundProfession);
	}

	@Test
	void testIoUpdateProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		foundProfession.setDescription("Update");
		Profession updatedProfession = professionIoOperation.updateProfession(foundProfession);
		assertThat(updatedProfession.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewProfession() throws Exception {
		Profession profession = testProfession.setup(true);
		Profession newProfession = professionIoOperation.newProfession(profession);
		checkProfessionIntoDb(newProfession.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestProfession(false);
		boolean result = professionIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testIoDeleteProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		professionBrowserManager.deleteProfession(foundProfession);
		boolean result = professionIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testMgrGetProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		List<Profession> professions = professionBrowserManager.getProfessions();
		assertThat(professions).contains(foundProfession);
	}

	@Test
	void testMgrUpdateProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		foundProfession.setDescription("Update");
		Profession updatedProfession = professionBrowserManager.updateProfession(foundProfession);
		assertThat(updatedProfession.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewProfession() throws Exception {
		Profession profession = testProfession.setup(true);
		Profession newProfession = professionBrowserManager.newProfession(profession);
		checkProfessionIntoDb(newProfession.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestProfession(false);
		boolean result = professionBrowserManager.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	void testGetProfessionFound() throws Exception {
		String code = setupTestProfession(false);
		assertThat(professionBrowserManager.getProfession(code)).isNotNull();
	}

	@Test
	void testGetProfessionNotFound() throws Exception {
		setupTestProfession(false);
		assertThat(professionBrowserManager.getProfession("someCodeThatDoesNotExist")).isNull();
	}

	@Test
	void testMgrDeleteProfession() throws Exception {
		String code = setupTestProfession(false);
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		professionBrowserManager.deleteProfession(foundProfession);
		boolean result = professionBrowserManager.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	void testMgrValidateProfessionCodeEmpty() throws Exception {
		Profession profession = new Profession("ZZ", "TestDescription");
		profession.setCode("");
		assertThatThrownBy(() -> professionBrowserManager.newProfession(profession))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testMgrValidateProfessionCodeTooLong() throws Exception {
		Profession profession = new Profession("ZZ", "TestDescription");
		profession.setCode("1234567890123456789012345678901234567890123456789012345");
		assertThatThrownBy(() -> professionBrowserManager.newProfession(profession))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testMgrValidateProfessionDescriptionEmpty() throws Exception {
		Profession profession = new Profession("ZZ", "TestDescription");
		profession.setDescription("");
		assertThatThrownBy(() -> professionBrowserManager.newProfession(profession))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testMgrValidationInsert() throws Exception {
		String code = setupTestProfession(false);
		professionIoOperationRepository.getReferenceById(code);
		// code already exists
		Profession profession2 = new Profession("ZZ", "TestDescription");
		profession2.setCode(code);
		assertThatThrownBy(() -> professionBrowserManager.newProfession(profession2))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	void testProfessionEqualHashToString() throws Exception {
		String code = setupTestProfession(false);
		Profession profession = professionIoOperationRepository.getReferenceById(code);
		Profession profession2 = new Profession("code", "description");
		assertThat(profession)
				.isEqualTo(profession)
				.isNotEqualTo(profession2)
				.isNotEqualTo("xyzzy");
		profession2.setCode(profession.getCode());
		profession2.setDescription(profession.getDescription());
		assertThat(profession).isEqualTo(profession2);

		assertThat(profession.hashCode()).isPositive();

		assertThat(profession).hasToString(profession.getDescription());
	}

	private String setupTestProfession(boolean usingSet) throws OHException {
		Profession profession = testProfession.setup(usingSet);
		professionIoOperationRepository.saveAndFlush(profession);
		return profession.getCode();
	}

	private void checkProfessionIntoDb(String code) throws OHException {
		Profession foundProfession = professionIoOperationRepository.getReferenceById(code);
		testProfession.check(foundProfession);
	}
}
