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
package org.isf.disease.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.disease.model.Disease;
import org.isf.disease.service.DiseaseIoOperationRepository;
import org.isf.disease.service.DiseaseIoOperations;
import org.isf.distype.model.DiseaseType;
import org.isf.distype.service.DiseaseTypeIoOperationRepository;
import org.isf.distype.test.TestDiseaseType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Tests extends OHCoreTestCase {

	private static TestDisease testDisease;
	private static TestDiseaseType testDiseaseType;

	@Autowired
	DiseaseIoOperations diseaseIoOperation;
	@Autowired
	DiseaseIoOperationRepository diseaseIoOperationRepository;
	@Autowired
	DiseaseTypeIoOperationRepository diseaseTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDisease = new TestDisease();
		testDiseaseType = new TestDiseaseType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDiseaseGets() throws Exception {
		String code = _setupTestDisease(false);
		_checkDiseaseIntoDb(code);
	}

	@Test
	public void testDiseaseSets() throws Exception {
		String code = _setupTestDisease(true);
		_checkDiseaseIntoDb(code);
	}

	@Test
	public void testIoGetDiseaseByCode() throws Exception {
		String code = _setupTestDisease(false);
		//Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.parseInt(code));
		Disease foundDisease = diseaseIoOperationRepository.findOneByCode(code);
		testDisease.check(foundDisease);
	}

	@Test
	public void testIoGetDiseases() throws Exception {
		String code = _setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));

		ArrayList<Disease> diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, false);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, false, false);
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, false);
		assertThat(diseases).contains(foundDisease);

		foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
		assertThat(diseases).doesNotContain(foundDisease);
		foundDisease.setOpdInclude(true);
		foundDisease.setIpdInInclude(true);
		foundDisease.setIpdOutInclude(true);
		diseaseIoOperationRepository.saveAndFlush(foundDisease);
		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), true, true, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, false, true);
		assertThat(diseases).contains(foundDisease);

		diseases = diseaseIoOperation.getDiseases(foundDisease.getType().getCode(), false, true, false);
		assertThat(diseases).contains(foundDisease);
	}

	@Test
	public void testIoNewDisease() throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, true);
		boolean result = diseaseIoOperation.newDisease(disease);
		assertThat(result).isTrue();
		_checkDiseaseIntoDb(disease.getCode());
	}

	@Test
	public void testIoUpdateDisease() throws Exception {
		String code = _setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		foundDisease.setDescription("Update");
		boolean result = diseaseIoOperation.updateDisease(foundDisease);
		Disease updateDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));

		assertThat(result).isTrue();
		assertThat(updateDisease.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoHasDiseaseModified() throws Exception {
		String code = _setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		boolean result = diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
	}

	@Test
	public void testIoDeleteDisease() throws Exception {
		String code = _setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		boolean result = diseaseIoOperation.deleteDisease(foundDisease);
		assertThat(result).isTrue();
		assertThat(foundDisease.getIpdInInclude()).isFalse();
		assertThat(foundDisease.getIpdOutInclude()).isFalse();
		assertThat(foundDisease.getOpdInclude()).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestDisease(false);
		boolean result = diseaseIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsDescriptionPresent() throws Exception {
		String code = _setupTestDisease(false);
		Disease foundDisease = diseaseIoOperation.getDiseaseByCode(Integer.valueOf(code));
		boolean result = diseaseIoOperation.isDescriptionPresent(foundDisease.getDescription(), foundDisease.getType().getCode());
		assertThat(result).isTrue();
	}

	private String _setupTestDisease(boolean usingSet) throws Exception {
		DiseaseType diseaseType = testDiseaseType.setup(false);
		Disease disease = testDisease.setup(diseaseType, usingSet);
		diseaseTypeIoOperationRepository.saveAndFlush(diseaseType);
		diseaseIoOperationRepository.saveAndFlush(disease);
		return disease.getCode();
	}

	private void _checkDiseaseIntoDb(String code) throws Exception {
		Disease foundDisease = diseaseIoOperationRepository.getOne(code);
		testDisease.check(foundDisease);
	}
}