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
package org.isf.dicom.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.isf.OHCoreTestCase;
import org.isf.dicom.TestDicom;
import org.isf.dicom.model.FileDicom;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

class DicomIoOperationsTest extends OHCoreTestCase {

	private static TestDicom testFileDicom;
	private static TestDicomType testDicomType;

	@Autowired
	DicomIoOperations dicomIoOperation;
	@Autowired
	DicomIoOperationRepository dicomIoOperationRepository;
	@Autowired
	DicomTypeIoOperationRepository dicomTypeIoOperationRepository;
	@Autowired
	private ApplicationContext applicationContext;

	@BeforeAll
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
	}

	@BeforeEach
	void setUp() throws Exception {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@Test
	void getSeriesDetail_shouldReturnFileIdsOfSeries() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		Long[] dicoms = dicomIoOperation.getSeriesDetail(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicoms).hasSize(1);
	}

	@Test
	void deleteSeries_shouldDeleteSeries() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		dicomIoOperation.deleteSeries(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicomIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void loadDetails_shouldReturnStoredFile() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		FileDicom dicom = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		FileDicom dicom2 = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicom2.getDicomInstanceUID()).isEqualTo(dicom.getDicomInstanceUID());
		assertThat(dicom.getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	void loadDetails_shouldReturnNull_whenIdFileDoesNotExist() throws Exception {
		assertThat(dicomIoOperation.loadDetails(-1, 1, "someSeriesNumber")).isNull();
	}

	@Test
	void loadPatientFiles_shouldReturnFilesOfPatient() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		FileDicom[] dicoms = dicomIoOperation.loadPatientFiles(foundFileDicom.getPatId());
		assertThat(dicoms[0].getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	void exist_shouldReturnTrue_whenFileIsSaved() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		assertThat(dicomIoOperation.exist(foundFileDicom)).isTrue();
	}

	@Test
	void saveFile_shouldUpdateExistingFile() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		foundFileDicom.setDicomSeriesDescription("Update");
		FileDicom updatedFileDicom = dicomIoOperation.saveFile(foundFileDicom);
		assertThat(updatedFileDicom.getDicomSeriesDescription()).isEqualTo("Update");
	}

	private long setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}

}
