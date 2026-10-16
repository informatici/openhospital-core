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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Vector;

import org.aspectj.util.FileUtil;
import org.isf.OHCoreTestCase;
import org.isf.dicom.TestDicom;
import org.isf.dicom.manager.FileSystemDicomManager.DicomDateComparator;
import org.isf.dicom.manager.FileSystemDicomManager.DicomTypeDateComparator;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.time.TimeTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;

class FileSystemDicomManagerTest extends OHCoreTestCase {

	public static final Long _4M = 4194304L;
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

	private DicomManagerInterface fileSystemDicomManager;

	@BeforeAll
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
	}

	@BeforeEach
	void setUp() throws OHException, OHDicomException {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
		fileSystemDicomManager = new FileSystemDicomManager(getDicomProperties());
	}

	@AfterEach
	void tearDown() throws Exception {
		fileSystemDicomManager = null;
	}

	private Properties getDicomProperties() {
		Properties properties = new Properties();
		properties.setProperty("dicom.manager.impl", "FileSystemDicomManager");
		properties.setProperty("dicom.storage.filesystem", "rsc-test/dicom");
		return properties;
	}

	@Test
	void constructor_shouldThrow_whenStorageDirectoryPropertyIsMissing() throws Exception {
		assertThatThrownBy(() ->
		{
			Properties properties = new Properties();
			properties.setProperty("dicom.manager.impl", "FileSystemDicomManager");
			new FileSystemDicomManager(properties);
		})
				.isInstanceOf(OHDicomException.class);
	}

	@Test
	void saveFile_shouldWritePropertiesDataAndThumbnailFiles() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		fileSystemDicomManager.saveFile(dicomFile);

		File dicomFileDir = new File("rsc-test/dicom/0/TestSeriesNumber");
		FileReader fr = new FileReader(new File(dicomFileDir, "1.properties"));
		Properties dicomProperties = new Properties();
		dicomProperties.load(fr);
		fr.close();
		assertThat(dicomFileDir.listFiles()).hasSize(3);
		assertThat(dicomProperties.getProperty("dicomInstanceUID")).isEqualTo("TestInstanceUid");

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void saveFile_shouldSaveFile_whenSeriesNumberIsNull() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber(null);
		fileSystemDicomManager.saveFile(dicomFile);

		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(0);
		assertThat(fileDicoms).hasSize(1);
		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void saveFile_shouldSaveFile_whenInstanceUidIsNull() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomInstanceUID(null);
		fileSystemDicomManager.saveFile(dicomFile);

		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(0);
		assertThat(fileDicoms).hasSize(1);
		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void loadPatientFiles_shouldReturnFilesOfPatient() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		fileSystemDicomManager.saveFile(dicomFile);

		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(0);
		assertThat(fileDicoms).hasSize(1);
		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void loadDetails_shouldReturnStoredFile() throws Exception {
		FileDicom fileDicom = fileSystemDicomManager.loadDetails(2, 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void loadDetails_shouldReturnStoredFile_whenIdFileIsLongObject() throws Exception {
		FileDicom fileDicom = fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void loadDetails_shouldReturnNull_whenSeriesNumberIsNull() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, null)).isNull();
	}

	@Test
	void loadDetails_shouldReturnNull_whenSeriesNumberIsBlank() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "     ")).isNull();
	}

	@Test
	void loadDetails_shouldReturnNull_whenIdFileIsNull() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(null, 1, "TestSeriesNumber")).isNull();
	}

	@Test
	void loadDetails_shouldReturnNull_whenSeriesNumberIsNullString() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "NuLl")).isNull();
	}

	@Test
	void getSeriesDetail_shouldReturnFileIdsOfSeries() throws Exception {
		Long[] result = fileSystemDicomManager.getSeriesDetail(1, "TestSeriesNumber");
		assertThat(result).isNotEmpty();
	}

	@Test
	void getSeriesDetail_shouldReturnNull_whenSeriesNumberIsNull() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, null)).isNull();
	}

	@Test
	void getSeriesDetail_shouldReturnNull_whenSeriesNumberIsBlank() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, "     ")).isNull();
	}

	@Test
	void getSeriesDetail_shouldReturnNull_whenSeriesNumberIsNullString() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, "nuLL")).isNull();
	}

	@Test
	void exist_shouldReturnTrue_whenFileIsSaved() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		fileSystemDicomManager.saveFile(dicomFile);

		boolean exists = fileSystemDicomManager.exist(dicomFile);
		assertThat(exists).isTrue();

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void exist_shouldReturnFalse_whenFileIsNotSaved() throws OHServiceException {
		FileDicom dicomFile = new FileDicom();
		boolean exists = fileSystemDicomManager.exist(dicomFile);
		assertThat(exists).isFalse();
	}

	@Test
	void deleteSeries_shouldDeleteSeries() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber("SeriesNumber");
		dicomFile.setPatId(2);
		fileSystemDicomManager.saveFile(dicomFile);
		fileSystemDicomManager.deleteSeries(2, "SeriesNumber");
		assertThat(fileSystemDicomManager.exist(2, "SeriesNumber")).isFalse();

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void deleteSeries_shouldThrow_whenSeriesNumberIsNull() throws Exception {
		assertThatThrownBy(() ->
				fileSystemDicomManager.deleteSeries(1, null))
			.isInstanceOf(OHDicomException.class);
	}

	@Test
	void deleteSeries_shouldThrow_whenSeriesNumberIsBlank() throws Exception {
		assertThatThrownBy(() ->
			fileSystemDicomManager.deleteSeries(1, "     "))
			.isInstanceOf(OHDicomException.class);
	}

	@Test
	void deleteSeries_shouldThrow_whenSeriesNumberIsNullString() throws Exception {
		assertThatThrownBy(() ->
			fileSystemDicomManager.deleteSeries(1, "nuLL"))
			.isInstanceOf(OHDicomException.class);
	}

	@Test
	void dicomDateComparator_shouldSortByStudyDate() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);

		FileDicom dicomFile2 = testFileDicom.setup(dicomType, false);
		FileDicom dicomFile3 = testFileDicom.setup(dicomType, false);

		LocalDateTime thirtyDays = TimeTools.getNow().plusDays(30);
		LocalDateTime sixtyDays = TimeTools.getNow().plusDays(60);
		LocalDateTime now = TimeTools.getNow();

		dicomFile.setDicomStudyDate(thirtyDays);
		dicomFile2.setDicomStudyDate(sixtyDays);
		dicomFile3.setDicomStudyDate(now);

		Vector<FileDicom> fileDicomVector = new Vector<>(3);
		fileDicomVector.addElement(dicomFile);
		fileDicomVector.addElement(dicomFile2);
		fileDicomVector.addElement(dicomFile3);

		FileDicom[] dicomArray = new FileDicom[fileDicomVector.size()];

		fileDicomVector.sort(new DicomDateComparator());
		FileDicom[] fileDicomSorted = new FileDicom[3];
		fileDicomVector.copyInto(fileDicomSorted);

		assertThat(fileDicomSorted[0].getDicomStudyDate()).isEqualTo(now);
		assertThat(fileDicomSorted[1].getDicomStudyDate()).isEqualTo(thirtyDays);
		assertThat(fileDicomSorted[2].getDicomStudyDate()).isEqualTo(sixtyDays);
	}

	@Test
	void dicomDateComparator_shouldSortNullStudyDateFirst() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);

		FileDicom dicomFile2 = testFileDicom.setup(dicomType, false);
		FileDicom dicomFile3 = testFileDicom.setup(dicomType, false);

		LocalDateTime thirtyDays = TimeTools.getNow().plusDays(30);
		LocalDateTime now = TimeTools.getNow();

		dicomFile.setDicomStudyDate(thirtyDays);
		dicomFile2.setDicomStudyDate(null);
		dicomFile3.setDicomStudyDate(now);

		Vector<FileDicom> fileDicomVector = new Vector<>(3);
		fileDicomVector.addElement(dicomFile);
		fileDicomVector.addElement(dicomFile2);
		fileDicomVector.addElement(dicomFile3);

		FileDicom[] dicomArray = new FileDicom[fileDicomVector.size()];

		fileDicomVector.sort(new DicomDateComparator());
		FileDicom[] fileDicomSorted = new FileDicom[3];
		fileDicomVector.copyInto(fileDicomSorted);

		assertThat(fileDicomSorted[0].getDicomStudyDate()).isNull();
		assertThat(fileDicomSorted[1].getDicomStudyDate()).isEqualTo(now);
		assertThat(fileDicomSorted[2].getDicomStudyDate()).isEqualTo(thirtyDays);
	}

	@Test
	void dicomTypeDateComparator_shouldSortByStudyDate_whenTypeDescriptionsAreEqual() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);

		FileDicom dicomFile2 = testFileDicom.setup(dicomType, false);
		FileDicom dicomFile3 = testFileDicom.setup(dicomType, false);

		LocalDateTime thirtyDays = TimeTools.getNow().plusDays(30);
		LocalDateTime sixtyDays = TimeTools.getNow().plusDays(60);
		LocalDateTime now = TimeTools.getNow();

		dicomFile.setDicomStudyDate(thirtyDays);
		dicomFile2.setDicomStudyDate(sixtyDays);
		dicomFile3.setDicomStudyDate(now);

		Vector<FileDicom> fileDicomVector = new Vector<>(3);
		fileDicomVector.addElement(dicomFile);
		fileDicomVector.addElement(dicomFile2);
		fileDicomVector.addElement(dicomFile3);

		FileDicom[] dicomArray = new FileDicom[fileDicomVector.size()];

		fileDicomVector.sort(new DicomTypeDateComparator());
		FileDicom[] fileDicomSorted = new FileDicom[3];
		fileDicomVector.copyInto(fileDicomSorted);

		assertThat(fileDicomSorted[0].getDicomStudyDate()).isEqualTo(now);
		assertThat(fileDicomSorted[1].getDicomStudyDate()).isEqualTo(thirtyDays);
		assertThat(fileDicomSorted[2].getDicomStudyDate()).isEqualTo(sixtyDays);
	}

	@Test
	void dicomTypeDateComparator_shouldSortByStudyDate_whenADicomTypeIsNull() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);

		FileDicom dicomFile2 = testFileDicom.setup(dicomType, false);
		FileDicom dicomFile3 = testFileDicom.setup(dicomType, false);

		LocalDateTime thirtyDays = TimeTools.getNow().plusDays(30);
		LocalDateTime sixtyDays = TimeTools.getNow().plusDays(60);
		LocalDateTime now = TimeTools.getNow();

		dicomFile.setDicomStudyDate(thirtyDays);
		dicomFile2.setDicomStudyDate(sixtyDays);
		dicomFile3.setDicomStudyDate(now);

		dicomFile2.setDicomType(null);

		Vector<FileDicom> fileDicomVector = new Vector<>(3);
		fileDicomVector.addElement(dicomFile);
		fileDicomVector.addElement(dicomFile2);
		fileDicomVector.addElement(dicomFile3);

		FileDicom[] dicomArray = new FileDicom[fileDicomVector.size()];

		fileDicomVector.sort(new DicomTypeDateComparator());
		FileDicom[] fileDicomSorted = new FileDicom[3];
		fileDicomVector.copyInto(fileDicomSorted);

		assertThat(fileDicomSorted[0].getDicomStudyDate()).isEqualTo(now);
		assertThat(fileDicomSorted[1].getDicomStudyDate()).isEqualTo(thirtyDays);
		assertThat(fileDicomSorted[2].getDicomStudyDate()).isEqualTo(sixtyDays);
	}

	@Test
	void dicomTypeDateComparator_shouldSortByTypeDescriptionFirst() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);

		DicomType dicomType2 = testDicomType.setup(false);
		FileDicom dicomFile2 = testFileDicom.setup(dicomType2, false);
		DicomType dicomType3 = testDicomType.setup(false);
		FileDicom dicomFile3 = testFileDicom.setup(dicomType3, false);

		LocalDateTime thirtyDays = TimeTools.getNow().plusDays(30);
		LocalDateTime sixtyDays = TimeTools.getNow().plusDays(60);
		LocalDateTime now = TimeTools.getNow();

		dicomFile.setDicomStudyDate(thirtyDays);
		dicomFile2.setDicomStudyDate(sixtyDays);
		dicomFile3.setDicomStudyDate(now);

		dicomFile3.getDicomType().setDicomTypeDescription("someDifferentDescription");

		Vector<FileDicom> fileDicomVector = new Vector<>(3);
		fileDicomVector.addElement(dicomFile);
		fileDicomVector.addElement(dicomFile2);
		fileDicomVector.addElement(dicomFile3);

		FileDicom[] dicomArray = new FileDicom[fileDicomVector.size()];

		fileDicomVector.sort(new DicomTypeDateComparator());
		FileDicom[] fileDicomSorted = new FileDicom[3];
		fileDicomVector.copyInto(fileDicomSorted);

		assertThat(fileDicomSorted[0].getDicomStudyDate()).isEqualTo(thirtyDays);
		assertThat(fileDicomSorted[1].getDicomStudyDate()).isEqualTo(sixtyDays);
		assertThat(fileDicomSorted[2].getDicomStudyDate()).isEqualTo(now);
	}

	private static void cleanupDicomFiles(int patientId) {
		FileSystemUtils.deleteRecursively(new File("rsc-test/dicom/" + patientId));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}

}
