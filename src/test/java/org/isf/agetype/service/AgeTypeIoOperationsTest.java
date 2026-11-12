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
package org.isf.agetype.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.agetype.TestAgeType;
import org.isf.agetype.model.AgeType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AgeTypeIoOperationsTest extends OHCoreTestCase {

	private static TestAgeType testAgeType;

	@Autowired
	AgeTypeIoOperations ageTypeIoOperations;
	@Autowired
	AgeTypeIoOperationRepository ageTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testAgeType = new TestAgeType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void getAgeType_shouldReturnSavedAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		List<AgeType> ageTypes = ageTypeIoOperations.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	void updateAgeType_shouldSaveNewRange() throws Exception {
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
	void getAgeTypeByCode_shouldFindAgeTypeByIndex() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeIoOperations.getAgeTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	private String setupTestAgeType(boolean usingSet) throws Exception {
		AgeType ageType = testAgeType.setup(usingSet);
		ageTypeIoOperationRepository.saveAndFlush(ageType);
		return ageType.getCode();
	}

}
