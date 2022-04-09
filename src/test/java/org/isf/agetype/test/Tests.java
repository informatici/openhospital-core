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
package org.isf.agetype.test;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestAgeType testAgeType;

	@Autowired
	AgeTypeIoOperations ageTypeIoOperations;
	@Autowired
	AgeTypeIoOperationRepository ageTypeIoOperationRepository;
	@Autowired
	AgeTypeBrowserManager ageTypeBrowserManager;

	@BeforeClass
	public static void setUpClass() {
		testAgeType = new TestAgeType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testAgeTypeGets() throws Exception {
		String code = setupTestAgeType(false);
		checkAgeTypeIntoDb(code);
	}

	@Test
	public void testAgeTypeSets() throws Exception {
		String code = setupTestAgeType(true);
		checkAgeTypeIntoDb(code);
	}

	@Test
	public void testIoGetAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		List<AgeType> ageTypes = ageTypeIoOperations.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	public void testIoUpdateAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(4);
		foundAgeType.setTo(40);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		boolean result = ageTypeIoOperations.updateAgeType(ageTypes);
		AgeType updateAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		assertThat(result).isTrue();
		assertThat(updateAgeType.getFrom()).isEqualTo(4);
		assertThat(updateAgeType.getTo()).isEqualTo(40);
	}

	@Test
	public void testIoGetAgeTypeByCode() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeIoOperations.getAgeTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	@Test
	public void testMgrGetAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		List<AgeType> ageTypes = ageTypeBrowserManager.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	public void testMgrUpdateAgeType() throws Exception {
		String code = setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(4);
		foundAgeType.setTo(40);
		List<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		boolean result = ageTypeBrowserManager.updateAgeType(ageTypes);
		AgeType updateAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		assertThat(result).isTrue();
		assertThat(updateAgeType.getFrom()).isEqualTo(4);
		assertThat(updateAgeType.getTo()).isEqualTo(40);
	}

	@Test
	public void testMgrGetTypeByAge() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		String foundCode = ageTypeBrowserManager.getTypeByAge(9);
		assertThat(foundCode).isEqualTo(code);

		assertThat(ageTypeBrowserManager.getTypeByAge(-1)).isNull();
	}

	@Test
	public void testMgrGetAgeTypeByCode() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeBrowserManager.getTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	@Test
	public void testAgeTypeEqualHashToString() throws Exception {
		String code = setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType ageType2 = new AgeType(ageType.getCode(), ageType.getDescription());
		ageType2.setFrom(ageType.getFrom());
		ageType2.setTo(ageType.getTo());
		assertThat(ageType.equals(ageType)).isTrue();
		assertThat(ageType)
				.isEqualTo(ageType2)
				.isNotEqualTo("xyzzy");
		ageType2.setCode("xxxx");
		assertThat(ageType).isNotEqualTo(ageType2);

		assertThat(ageType.hashCode()).isPositive();

		assertThat(ageType2).hasToString(ageType.getDescription());
	}

	@Test
	public void testMgrAgeTypeValidation() throws Exception {
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
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
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
								(e -> ((OHServiceException) e).getMessages().size() == 1), "Expecting single validation error")
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
