/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dicom.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.isf.OHCoreTestCase;
import org.isf.dicom.manager.DicomManagerInterface;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.dicomtype.test.TestDicomType;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class TestSqlDicomManager extends OHCoreTestCase {

	private static TestDicom testFileDicom;
	private static TestDicomType testDicomType;

	@Autowired
	DicomIoOperationRepository dicomIoOperationRepository;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private DicomManagerInterface sqlDicomManager;

	@BeforeClass
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
		System.setProperty("dicom.manager.impl", "SqlDicomManager");
	}

	@Before
	public void setUp() throws OHException, OHDicomException {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		System.clearProperty("dicom.manager.impl");
	}

	@Test
	public void testLoadPatientFiles() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		sqlDicomManager.saveFile(dicomFile);

		FileDicom[] fileDicoms = sqlDicomManager.loadPatientFiles(0);
		assertThat(fileDicoms).hasSize(1);
		fileDicoms = sqlDicomManager.loadPatientFiles(-99);
		assertThat(fileDicoms).isEmpty();
	}

	@Test
	public void testLoadDetails() throws Exception {
		long id = setupTestFileDicom(true);
		FileDicom fileDicom = sqlDicomManager.loadDetails(id, 0, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	public void testLoadDetailsLongObject() throws Exception {
		long id = setupTestFileDicom(true);
		FileDicom fileDicom = sqlDicomManager.loadDetails(new Long(id), 0, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	public void testGetSerieDetail() throws Exception {
		long id = setupTestFileDicom(true);
		Long[] result = sqlDicomManager.getSerieDetail(0, "TestSeriesNumber");
		assertThat(result).isNotEmpty();
	}

	@Test
	public void testExist() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		sqlDicomManager.saveFile(dicomFile);

		boolean fileExits = sqlDicomManager.exist(dicomFile);
		assertThat(fileExits).isTrue();
	}

	@Test
	public void testExistWhenDicomFileDoesNotExist() throws Exception {
		FileDicom dicomFile = new FileDicom();
		boolean fileExits = sqlDicomManager.exist(dicomFile);
		assertThat(fileExits).isFalse();
	}

	@Test
	public void testExistSeriesNumber() throws Exception {
		long id = setupTestFileDicom(true);
		boolean exists = sqlDicomManager.exist(0, "TestSeriesNumber");
		assertThat(exists).isTrue();
	}

	@Test
	public void testExistSeriesNumberDoesNotExist() throws Exception {
		long id = setupTestFileDicom(true);
		boolean exists = sqlDicomManager.exist(0, "SomeOtherTestSeriesNumberThatIsNotThere");
		assertThat(exists).isFalse();
	}

	@Test
	public void testDeleteSerie() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber("SeriesNumber");
		dicomFile.setPatId(2);
		sqlDicomManager.saveFile(dicomFile);
		boolean serieDeleted = sqlDicomManager.deleteSerie(2, "SeriesNumber");
		assertThat(serieDeleted).isTrue();
	}

	private long setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}
}
