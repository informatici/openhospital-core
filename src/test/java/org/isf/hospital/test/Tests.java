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
package org.isf.hospital.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.isf.OHCoreTestCase;
import org.isf.hospital.manager.HospitalBrowsingManager;
import org.isf.hospital.model.Hospital;
import org.isf.hospital.service.HospitalIoOperationRepository;
import org.isf.hospital.service.HospitalIoOperations;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestHospital testHospital;

	@Autowired
	HospitalIoOperations hospitalIoOperation;
	@Autowired
	HospitalIoOperationRepository hospitalIoOperationRepository;
	@Autowired
	HospitalBrowsingManager hospitalBrowsingManager;

	@BeforeClass
	public static void setUpClass() {
		testHospital = new TestHospital();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testHospitalGets() throws Exception {
		String code = setupTestHospital(false);
		checkHospitalIntoDb(code);
	}

	@Test
	public void testHospitalSets() throws Exception {
		String code = setupTestHospital(true);
		checkHospitalIntoDb(code);
	}

	@Test
	public void testIoGetHospital() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		Hospital getHospital = hospitalIoOperation.getHospital();
		assertThat(getHospital).isEqualTo(foundHospital);
	}

	@Test
	public void testIoUpdateHospital() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		foundHospital.setDescription("Update");
		Hospital updateHospital = hospitalIoOperation.updateHospital(foundHospital);
		assertThat(updateHospital.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoGetHospitalCurrencyCod() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		String cod = hospitalIoOperation.getHospitalCurrencyCod();
		assertThat(foundHospital.getCurrencyCod()).isEqualTo(cod);

		foundHospital.setCurrencyCod(null);
		Hospital updateHospital = hospitalIoOperation.updateHospital(foundHospital);
		assertThat(updateHospital.getCurrencyCod()).isNull();
	}

	@Test
	public void testIoHospitalSanitize() throws Exception {
		Method method = hospitalIoOperation.getClass().getDeclaredMethod("sanitize", String.class);
		method.setAccessible(true);
		assertThat((String) method.invoke(hospitalIoOperation, "abc'de'f")).isEqualTo("abc''de''f");
		assertThat((String) method.invoke(hospitalIoOperation, (String) null)).isNull();
		assertThat((String) method.invoke(hospitalIoOperation, "abcdef")).isEqualTo("abcdef");
	}

	@Test
	public void testMgrGetHospital() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		Hospital getHospital = hospitalBrowsingManager.getHospital();
		assertThat(getHospital).isEqualTo(foundHospital);
	}

	@Test
	public void testMgrUpdateHospital() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		foundHospital.setDescription("Update");
		Hospital updateHospital = hospitalBrowsingManager.updateHospital(foundHospital);
		assertThat(updateHospital.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testMgrGetHospitalCurrencyCod() throws Exception {
		String code = setupTestHospital(false);
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		String cod = hospitalBrowsingManager.getHospitalCurrencyCod();
		assertThat(foundHospital.getCurrencyCod()).isEqualTo(cod);

		foundHospital.setCurrencyCod(null);
		Hospital updateHospital = hospitalBrowsingManager.updateHospital(foundHospital);
		assertThat(updateHospital.getCurrencyCod()).isNull();
	}

	@Test
	public void testHospitalGetterSetter() throws Exception {
		String code = setupTestHospital(false);
		Hospital hospital = hospitalIoOperationRepository.findById(code).get();

		Integer lock = hospital.getLock();
		hospital.setLock(-1);
		assertThat(hospital.getLock()).isEqualTo(-1);
	}

	@Test
	public void testHospitalHashToString() throws Exception {
		String code = setupTestHospital(false);
		Hospital hospital = hospitalIoOperationRepository.findById(code).get();

		assertThat(hospital.hashCode()).isPositive();

		assertThat(hospital).hasToString(hospital.getDescription());
	}

	private String setupTestHospital(boolean usingSet) throws OHException {
		Hospital hospital = testHospital.setup(usingSet);
		hospitalIoOperationRepository.saveAndFlush(hospital);
		return hospital.getCode();
	}

	private void checkHospitalIntoDb(String code) throws OHException {
		Hospital foundHospital = hospitalIoOperationRepository.findById(code).get();
		testHospital.check(foundHospital);
	}
}