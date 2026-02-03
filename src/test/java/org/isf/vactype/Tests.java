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
package org.isf.vactype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestVaccineType testVaccineType;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineTypeBrowserManager vaccineTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testVaccineType = new TestVaccineType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testVaccineTypeGets() throws Exception {
		String code = setupTestVaccineType(false);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	void testVaccineTypeSets() throws Exception {
		String code = setupTestVaccineType(true);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	void testIoGetVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		List<VaccineType> vaccineTypes = vaccineTypeIoOperation.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	void testIoUpdateVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		foundVaccineType.setDescription("Update");
		VaccineType updatedVaccineType = vaccineTypeIoOperation.updateVaccineType(foundVaccineType);
		assertThat(updatedVaccineType).isNotNull();
		assertThat(updatedVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testIoNewVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		VaccineType newVaccineType = vaccineTypeIoOperation.newVaccineType(vaccineType);
		checkVaccineTypeIntoDb(newVaccineType.getCode());
	}

	@Test
	void testIoIsCodePresent() throws Exception {
		String code = setupTestVaccineType(false);
		assertThat(vaccineTypeIoOperation.isCodePresent(code)).isTrue();
	}

	@Test
	void testIoDeleteVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		vaccineTypeIoOperation.deleteVaccineType(foundVaccineType);
		assertThat(vaccineTypeIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoFindVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		assertThat(foundVaccineType.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrGetVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		List<VaccineType> vaccineTypes = vaccineTypeBrowserManager.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	void testMgrUpdateVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		foundVaccineType.setDescription("Update");
		VaccineType updatedVaccineType = vaccineTypeBrowserManager.updateVaccineType(foundVaccineType);
		assertThat(updatedVaccineType).isNotNull();
		assertThat(updatedVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		assertThat(vaccineTypeBrowserManager.newVaccineType(vaccineType)).isNotNull();
		checkVaccineTypeIntoDb(vaccineType.getCode());
	}

	@Test
	void testMgrIsCodePresent() throws Exception {
		String code = setupTestVaccineType(false);
		assertThat(vaccineTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	void testMgrDeleteVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		vaccineTypeBrowserManager.deleteVaccineType(foundVaccineType);
		assertThat(vaccineTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	void testMgrFindVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		assertThat(foundVaccineType.getCode()).isEqualTo(code);
	}

	@Test
	void testMgrFindVaccineTypeNull() throws Exception {
		assertThatThrownBy(() -> vaccineTypeBrowserManager.findVaccineType(null))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		vaccineType.setCode("");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeTooLong() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		vaccineType.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		vaccineType.setDescription("");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationCodeExists() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
			.isInstanceOf(OHDataIntegrityViolationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testVaccineToString() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType).hasToString(vaccineType.getDescription());
	}

	@Test
	void testVaccinePrint() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType).isNotNull();
		assertThat(vaccineType.print()).isEqualTo("vaccineType code=." + vaccineType.getCode() + ". description=." + vaccineType.getDescription() + '.');
	}

	@Test
	void testVaccineEquals() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);

		assertThat(vaccineType)
				.isEqualTo(vaccineType)
				.isNotNull()
				.isNotEqualTo("someStringValue");

		VaccineType vaccineType2 = new VaccineType("A", "adescription");
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setCode(vaccineType.getCode());
		assertThat(vaccineType).isNotEqualTo(vaccineType2);

		vaccineType2.setDescription(vaccineType.getDescription());
		assertThat(vaccineType).isEqualTo(vaccineType2);
	}

	@Test
	void testVaccineTypeHashCode() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(false);
		// compute value
		int hashCode = vaccineType.hashCode();
		// compare with stored value
		assertThat(vaccineType.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestVaccineType(boolean usingSet) throws OHException {
		VaccineType vaccineType = testVaccineType.setup(usingSet);
		vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
		return vaccineType.getCode();
	}

	private void checkVaccineTypeIntoDb(String code) throws OHServiceException {
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(foundVaccineType).isNotNull();
		testVaccineType.check(foundVaccineType);
	}
}
