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
package org.isf.disctype.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.disctype.TestDischargeType;
import org.isf.disctype.service.DischargeTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DischargeTypeTest extends OHCoreTestCase {

	private static TestDischargeType testDischargeType;

	@Autowired
	DischargeTypeIoOperationRepository dischargeTypeIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testDischargeType = new TestDischargeType();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		String code = setupTestDischargeType(false);
		checkDischargeTypeIntoDb(code);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		String code = setupTestDischargeType(true);
		checkDischargeTypeIntoDb(code);
	}

	@Test
	void equalsHashCodeToString_shouldUseDischargeTypeFields() throws Exception {
		String code = setupTestDischargeType(false);
		DischargeType dischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(dischargeType).isNotNull();
		DischargeType dischargeType2 = new DischargeType("someCode", "someDescription");
		assertThat(dischargeType)
				.isEqualTo(dischargeType)
				.isNotEqualTo(dischargeType2)
				.isNotEqualTo("xyzzy");
		dischargeType2.setCode(code);
		assertThat(dischargeType).isEqualTo(dischargeType2);

		assertThat(dischargeType.hashCode()).isPositive();

		assertThat(dischargeType2).hasToString("someDescription");
	}

	private String setupTestDischargeType(boolean usingSet) throws OHException {
		DischargeType dischargeType = testDischargeType.setup(usingSet);
		dischargeTypeIoOperationRepository.saveAndFlush(dischargeType);
		return dischargeType.getCode();
	}

	private void checkDischargeTypeIntoDb(String code) throws OHException {
		DischargeType foundDischargeType = dischargeTypeIoOperationRepository.findById(code).orElse(null);
		assertThat(foundDischargeType).isNotNull();
		testDischargeType.check(foundDischargeType);
	}

}
