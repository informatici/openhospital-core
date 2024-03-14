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
package org.isf.agetype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.agetype.manager.AgeTypeBrowserManager;
import org.isf.agetype.model.AgeType;
import org.isf.agetype.service.AgeTypeIoOperationRepository;
import org.isf.agetype.service.AgeTypeIoOperations;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestAgeType testAgeType;

	@Autowired
	AgeTypeIoOperations ageTypeIoOperations;
	@Autowired
	AgeTypeIoOperationRepository ageTypeIoOperationRepository;
	@Autowired
	AgeTypeBrowserManager ageTypeBrowserManager;

	@BeforeAll
	static void setUpClass() {
		testAgeType = new TestAgeType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testAgeTypeGets() throws Exception {
		String code = setupTestAgeType(false);
		checkAgeTypeIntoDb(code);
	}

	@Test
	void testAgeTypeSets() throws Exception {
		String code = setupTestAgeType(true);
		checkAgeTypeIntoDb(code);
	}

	@Test
	void testIoGetAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		List<AgeType> ageTypes = ageTypeIoOperations.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	void testIoUpdateAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(4);
		foundAgeType.setTo(40);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		List<AgeType> updatedAgeTypes = ageTypeIoOperations.updateAgeType(ageTypes);
		AgeType updateAgeType = ageTypeIoOperationRepository.findOneByCode(updatedAgeTypes.get(0).getCode());
		assertThat(updateAgeType).isNotNull();
		assertThat(updateAgeType.getFrom()).isEqualTo(4);
		assertThat(updateAgeType.getTo()).isEqualTo(40);
	}

	@Test
	void testIoGetAgeTypeByCode() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeIoOperations.getAgeTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	@Test
	void testMgrGetAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		List<AgeType> ageTypes = ageTypeBrowserManager.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	void testMgrUpdateAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(4);
		foundAgeType.setTo(40);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		List<AgeType> updatedAgeTypes = ageTypeBrowserManager.updateAgeType(ageTypes);
		AgeType updateAgeType = ageTypeIoOperationRepository.findOneByCode(updatedAgeTypes.get(0).getCode());
		assertThat(updateAgeType).isNotNull();
		assertThat(updateAgeType.getFrom()).isEqualTo(4);
		assertThat(updateAgeType.getTo()).isEqualTo(40);
	}

	@Test
	void testMgrGetTypeByAge() throws Exception {
		String code = setupTestAgeType(false);
		ageTypeIoOperationRepository.findOneByCode(code);
		String foundCode = ageTypeBrowserManager.getTypeByAge(9);
		assertThat(foundCode).isEqualTo(code);

		assertThat(ageTypeBrowserManager.getTypeByAge(-1)).isNull();
	}

	@Test
	void testMgrGetAgeTypeByCode() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeBrowserManager.getTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	@Test
	void testAgeTypeEqualHashToString() throws Exception {
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

	@Test
	void testMgrAgeTypeValidation() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(0);
		foundAgeType.setTo(1);
		AgeType foundAgeType2 = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType2.setFrom(0);
		foundAgeType2.setTo(1);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		ageTypes.add(foundAgeType2);

		// Range overlap
		assertThatThrownBy(() -> ageTypeBrowserManager.updateAgeType(ageTypes))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
		// Age range not defined
		foundAgeType2.setFrom(90);
		foundAgeType2.setTo(100);
		List<AgeType> ageTypes2 = new ArrayList<>();
		ageTypes2.add(foundAgeType);
		ageTypes2.add(foundAgeType2);
		assertThatThrownBy(() -> ageTypeBrowserManager.updateAgeType(ageTypes2))
			.isInstanceOf(OHServiceException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
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
