/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.pricesothers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.pricesothers.manager.PricesOthersManager;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.isf.pricesothers.service.PriceOthersIoOperations;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Tests extends OHCoreTestCase {

	private static TestPricesOthers testPricesOthers;

	@Autowired
	PriceOthersIoOperations priceOthersIoOperations;
	@Autowired
	PriceOthersIoOperationRepository priceOthersIoOperationRepository;
	@Autowired
	PricesOthersManager pricesOthersManager;

	@BeforeAll
	static void setUpClass() {
		testPricesOthers = new TestPricesOthers();
	}

	@BeforeEach
	void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	void testPricesOthersGets() throws Exception {
		// given:
		int id = setupTestPricesOthers(false);

		// then:
		checkPricesOthersIntoDb(id);
	}

	@Test
	void testPricesOthersSets() throws Exception {
		// given:
		int id = setupTestPricesOthers(true);

		// then:
		checkPricesOthersIntoDb(id);
	}

	@Test
	void testIoGetPricesOthers() throws Exception {
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
	void testIoUpdatePricesOthers() throws Exception {
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
	void testIoNewPricesOthers() throws Exception {
		// given:
		PricesOthers pricesOthers = testPricesOthers.setup(true);

		// when:
		PricesOthers newPricesOthers = priceOthersIoOperations.newOthers(pricesOthers);

		// then:
		checkPricesOthersIntoDb(newPricesOthers.getId());
	}

	@Test
	void testIoDeletePricesOthers() throws Exception {
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
	void testIoIsCodePresent() throws Exception {
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		assertThat(priceOthersIoOperations.isCodePresent(foundPricesOthers.getId())).isTrue();
		assertThat(priceOthersIoOperations.isCodePresent(-1)).isFalse();
	}

	@Test
	void testMgrGetPricesOthers() throws Exception {
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		List<PricesOthers> result = pricesOthersManager.getOthers();
		assertThat(result.get(0).getDescription()).isEqualTo(foundPricesOthers.getDescription());
	}

	@Test
	void testMgrUpdatePricesOthers() throws Exception {
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		foundPricesOthers.setDescription("Update");
		PricesOthers updatedPricesOthers = pricesOthersManager.updateOther(foundPricesOthers);
		assertThat(updatedPricesOthers.getDescription()).isEqualTo("Update");
	}

	@Test
	void testMgrNewPricesOther() throws Exception {
		PricesOthers pricesOthers = testPricesOthers.setup(true);
		PricesOthers newPricesOthers = pricesOthersManager.newOther(pricesOthers);
		checkPricesOthersIntoDb(newPricesOthers.getId());
	}

	@Test
	void testMgrDeletePricesOther() throws Exception {
		int id = setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findById(id).orElse(null);
		assertThat(foundPricesOthers).isNotNull();
		pricesOthersManager.deleteOther(foundPricesOthers);
		assertThat(priceOthersIoOperationRepository.existsById(id)).isFalse();
	}

	@Test
	void testMgrValidationCodeEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PricesOthers pricesOthers = testPricesOthers.setup(true);
			pricesOthers.setCode("");
			pricesOthersManager.newOther(pricesOthers);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testMgrValidationDescriptionEmpty() throws Exception {
		assertThatThrownBy(() ->
		{
			PricesOthers pricesOthers = testPricesOthers.setup(true);
			pricesOthers.setDescription("");
			pricesOthersManager.newOther(pricesOthers);
		})
			.isInstanceOf(OHDataValidationException.class)
			.has(
				new Condition<Throwable>(
					e -> ((OHServiceException) e).getMessages().size() == 1, "Expecting single validation error")
			);
	}

	@Test
	void testPricesOthersToString() throws Exception {
		PricesOthers pricesOthers = new PricesOthers("TestCode", "TestDescription", true, false, false, true);
		assertThat(pricesOthers).hasToString("TestDescription");
	}

	@Test
	void testPricesOthersEquals() throws Exception {
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
	void testPricesOthersHashCode() throws Exception {
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