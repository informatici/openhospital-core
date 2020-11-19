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
package org.isf.dicomtype.test;

import org.isf.OHCoreTestCase;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperation;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class Tests extends OHCoreTestCase {

	private static TestDicomType testDicomType;

	@Autowired
	DicomTypeIoOperation dicomTypeIoOperation;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;

	@BeforeClass
	public static void setUpClass() {
		testDicomType = new TestDicomType();
	}

	@Before
	public void setUp() {
		cleanH2InMemoryDb();
	}

	@Test
	public void testDicomTypeGets() throws Exception {
		String code = _setupTestDicomType(false);
		_checkDicomTypeIntoDb(code);
	}

	@Test
	public void testDicomTypeSets() throws Exception {
		String code = _setupTestDicomType(true);
		_checkDicomTypeIntoDb(code);
	}

	private String _setupTestDicomType(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		return dicomType.getDicomTypeID();
	}

	private void _checkDicomTypeIntoDb(String code) throws OHException {
		DicomType foundDicomType = dicomTypeIoOperationRepository.findOne(code);
		testDicomType.check(foundDicomType);
	}
}
