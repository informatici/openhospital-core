package org.isf.pricesothers.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.isf.pricesothers.model.PricesOthers;
import org.isf.pricesothers.service.PriceOthersIoOperationRepository;
import org.isf.pricesothers.service.PriceOthersIoOperations;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
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
		assertEquals(foundPricesOthers.getDescription(), result.get(0).getDescription());
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
		assertTrue(result);
		assertEquals("Update", updatePricesOthers.getDescription());
	}

	@Test
	public void testIoNewPricesOthers() throws OHException, OHServiceException {
		// given:
		PricesOthers pricesOthers = testPricesOthers.setup(true);

		// when:
		boolean result = otherIoOperation.newOthers(pricesOthers);

		// then:
		assertTrue(result);
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
		assertTrue(result);
		assertFalse(repository.existsById(id));
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