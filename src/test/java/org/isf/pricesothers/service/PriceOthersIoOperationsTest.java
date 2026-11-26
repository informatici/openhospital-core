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
package org.isf.pricesothers.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.isf.OHCoreTestCase;
import org.isf.pricesothers.TestPricesOthers;
import org.isf.pricesothers.model.PricesOthers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PriceOthersIoOperationsTest extends OHCoreTestCase {

	private static TestPricesOthers testPricesOthers;

	@Autowired
	PriceOthersIoOperations priceOthersIoOperations;
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
	void getOthers_shouldReturnSavedOther() throws Exception {
		// given:
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();

		// when:
		List<PricesOthers> result = priceOthersIoOperations.getOthers();

		// then:
		assertThat(result.get(0).getDescription()).isEqualTo(foundPricesOthers.getDescription());
	}

	@Test
	void updateOther_shouldSaveNewDescription() throws Exception {
		// given:
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		foundPricesOthers.setDescription("Update");

		// when:
		PricesOthers updatedPricesOthers = priceOthersIoOperations.updateOther(foundPricesOthers);

		// then:
		assertThat(updatedPricesOthers.getDescription()).isEqualTo("Update");
	}

	@Test
	void newOthers_shouldSaveOther() throws Exception {
		// given:
		PricesOthers pricesOthers = testPricesOthers.setup(true);

		// when:
		PricesOthers newPricesOthers = priceOthersIoOperations.newOthers(pricesOthers);

		// then:
		checkPricesOthersIntoDb(newPricesOthers.getId());
	}

	@Test
	void deleteOthers_shouldRemoveOther() throws Exception {
		// given:
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();

		// when:
		priceOthersIoOperations.deleteOthers(foundPricesOthers);

		// then:
		assertThat(priceOthersIoOperationRepository.existsById(id)).isFalse();
	}

	@Test
	void isCodePresent_shouldReturnTrueOnlyForExistingId() throws Exception {
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		assertThat(priceOthersIoOperations.isCodePresent(foundPricesOthers.getId())).isTrue();
		assertThat(priceOthersIoOperations.isCodePresent(-1)).isFalse();
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
