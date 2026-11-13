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
package org.isf.pricesothers.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.isf.OHCoreTestCase;
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PricesOthersTest extends OHCoreTestCase {

	private static TestPricesOthers testPricesOthers;

	@Autowired
	PriceOthersIoOperationRepository priceOthersIoOperationRepository;

	@BeforeAll
	static void setUpClass() {
		testPricesOthers = new TestPricesOthers();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void constructor_shouldSetFields() throws Exception {
		// given:
		int id = setupTestPricesOthers(false);

		// then:
		checkPricesOthersIntoDb(id);
	}

	@Test
	void setters_shouldSetFields() throws Exception {
		// given:
		int id = setupTestPricesOthers(true);

		// then:
		checkPricesOthersIntoDb(id);
	}

	@Test
	void toString_shouldReturnDescription() throws Exception {
		PricesOthers pricesOthers = new PricesOthers("TestCode", "TestDescription", true, false, false, true);
		assertThat(pricesOthers).hasToString("TestDescription");
	}

	@Test
	void equals_shouldCompareId() throws Exception {
		PricesOthers pricesOthers = new PricesOthers(-1, "TestCode", "TestDescription", true, false, false, true);

		assertThat(pricesOthers)
				.isNotNull()
				.isEqualTo(pricesOthers)
				.isNotEqualTo("someString");

		PricesOthers pricesOthers2 = testPricesOthers.setup(true);
		pricesOthers2.setId(-99);
		assertThat(pricesOthers).isNotEqualTo(pricesOthers2);

		pricesOthers2.setId(-1);
		assertThat(pricesOthers).isEqualTo(pricesOthers2);
	}

	@Test
	void hashCode_shouldBeComputedFromId() throws Exception {
		PricesOthers pricesOthers = testPricesOthers.setup(true);
		pricesOthers.setId(1);
		int hashCode = pricesOthers.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + 1);
		// check computed value
		assertThat(pricesOthers.hashCode()).isEqualTo(hashCode);
	}

	private int setupTestPricesOthers(boolean usingSet) throws Exception {
		PricesOthers pricesOthers = testPricesOthers.setup(usingSet);
		priceOthersIoOperationRepository.saveAndFlush(pricesOthers);
		return pricesOthers.getId();
	}

	private void checkPricesOthersIntoDb(int id) {
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		testPricesOthers.check(foundPricesOthers);
	}

}
