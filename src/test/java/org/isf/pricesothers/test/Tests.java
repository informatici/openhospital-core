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

import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.isf.pricesothers.service.PriceOthersIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class Tests {
	private static TestPricesOthers testPricesOthers;

	@Autowired
	PriceOthersIoOperations otherIoOperation;
	@Autowired
	PriceOthersIoOperationRepository repository;

	@BeforeClass
	public static void setUpClass() {
		testPricesOthers = new TestPricesOthers();
	}

	@Before
	@After
	public void setUp() throws OHException {
		repository.deleteAll();
	}

	@Test
	public void testPricesOthersGets() throws OHException {
		// given:
		int id = _setupTestPricesOthers(false);

		// then:
		_checkPricesOthersIntoDb(id);
	}

	@Test
	public void testPricesOthersSets() throws OHException {
		// given:
		int id = _setupTestPricesOthers(true);

		// then:
		_checkPricesOthersIntoDb(id);
	}

	@Test
	public void testIoGetPricesOthers() throws OHException, OHServiceException {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = repository.findById(id).get();

		// when:
		ArrayList<PricesOthers> result = otherIoOperation.getOthers();

		// then:
		assertThat(result.get(0).getDescription()).isEqualTo(foundPricesOthers.getDescription());
	}

	@Test
	public void testIoUpdatePricesOthers() throws OHServiceException, OHException {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = repository.findById(id).get();
		foundPricesOthers.setDescription("Update");

		// when:
		boolean result = otherIoOperation.updateOther(foundPricesOthers);
		PricesOthers updatePricesOthers = repository.findById(id).get();

		// then:
		assertThat(result).isTrue();
		assertThat(updatePricesOthers.getDescription()).isEqualTo("Update");
	}

	@Test
	public void testIoNewPricesOthers() throws OHException, OHServiceException {
		// given:
		PricesOthers pricesOthers = testPricesOthers.setup(true);

		// when:
		boolean result = otherIoOperation.newOthers(pricesOthers);

		// then:
		assertThat(result).isTrue();
		_checkPricesOthersIntoDb(pricesOthers.getId());
	}

	@Test
	public void testIoDeletePricesOthers() throws OHException, OHServiceException {
		// given:
		int id = _setupTestPricesOthers(false);
		PricesOthers foundPricesOthers = repository.findById(id).get();

		// when:
		boolean result = otherIoOperation.deleteOthers(foundPricesOthers);

		// then:
		assertThat(result).isTrue();
		assertThat(repository.existsById(id)).isFalse();
	}

	private int _setupTestPricesOthers(boolean usingSet) throws OHException {
		PricesOthers pricesOthers = testPricesOthers.setup(usingSet);
		repository.save(pricesOthers);
		return pricesOthers.getId();
	}

	private void _checkPricesOthersIntoDb(int id) {
		PricesOthers foundPricesOthers = repository.findById(id).get();
		testPricesOthers.check(foundPricesOthers);
	}
}