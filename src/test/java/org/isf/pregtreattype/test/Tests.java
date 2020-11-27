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
package org.isf.pregtreattype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestPregnantTreatmentType testPregnantTreatmentType;

	@Autowired
	PregnantTreatmentTypeIoOperation pregnantTreatmentTypeIoOperation;
	@Autowired
	PregnantTreatmentTypeIoOperationRepository pregnantTreatmentTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testPregnantTreatmentType = new TestPregnantTreatmentType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPregnantTreatmentTypeGets() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		_checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	public void testPregnantTreatmentTypeSets() throws Exception {
		String code = _setupTestPregnantTreatmentType(true);
		_checkPregnantTreatmentTypeIntoDb(code);
	}

	@Test
	public void testIoGetPregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		ArrayList<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeIoOperation.getPregnantTreatmentType();

		for (PregnantTreatmentType pregnantTreatmentType : pregnantTreatmentTypes) {
			if (pregnantTreatmentType.getCode().equals(code)) {
				assertThat(pregnantTreatmentType.getDescription()).isEqualTo(foundPregnantTreatmentType.getDescription());
			}
		}
	}

	@Test
	public void testIoUpdatePregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		foundPregnantTreatmentType.setDescription("Update");
		boolean result = pregnantTreatmentTypeIoOperation.updatePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(result).isTrue();
		PregnantTreatmentType updatePregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		assertThat(updatePregnantTreatmentType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewPregnantTreatmentType() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
		boolean result = pregnantTreatmentTypeIoOperation.newPregnantTreatmentType(pregnantTreatmentType);

		assertThat(result).isTrue();
		_checkPregnantTreatmentTypeIntoDb(pregnantTreatmentType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		boolean result = pregnantTreatmentTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeletePregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		boolean result = pregnantTreatmentTypeIoOperation.deletePregnantTreatmentType(foundPregnantTreatmentType);
		assertThat(result).isTrue();
		result = pregnantTreatmentTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestPregnantTreatmentType(boolean usingSet) throws OHException {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setDescription("Test Description");
		pregnantTreatmentType = testPregnantTreatmentType.setup(usingSet);
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType);
		return pregnantTreatmentType.getCode();
	}

	private void _checkPregnantTreatmentTypeIntoDb(String code) {
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		testPregnantTreatmentType.check(foundPregnantTreatmentType);
	}
}