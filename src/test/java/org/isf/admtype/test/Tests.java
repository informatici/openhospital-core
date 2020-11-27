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
package org.isf.admtype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.admtype.model.AdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperation;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestAdmissionType testAdmissionType;

	@Autowired
	AdmissionTypeIoOperation admissionTypeIoOperation;
	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testAdmissionType = new TestAdmissionType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testAdmissionTypeGets() throws Exception {
		String code = _setupTestAdmissionType(false);
		_checkAdmissionTypeIntoDb(code);
	}

	@Test
	public void testAdmissionTypeSets() throws Exception {
		String code = _setupTestAdmissionType(true);
		_checkAdmissionTypeIntoDb(code);
	}

	@Test
	public void testIoGetAdmissionType() throws Exception {
		String code = _setupTestAdmissionType(false);
		ArrayList<AdmissionType> admissionTypes = admissionTypeIoOperation.getAdmissionType();
		assertThat(admissionTypes).hasSize(1);
		assertThat(admissionTypes.get(0).getDescription()).isEqualTo("TestDescription");
	}

	@Test
	public void testIoUpdateAdmissionType() throws Exception {
		String code = _setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findOne(code);
		foundAdmissionType.setDescription("Update");
		boolean result = admissionTypeIoOperation.updateAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		AdmissionType updateAdmissionType = admissionTypeIoOperationRepository.findOne(code);
		assertThat(updateAdmissionType.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewAdmissionType() throws Exception {
		AdmissionType admissionType = testAdmissionType.setup(true);
		boolean result = admissionTypeIoOperation.newAdmissionType(admissionType);
		assertThat(result).isTrue();
		_checkAdmissionTypeIntoDb(admissionType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestAdmissionType(false);
		boolean result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteAdmissionType() throws Exception {
		String code = _setupTestAdmissionType(false);
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findOne(code);
		boolean result = admissionTypeIoOperation.deleteAdmissionType(foundAdmissionType);
		assertThat(result).isTrue();
		result = admissionTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	private String _setupTestAdmissionType(boolean usingSet) throws OHException {
		AdmissionType admissionType = testAdmissionType.setup(usingSet);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		return admissionType.getCode();
	}

	private void _checkAdmissionTypeIntoDb(String code) throws OHException {
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findOne(code);
		testAdmissionType.check(foundAdmissionType);
	}
}