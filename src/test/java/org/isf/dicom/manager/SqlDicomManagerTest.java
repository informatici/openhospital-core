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
package org.isf.dicom.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.isf.OHCoreTestCase;
import org.isf.dicom.TestDicom;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class SqlDicomManagerTest extends OHCoreTestCase {

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

	@BeforeAll
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
		System.setProperty("dicom.manager.impl", "SqlDicomManager");
	}

	@BeforeEach
	void setUp() throws OHException, OHDicomException {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@AfterAll
	static void tearDownClass() throws Exception {
		System.clearProperty("dicom.manager.impl");
	}

	@Test
	void loadPatientFiles_shouldReturnOnlyFilesOfPatient() throws Exception {
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
	void loadDetails_shouldReturnStoredFile() throws Exception {
		long id = setupTestFileDicom(true);
		FileDicom fileDicom = sqlDicomManager.loadDetails(id, 0, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void loadDetails_shouldReturnStoredFile_whenIdFileIsLongObject() throws Exception {
		long id = setupTestFileDicom(true);
		FileDicom fileDicom = sqlDicomManager.loadDetails(Long.valueOf(id), 0, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void getSeriesDetail_shouldReturnFileIdsOfSeries() throws Exception {
		long id = setupTestFileDicom(true);
		Long[] result = sqlDicomManager.getSeriesDetail(0, "TestSeriesNumber");
		assertThat(result).isNotEmpty();
	}

	@Test
	void exist_shouldReturnTrue_whenFileIsSaved() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		sqlDicomManager.saveFile(dicomFile);

		boolean fileExits = sqlDicomManager.exist(dicomFile);
		assertThat(fileExits).isTrue();
	}

	@Test
	void exist_shouldReturnFalse_whenFileIsNotSaved() throws Exception {
		FileDicom dicomFile = new FileDicom();
		boolean fileExits = sqlDicomManager.exist(dicomFile);
		assertThat(fileExits).isFalse();
	}

	@Test
	void exist_shouldReturnTrue_whenSeriesNumberExists() throws Exception {
		long id = setupTestFileDicom(true);
		boolean exists = sqlDicomManager.exist(0, "TestSeriesNumber");
		assertThat(exists).isTrue();
	}

	@Test
	void exist_shouldReturnFalse_whenSeriesNumberDoesNotExist() throws Exception {
		long id = setupTestFileDicom(true);
		boolean exists = sqlDicomManager.exist(0, "SomeOtherTestSeriesNumberThatIsNotThere");
		assertThat(exists).isFalse();
	}

	@Test
	void deleteSeries_shouldDeleteSeries() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber("SeriesNumber");
		dicomFile.setPatId(2);
		sqlDicomManager.saveFile(dicomFile);
		sqlDicomManager.deleteSeries(2, "SeriesNumber");
		assertThat(sqlDicomManager.exist(2, "SeriesNumber")).isFalse();
	}

	private long setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}

}
