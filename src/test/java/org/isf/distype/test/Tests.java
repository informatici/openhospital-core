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
package org.isf.distype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperation;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Tests extends OHCoreTestCase {

	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseTypeIoOperation diseaseTypeIoOperation;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDiseaseType = new TestDiseaseType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDiseaseTypeGets() throws Exception {
		String code = _setupTestDiseaseType(false);
		_checkDiseaseTypeIntoDb(code);
	}

	@Test
	public void testDiseaseTypeSets() throws Exception {
		String code = _setupTestDiseaseType(true);
		_checkDiseaseTypeIntoDb(code);
	}

	@Test
	public void testIoGetDiseaseType() throws Exception {
		String code = _setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getOne(code);
		ArrayList<DiseaseType> diseaseTypes = diseaseTypeIoOperation.getDiseaseTypes();
		assertThat(diseaseTypes).contains(foundDiseaseType);

	}

	@Test
	public void testIoUpdateDiseaseType() throws Exception {
		String code = _setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getOne(code);
		foundDiseaseType.setDescription("Update");
		boolean result = diseaseTypeIoOperation.updateDiseaseType(foundDiseaseType);
		DiseaseType updateDiseaseType = diseaseTypeIoOperationRepository.getOne(code);
		assertThat(result).isTrue();
		assertThat(updateDiseaseType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewDiseaseType() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(true);
		boolean result = diseaseTypeIoOperation.newDiseaseType(diseaseType);
		assertThat(result).isTrue();
		_checkDiseaseTypeIntoDb(diseaseType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestDiseaseType(false);
		boolean result = diseaseTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDiseaseType() throws Exception {
		String code = _setupTestDiseaseType(false);
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getOne(code);
		boolean result = diseaseTypeIoOperation.deleteDiseaseType(foundDiseaseType);
		result = diseaseTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestDiseaseType(boolean usingSet) throws OHException {
		DiseaseType diseaseType = testDiseaseType.setup(usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		return diseaseType.getCode();
	}

	private void _checkDiseaseTypeIntoDb(String code) throws OHException {
		DiseaseType foundDiseaseType = diseaseTypeIoOperationRepository.getOne(code);
		testDiseaseType.check(foundDiseaseType);
	}
}