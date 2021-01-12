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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.pregtreattype.manager.PregnantTreatmentTypeBrowserManager;
import org.isf.pregtreattype.model.PregnantTreatmentType;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperation;
import org.isf.pregtreattype.service.PregnantTreatmentTypeIoOperationRepository;
import org.isf.utils.exception.OHDataValidationException;
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
	@Autowired
	PregnantTreatmentTypeBrowserManager pregnantTreatmentTypeBrowserManager;

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

	@Test
	public void testMgrGetPregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		PregnantTreatmentType pregnantTreatmentType2 = new PregnantTreatmentType("AA", "AA description");
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType2);
		PregnantTreatmentType pregnantTreatmentType3 = new PregnantTreatmentType("BB", "BB description");
		pregnantTreatmentTypeIoOperationRepository.saveAndFlush(pregnantTreatmentType3);

		ArrayList<PregnantTreatmentType> pregnantTreatmentTypes = pregnantTreatmentTypeBrowserManager.getPregnantTreatmentType();
		assertThat(pregnantTreatmentTypes).hasSize(3);

		for (PregnantTreatmentType pregnantTreatmentType : pregnantTreatmentTypes) {
			if (pregnantTreatmentType.getCode().equals(code)) {
				assertThat(pregnantTreatmentType.getDescription()).isEqualTo(foundPregnantTreatmentType.getDescription());
			} else {
				assertThat(pregnantTreatmentType.getDescription()).isNotEqualTo(foundPregnantTreatmentType.getDescription());
			}
		}
	}

	@Test
	public void testMgrUpdatePregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		foundPregnantTreatmentType.setDescription("Update");
		assertThat(pregnantTreatmentTypeBrowserManager.updatePregnantTreatmentType(foundPregnantTreatmentType)).isTrue();
		PregnantTreatmentType updatePregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		assertThat(updatePregnantTreatmentType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrNewPregnantTreatmentType() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = testPregnantTreatmentType.setup(true);
		assertThat(pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType)).isTrue();
		_checkPregnantTreatmentTypeIntoDb(pregnantTreatmentType.getCode());
	}

	@Test
	public void testMgrCodeControl() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		assertThat(pregnantTreatmentTypeBrowserManager.codeControl(code)).isTrue();
	}

	@Test
	public void testMgrDeletePregnantTreatmentType() throws Exception {
		String code = _setupTestPregnantTreatmentType(false);
		PregnantTreatmentType foundPregnantTreatmentType = pregnantTreatmentTypeIoOperationRepository.findOne(code);
		assertThat(pregnantTreatmentTypeBrowserManager.deletePregnantTreatmentType(foundPregnantTreatmentType)).isTrue();
		assertThat(pregnantTreatmentTypeBrowserManager.codeControl(code)).isFalse();
	}

	@Test
	public void testMgrValidationCodeEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationCodeTooLong() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("thisIsAVeryLongKey");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationCodeAlreadyExists() throws Exception {
		assertThatThrownBy(() ->
		{
			_setupTestPregnantTreatmentType(true);
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("ZZ");
			pregnantTreatmentType.setDescription("someDescription");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testMgrValidationDescriptionIsEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
			pregnantTreatmentType.setCode("ZZ");
			pregnantTreatmentType.setDescription("");
			pregnantTreatmentTypeBrowserManager.newPregnantTreatmentType(pregnantTreatmentType);
		})
				.isInstanceOf(OHDataValidationException.class);
	}

	@Test
	public void testPregnantTreatmentTypeEquals() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");

		assertThat(pregnantTreatmentType).isEqualTo(pregnantTreatmentType);
		assertThat(pregnantTreatmentType).isNotEqualTo(null);
		assertThat(pregnantTreatmentType).isNotEqualTo("someString");

		PregnantTreatmentType pregnantTreatmentType2 = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("XX");
		pregnantTreatmentType.setDescription("someDescription");

		assertThat(pregnantTreatmentType).isNotEqualTo(pregnantTreatmentType2);
		pregnantTreatmentType2.setCode(pregnantTreatmentType.getCode());
		pregnantTreatmentType2.setDescription("someOtherDescription");
		assertThat(pregnantTreatmentType).isNotEqualTo(pregnantTreatmentType2);

		pregnantTreatmentType2.setDescription(pregnantTreatmentType.getDescription().toUpperCase());
		assertThat(pregnantTreatmentType).isEqualTo(pregnantTreatmentType2);
	}

	@Test
	public void testPregnantTreatmentTypeToString() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");
		assertThat(pregnantTreatmentType).hasToString("someDescription");
	}

	@Test
	public void testPregnantTreatmentTypeHashCode() throws Exception {
		PregnantTreatmentType pregnantTreatmentType = new PregnantTreatmentType();
		pregnantTreatmentType.setCode("ZZ");
		pregnantTreatmentType.setDescription("someDescription");

		int hashCode = pregnantTreatmentType.hashCode();
		// use computed value
		assertThat(pregnantTreatmentType.hashCode()).isEqualTo(hashCode);
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