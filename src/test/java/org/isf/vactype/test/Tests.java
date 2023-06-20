/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.vactype.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestVaccineType testVaccineType;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineTypeBrowserManager vaccineTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testVaccineType = new TestVaccineType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testVaccineTypeGets() throws Exception {
		String code = setupTestVaccineType(false);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	public void testVaccineTypeSets() throws Exception {
		String code = setupTestVaccineType(true);
		checkVaccineTypeIntoDb(code);
	}

	@Test
	public void testIoGetVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		List<VaccineType> vaccineTypes = vaccineTypeIoOperation.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	public void testIoUpdateVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		foundVaccineType.setDescription("Update");
		VaccineType result = vaccineTypeIoOperation.updateVaccineType(foundVaccineType);
		assertThat(result);
		VaccineType updateVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(updateVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		VaccineType result = vaccineTypeIoOperation.newVaccineType(vaccineType);
		assertThat(result);
		checkVaccineTypeIntoDb(vaccineType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = setupTestVaccineType(false);
		boolean result = vaccineTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		boolean result = vaccineTypeIoOperation.deleteVaccineType(foundVaccineType);
		assertThat(result).isTrue();
		result = vaccineTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoFindVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType result = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrGetVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		List<VaccineType> vaccineTypes = vaccineTypeBrowserManager.getVaccineType();
		assertThat(vaccineTypes.get(vaccineTypes.size() - 1).getDescription()).isEqualTo(foundVaccineType.getDescription());
	}

	@Test
	public void testMgrUpdateVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		foundVaccineType.setDescription("Update");
		assertThat(vaccineTypeBrowserManager.updateVaccineType(foundVaccineType));
		VaccineType updateVaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(updateVaccineType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewVaccineType() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);
		assertThat(vaccineTypeBrowserManager.newVaccineType(vaccineType));
		checkVaccineTypeIntoDb(vaccineType.getCode());
	}

	@Test
	public void testMgrIsCodePresent() throws Exception {
		String code = setupTestVaccineType(false);
		assertThat(vaccineTypeBrowserManager.isCodePresent(code)).isTrue();
	}

	@Test
	public void testMgrDeleteVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType foundVaccineType = vaccineTypeIoOperation.findVaccineType(code);
		assertThat(vaccineTypeBrowserManager.deleteVaccineType(foundVaccineType)).isTrue();
		assertThat(vaccineTypeBrowserManager.isCodePresent(code)).isFalse();
	}

	@Test
	public void testMgrFindVaccineType() throws Exception {
		String code = setupTestVaccineType(false);
		VaccineType result = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	@Test
	public void testMgrFindVaccineTypeNull() throws Exception {
		assertThatThrownBy(() -> vaccineTypeBrowserManager.findVaccineType(null))
				.isInstanceOf(RuntimeException.class);
	}

	@Test
	public void testMgrValidationCodeEmpty() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		vaccineType.setCode("");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeTooLong() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		vaccineType.setCode("thisIsACodeThatIsTooLong");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
				.isInstanceOf(OHDataValidationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationDescriptionEmpty() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		vaccineType.setDescription("");
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testMgrValidationCodeExists() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThatThrownBy(() -> vaccineTypeBrowserManager.newVaccineType(vaccineType))
				.isInstanceOf(OHDataIntegrityViolationException.class)
				.has(
						new Condition<Throwable>(
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
				);
	}

	@Test
	public void testVaccineToString() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType).hasToString(vaccineType.getDescription());
	}

	@Test
	public void testVaccinePrint() throws Exception {
		String code = setupTestVaccineType(true);
		VaccineType vaccineType = vaccineTypeBrowserManager.findVaccineType(code);
		assertThat(vaccineType.print()).isEqualTo("vaccineType code=." + vaccineType.getCode() + ". description=." + vaccineType.getDescription() + '.');
	}

	@Test
	public void testVaccineEquals() throws Exception {
		VaccineType vaccineType = testVaccineType.setup(true);

		assertThat(vaccineType.equals(vaccineType)).isTrue();
		assertThat(vaccineType)
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
	public void testVaccineTypeHashCode() throws Exception {
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
		testVaccineType.check(foundVaccineType);
	}
}
