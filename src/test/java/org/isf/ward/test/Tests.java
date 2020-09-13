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
package org.isf.ward.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.service.WardIoOperations;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests
{
	private static DbJpaUtil jpa;
	private static TestWard testWard;
	private static TestWardContext testWardContext;

    @Autowired
    WardIoOperations wardIoOperation;
    @Autowired
    WardIoOperationRepository wardIoOperationRepository;
	
	@BeforeClass
    public static void setUpClass() {
    	jpa = new DbJpaUtil();
    	testWard = new TestWard();
    	testWardContext = new TestWardContext();
    }

    @Before
    public void setUp() throws OHException {
        jpa.open();

        _saveContext();
    }

    @After
    public void tearDown() throws Exception {
        _restoreContext();

        jpa.flush();
        jpa.close();
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException {

    }

	@Test
	public void testWardGets() {
		try {
			String code = _setupTestWard(false);
			_checkWardIntoDb(code);
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testWardSets() throws OHException {
		try {
			String code = _setupTestWard(true);
			_checkWardIntoDb(code);
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetWardsNoMaternity() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findById(code).get();

			// when:
			ArrayList<Ward> wards = wardIoOperation.getWardsNoMaternity();

			// then:
			assertThat(wards.get(wards.size() - 1).getDescription()).isEqualTo(foundWard.getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoGetWards() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findById(code).get();

			// when:
			ArrayList<Ward> wards = wardIoOperation.getWards(code);			

			// then:
			assertThat(wards.get(0).getDescription()).isEqualTo(foundWard.getDescription());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoNewWard() {
		try {
			Ward ward = testWard.setup(true);
			boolean result = wardIoOperation.newWard(ward);

			assertThat(result).isTrue();
			_checkWardIntoDb(ward.getCode());
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateWard() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findById(code).get();
			foundWard.setDescription("Update");

			// when:
			boolean result = wardIoOperation.updateWard(foundWard);
			Ward updateWard = wardIoOperationRepository.findById(code).get();

			// then:
			assertThat(result).isTrue();
			assertThat(updateWard.getDescription()).isEqualTo("Update");
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoUpdateWardNoCodePresent() {
		try {
			Ward ward = testWard.setup(true);
			ward.setCode("X");
			boolean result = wardIoOperation.updateWard(ward);

			assertThat(result).isTrue();
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}
	
	@Test
	public void testIoDeleteWard() {
		try {
			// given:
			String code = _setupTestWard(false);
			Ward foundWard = wardIoOperationRepository.findById(code).get();

			// when:
			boolean result = wardIoOperation.deleteWard(foundWard);

			// then:
			assertThat(result).isTrue();
			result = wardIoOperation.isCodePresent(code);
			assertThat(result).isFalse();
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoIsCodePresent() {
		try {
			String code = _setupTestWard(false);
			boolean result = wardIoOperation.isCodePresent(code);

			assertThat(result).isTrue();
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testIoIsCodePresentFalse() {
		boolean result = false;

		try {
			result = wardIoOperation.isCodePresent("X");
			assertThat(result).isFalse();
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
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

			assertThat(result).isTrue();
		} catch (Exception e) {
			e.printStackTrace();		
			fail();
		}
	}

	@Test
	public void testFindWard()
	{
		String code = "";
		Ward result;

		try
		{
			code = _setupTestWard(false);
			result = wardIoOperation.findWard(code);

			assertThat(result).isNotNull();
			assertThat(result.getCode()).isEqualTo(code);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
	}

	private void _saveContext() throws OHException
    {
		testWardContext.saveAll(jpa);
    }

    private void _restoreContext() throws OHException
    {
		testWardContext.deleteNews(jpa);
    }

	private String _setupTestWard(
			boolean usingSet) throws OHException
	{
		Ward ward;


    	jpa.beginTransaction();
    	ward = testWard.setup(usingSet);
		jpa.persist(ward);
    	jpa.commitTransaction();

		return ward.getCode();
	}
		
	private void  _checkWardIntoDb(String code) throws OHException {
		Ward foundWard = wardIoOperationRepository.findById(code).get();
		testWard.check(foundWard);
	}
}