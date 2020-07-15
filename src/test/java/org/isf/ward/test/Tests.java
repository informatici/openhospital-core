package org.isf.ward.test;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.service.WardIoOperations;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests {
	private static TestWard testWard;

    @Autowired
    WardIoOperations wardIoOperation;
    @Autowired
    WardIoOperationRepository wardIoOperationRepository;
	
	@BeforeClass
    public static void setUpClass() {
    	testWard = new TestWard();
    }

    @Before
    public void setUp() {
		wardIoOperationRepository.deleteAll();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException {
    	return;
    }

	@Test
	public void testWardGets() {
		try {
			String code = _setupTestWard(false);
			_checkWardIntoDb(code);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}
	
	@Test
	public void testWardSets() throws OHException {
		try {
			String code = _setupTestWard(true);
			_checkWardIntoDb(code);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	@Test
	public void testIoGetWardsNoMaternity() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findOne(code);

			// when:
			ArrayList<Ward> wards = wardIoOperation.getWardsNoMaternity();

			// then:
			assertEquals(foundWard.getDescription(), wards.get(wards.size()-1).getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	@Test
	public void testIoGetWards() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findOne(code);

			// when:
			ArrayList<Ward> wards = wardIoOperation.getWards(code);			

			// then:
			assertEquals(foundWard.getDescription(), wards.get(0).getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}
	
	@Test
	public void testIoNewWard() {
		try {
			Ward ward = testWard.setup(true);
			boolean result = wardIoOperation.newWard(ward);

			assertEquals(true, result);
			_checkWardIntoDb(ward.getCode());
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}
	
	@Test
	public void testIoUpdateWard() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findOne(code);
			foundWard.setDescription("Update");

			// when:
			boolean result = wardIoOperation.updateWard(foundWard);
			Ward updateWard = wardIoOperationRepository.findOne(code);

			// then:
			assertEquals(true, result);
			assertEquals("Update", updateWard.getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}
	
	@Test
	public void testIoUpdateWardNoCodePresent() {
		try {
			Ward ward = testWard.setup(true);
			ward.setCode("X");
			boolean result = wardIoOperation.updateWard(ward);

			assertEquals(true, result);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}
	
	@Test
	public void testIoDeleteWard() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findOne(code);

			// when:
			boolean result = wardIoOperation.deleteWard(foundWard);

			// then:
			assertEquals(true, result);
			result = wardIoOperation.isCodePresent(code);			
			assertEquals(false, result);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	@Test
	public void testIoIsCodePresent() {
		try {
			String code = _setupTestWard(false);
			boolean result = wardIoOperation.isCodePresent(code);

			assertEquals(true, result);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	@Test
	public void testIoIsCodePresentFalse() {
		boolean result = false;

		try {
			result = wardIoOperation.isCodePresent("X");
			assertEquals(false, result);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	@Test
	public void testIoIsMaternityPresent() {
		boolean result = false;

		try {
			Ward ward = testWard.setup(false);
			ward.setCode("M");
			wardIoOperationRepository.save(ward);

			result = wardIoOperation.isMaternityPresent();
			
			assertEquals(true, result);
		} catch (Exception e) {
			e.printStackTrace();		
			assertEquals(true, false);
		}
	}

	private String _setupTestWard(boolean usingSet) throws OHException {
		Ward ward = testWard.setup(usingSet);
		wardIoOperationRepository.save(ward);

		return ward.getCode();
	}
		
	private void  _checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findOne(code);
		testWard.check(foundWard);
	}
}