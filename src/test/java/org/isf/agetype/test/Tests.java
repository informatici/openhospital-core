/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.agetype.model.AgeType;
import org.isf.agetype.service.AgeTypeIoOperationRepository;
import org.isf.agetype.service.AgeTypeIoOperations;
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
		String code = _setupTestAgeType(false);
		_checkAgeTypeIntoDb(code);
	}

	@Test
	public void testAgeTypeSets() throws Exception {
		String code = _setupTestAgeType(true);
		_checkAgeTypeIntoDb(code);
	}

	@Test
	public void testIoGetAgeType() throws Exception {
		String code = _setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		ArrayList<AgeType> ageTypes = ageTypeIoOperations.getAgeType();

		assertThat(ageTypes.get(ageTypes.size() - 1).getDescription()).isEqualTo(foundAgeType.getDescription());
	}

	@Test
	public void testIoUpdateAgeType() throws Exception {
		String code = _setupTestAgeType(false);
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		foundAgeType.setFrom(4);
		foundAgeType.setTo(40);
		ArrayList<AgeType> ageTypes = new ArrayList<>();
		ageTypes.add(foundAgeType);
		boolean result = ageTypeIoOperations.updateAgeType(ageTypes);
		AgeType updateAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		assertThat(result).isTrue();
		assertThat(updateAgeType.getFrom()).isEqualTo(4);
		assertThat(updateAgeType.getTo()).isEqualTo(40);
	}

	@Test
	public void testIoGetAgeTypeByCode() throws Exception {
		String code = _setupTestAgeType(false);
		AgeType ageType = ageTypeIoOperationRepository.findOneByCode(code);
		AgeType foundAgeType = ageTypeIoOperations.getAgeTypeByCode(9);

		assertThat(foundAgeType.getFrom()).isEqualTo(ageType.getFrom());
		assertThat(foundAgeType.getTo()).isEqualTo(ageType.getTo());
		assertThat(foundAgeType.getDescription()).isEqualTo(ageType.getDescription());
	}

	private String _setupTestAgeType(boolean usingSet) throws Exception {
		AgeType ageType = testAgeType.setup(usingSet);
		ageTypeIoOperationRepository.saveAndFlush(ageType);
		return ageType.getCode();
	}

	private void _checkAgeTypeIntoDb(String code) throws Exception {
		AgeType foundAgeType = ageTypeIoOperationRepository.findOneByCode(code);
		testAgeType.check(foundAgeType);
	}
}