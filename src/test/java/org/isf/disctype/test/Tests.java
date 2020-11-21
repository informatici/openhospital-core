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
package org.isf.disctype.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.disctype.model.DischargeType;
import org.isf.disctype.service.DischargeTypeIoOperation;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestDischargeType testDischargeType;

	@Autowired
	DischargeTypeIoOperation dischargeTypeIoOperation;
	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDischargeType = new TestDischargeType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDischargeTypeGets() throws Exception {
		String code = _setupTestDischargeType(false);
		_checkDischargeTypeIntoDb(code);
	}

	@Test
	public void testDischargeTypeSets() throws Exception {
		String code = _setupTestDischargeType(true);
		_checkDischargeTypeIntoDb(code);
	}

	@Test
	public void testIoGetDischargeType() throws Exception {
		String code = _setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findOne(code);
		ArrayList<DischargeType> dischargeTypes = dischargeTypeIoOperation.getDischargeType();
		assertThat(dischargeTypes.get(dischargeTypes.size() - 1).getDescription()).isEqualTo(foundDischargeType.getDescription());
	}

	@Test
	public void testIoNewDischargeType() throws Exception {
		DischargeType dischargeType = testDischargeType.setup(true);
		boolean result = dischargeTypeIoOperation.newDischargeType(dischargeType);
		assertThat(result).isTrue();
		_checkDischargeTypeIntoDb(dischargeType.getCode());
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestDischargeType(false);
		boolean result = dischargeTypeIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoDeleteDischargeType() throws Exception {
		String code = _setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findOne(code);
		boolean result = dischargeTypeIoOperation.deleteDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		result = dischargeTypeIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoUpdateDischargeType() throws Exception {
		String code = _setupTestDischargeType(false);
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findOne(code);
		foundDischargeType.setDescription("Update");
		boolean result = dischargeTypeIoOperation.updateDischargeType(foundDischargeType);
		assertThat(result).isTrue();
		DischargeType updateDischargeType = dischargeTypeIoOperationRepository.findOne(code);
		assertThat(updateDischargeType.getDescription()).isEqualTo("Update");
	}

	private String _setupTestDischargeType(boolean usingSet) throws OHException {
		DischargeType dischargeType = testDischargeType.setup(usingSet);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		return dischargeType.getCode();
	}

	private void _checkDischargeTypeIntoDb(String code) throws OHException {
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findOne(code);
		testDischargeType.check(foundDischargeType);
	}
}