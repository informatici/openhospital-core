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
package org.isf.admtype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.admtype.TestAdmissionType;
import org.isf.admtype.service.AdmissionTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdmissionTypeTest extends OHCoreTestCase {

	private static TestAdmissionType testAdmissionType;

	@Autowired
	private AdmissionTypeIoOperationRepository admissionTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testAdmissionType = new TestAdmissionType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestAdmissionType(false);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestAdmissionType(true);
		checkAdmissionTypeIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseAdmissionTypeFields() throws Exception {
		String code = setupTestAdmissionType(false);
		AdmissionType admissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(admissionType).isNotNull();
		AdmissionType admissionType2 = new AdmissionType("someCode", "someDescription");
		assertThat(admissionType)
			.isEqualTo(admissionType)
			.isNotEqualTo(admissionType2)
			.isNotEqualTo("xyzzy");
		admissionType2.setCode(code);
		assertThat(admissionType).isEqualTo(admissionType2);

		assertThat(admissionType.hashCode()).isPositive();

		assertThat(admissionType2).hasToString("someDescription");
	}

	private String setupTestAdmissionType(boolean usingSet) throws OHException {
		AdmissionType admissionType = testAdmissionType.setup(usingSet);
		admissionTypeIoOperationRepository.saveAndFlush(admissionType);
		return admissionType.getCode();
	}

	private void checkAdmissionTypeIntoDb(String code) throws OHException {
		AdmissionType foundAdmissionType = admissionTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundAdmissionType).isNotNull();
		testAdmissionType.check(foundAdmissionType);
	}

}
