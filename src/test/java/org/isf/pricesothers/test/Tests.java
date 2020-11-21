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
package org.isf.pricesothers.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.isf.OHCoreTestCase;
import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.isf.pricesothers.service.PriceOthersIoOperations;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestPricesOthers testPricesOthers;

	@Autowired
	PriceOthersIoOperations priceOthersIoOperations;
	@Autowired
	PriceOthersIoOperationRepository priceOthersIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testPricesOthers = new TestPricesOthers();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testPricesOthersGets() throws Exception {
		// given:
		int id = _setupTestPricesOthers(false);

		// then:
		_checkPricesOthersIntoDb(id);
	}

	@Test
	public void testPricesOthersSets() throws Exception {
		// given:
		int id = _setupTestPricesOthers(true);

		// then:
		_checkPricesOthersIntoDb(id);
	}

	@Test
	public void testIoGetPricesOthers() throws Exception {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findOne(id);

		// when:
		ArrayList<PricesOthers> result = priceOthersIoOperations.getOthers();

		// then:
		assertThat(result.get(0).getDescription()).isEqualTo(foundPricesOthers.getDescription());
	}

	@Test
	public void testIoUpdatePricesOthers() throws Exception {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findOne(id);
		foundPricesOthers.setDescription("Update");

		// when:
		boolean result = priceOthersIoOperations.updateOther(foundPricesOthers);
		PricesOthers updatePricesOthers = priceOthersIoOperationRepository.findOne(id);

		// then:
		assertThat(result).isTrue();
		assertThat(updatePricesOthers.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewPricesOthers() throws Exception {
		// given:
		PricesOthers pricesOthers = testPricesOthers.setup(true);

		// when:
		boolean result = priceOthersIoOperations.newOthers(pricesOthers);

		// then:
		assertThat(result).isTrue();
		_checkPricesOthersIntoDb(pricesOthers.getId());
	}

	@Test
	public void testIoDeletePricesOthers() throws Exception {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findOne(id);

		// when:
		boolean result = priceOthersIoOperations.deleteOthers(foundPricesOthers);

		// then:
		assertThat(result).isTrue();
		assertThat(priceOthersIoOperationRepository.exists(id)).isFalse();
	}

	private int _setupTestPricesOthers(boolean usingSet) throws Exception {
		PricesOthers pricesOthers = testPricesOthers.setup(usingSet);
		priceOthersIoOperationRepository.saveAndFlush(pricesOthers);
		return pricesOthers.getId();
	}

	private void _checkPricesOthersIntoDb(int id) {
		PricesOthers foundPricesOthers = priceOthersIoOperationRepository.findOne(id);
		testPricesOthers.check(foundPricesOthers);
	}
}