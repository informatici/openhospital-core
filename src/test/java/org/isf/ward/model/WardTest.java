/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.ward.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.isf.OHCoreTestCase;
import org.isf.utils.exception.OHException;
import org.isf.ward.TestWard;
import org.isf.ward.service.WardIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WardTest extends OHCoreTestCase {

	private static TestWard testWard;

	@Autowired
	WardIoOperationRepository wardIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testWard = new TestWard();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestWard(false);
		checkWardIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestWard(true);
		checkWardIntoDb(code);
	}

	@Test
	void setLock_shouldUpdateLock() throws Exception {
		Ward ward = new Ward("Z", null, null, null, "TestEmail@gmail.com", 1, 1, 1, false, true);
		assertThat(ward.getLock()).isNull();
		ward.setLock(-1);
		assertThat(ward.getLock()).isEqualTo(-1);
	}

	@Test
	void equals_shouldCompareFields() throws Exception {
		Ward ward = testWard.setup(false);
		assertThat(ward)
			.isNotNull()
			.isNotEqualTo("someString");

		Ward ward2 = testWard.setup(false);
		ward2.setCode("-1");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setCode(ward.getCode());
		ward2.setDescription("someNewDescription");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setDescription(ward.getDescription().toLowerCase(Locale.ROOT));
		ward2.setTelephone("someNewTelephone");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setTelephone(ward.getTelephone().toUpperCase(Locale.ROOT));
		ward2.setFax("someNewFax");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setFax(ward.getFax());
		ward2.setEmail("someNewEmailAddress");
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setEmail(ward.getEmail());
		ward2.setBeds(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setBeds(ward.getBeds());
		ward2.setNurs(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setNurs(ward.getNurs());
		ward2.setDocs(-99);
		assertThat(ward).isNotEqualTo(ward2);

		ward2.setDocs(ward.getDocs());
		assertThat(ward).isEqualTo(ward2);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		Ward ward = new Ward("Z", "someDescription", null, null, "TestEmail@gmail.com", 1, 1, 1, false, true);
		assertThat(ward).hasToString("someDescription");
	}

	@Test
	void debug_shouldUseWardFields() throws Exception {
		Ward ward = testWard.setup(true);
		assertThat(ward.debug()).isEqualTo(
			"Ward [code=Z, description=TestDescription, telephone=TestTelephone, fax=TestFac, email=TestEmail@gmail.com, beds=100, nurs=101, docs=102, isPharmacy=true, isMale=false, isFemale=true, lock=null, hashCode=0]");
	}

	@Test
	void hashCode_shouldBeComputedFromCode() throws Exception {
		Ward ward = testWard.setup(true);
		int hashCode = ward.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + ward.getCode().hashCode());
		// return stored value
		assertThat(ward.hashCode()).isEqualTo(hashCode);
	}

	private String setupTestWard(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(usingSet);
		wardIoOperationRepository.saveAndFlush(ward);
		return ward.getCode();
	}

	private void checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findById(code).orElse(null);
		assertThat(foundWard).isNotNull();
		testWard.check(foundWard);
	}

}
