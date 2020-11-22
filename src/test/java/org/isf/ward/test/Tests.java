<<<<<<< HEAD
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
=======
/*-
 * #%L
 * OpenHospital
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2020 Informatici Senza Frontiere
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
>>>>>>> 1e7c42900a486f154cf95232f7189b88b929e885
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
<<<<<<< HEAD
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
=======
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
>>>>>>> 1e7c42900a486f154cf95232f7189b88b929e885
 */
package org.isf.ward.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.service.WardIoOperations;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestWard testWard;

	@Autowired
	WardIoOperations wardIoOperation;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testWard = new TestWard();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testWardGets() throws Exception {
		String code = _setupTestWard(false);
		_checkWardIntoDb(code);
	}

	@Test
	public void testWardSets() throws Exception {
		String code = _setupTestWard(true);
		_checkWardIntoDb(code);
	}

	@Test
	public void testIoGetWardsNoMaternity() throws Exception {
		// given:
		String code = _setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findOne(code);

		// when:
		ArrayList<Ward> wards = wardIoOperation.getWardsNoMaternity();

		// then:
		assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(foundWard.getDescription());
	}

	@Test
	public void testIoGetWards() throws Exception {
		// given:
		String code = _setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findOne(code);

		// when:
		ArrayList<Ward> wards = wardIoOperation.getWards(code);

		// then:
		assertThat(wards.get(0).getDescription()).isEqualTo(foundWard.getDescription());
	}

	@Test
<<<<<<< HEAD
	public void testIoNewWard() throws Exception {
		Ward ward = testWard.setup(true);
		Ward newWard = wardIoOperation.newWard(ward);

		assertThat(newWard.getDescription()).isEqualTo("TestDescription");
		_checkWardIntoDb(ward.getCode());
=======
	public void testIoNewWard() {
		try {
			Ward ward = testWard.setup(true);
			Ward newWard = wardIoOperation.newWard(ward);


			assertEquals("TestDescription", newWard.getDescription());
			_checkWardIntoDb(ward.getCode());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
>>>>>>> 1e7c42900a486f154cf95232f7189b88b929e885
	}

	@Test
<<<<<<< HEAD
	public void testIoUpdateWard() throws Exception {
		// given:
		String code = _setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findOne(code);
		foundWard.setDescription("Update");

		// when:
		wardIoOperation.updateWard(foundWard);
		Ward updateWard = wardIoOperationRepository.findOne(code);

		// then:
		assertThat(updateWard.getDescription()).isEqualTo("Update");
=======
	public void testIoUpdateWard() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findOne(code);
			foundWard.setDescription("Update");

			// when:
			wardIoOperation.updateWard(foundWard);
			Ward updateWard = wardIoOperationRepository.findOne(code);

			// then:
			assertEquals("Update", updateWard.getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
>>>>>>> 1e7c42900a486f154cf95232f7189b88b929e885
	}

	@Test
<<<<<<< HEAD
	public void testIoUpdateWardNoCodePresent() throws Exception {
		Ward ward = testWard.setup(true);
		ward.setCode("X");
		Ward result = wardIoOperation.updateWard(ward);
		assertThat(result.getCode()).isEqualTo("X");
=======
	public void testIoUpdateWardNoCodePresent() {
		try {
			Ward ward = testWard.setup(true);
			ward.setCode("X");
			Ward updateWard = wardIoOperation.updateWard(ward);

			
			assertEquals("X", updateWard.getCode());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
>>>>>>> 1e7c42900a486f154cf95232f7189b88b929e885
	}

	@Test
	public void testIoDeleteWard() throws Exception {
		// given:
		String code = _setupTestWard(false);
		Ward foundWard = wardIoOperationRepository.findOne(code);

		// when:
		boolean result = wardIoOperation.deleteWard(foundWard);

		// then:
		assertThat(result).isTrue();
		result = wardIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsCodePresent() throws Exception {
		String code = _setupTestWard(false);
		boolean result = wardIoOperation.isCodePresent(code);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoIsCodePresentFalse() throws Exception {
		boolean result = wardIoOperation.isCodePresent("X");
		assertThat(result).isFalse();
	}

	@Test
	public void testIoIsMaternityPresent() throws Exception {
		boolean result = wardIoOperation.isMaternityPresent();

		if (!result) {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.saveAndFlush(ward);
			result = wardIoOperation.isMaternityPresent();
		}

		assertThat(result).isTrue();
	}

	@Test
	public void testFindWard() throws Exception {
		String code = _setupTestWard(false);
		Ward result = wardIoOperation.findWard(code);

		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(code);
	}

	private String _setupTestWard(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		return ward.getCode();
	}

	private void _checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findOne(code);
		testWard.check(foundWard);
	}
}
