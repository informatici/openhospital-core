/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dicom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.swing.JFrame;

import org.aspectj.util.FileUtil;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.isf.OHCoreTestCase;
import org.isf.dicom.manager.AbstractDicomLoader;
import org.isf.dicom.manager.AbstractThumbnailViewGui;
import org.isf.dicom.manager.DicomManagerFactory;
import org.isf.dicom.manager.DicomManagerInterface;
import org.isf.dicom.manager.FileSystemDicomManager;
import org.isf.dicom.manager.SourceFiles;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;

class Tests extends OHCoreTestCase {

	public static final int PATIENT_ID = 0;
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
	void testFileDicomGets() throws Exception {
		long code = setupTestFileDicom(false);
		checkFileDicomIntoDb(code);
	}

	@Test
	void testFileDicomSets() throws Exception {
		long code = setupTestFileDicom(true);
		checkFileDicomIntoDb(code);
	}

	@Test
	void testIoGetSeriesDetail() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		Long[] dicoms = dicomIoOperation.getSeriesDetail(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicoms).hasSize(1);
	}

	@Test
	void testIoDeleteSeries() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		dicomIoOperation.deleteSeries(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicomIoOperation.isCodePresent(code)).isFalse();
	}

	@Test
	void testIoLoadDetails() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		FileDicom dicom = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		FileDicom dicom2 = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicom2.getDicomInstanceUID()).isEqualTo(dicom.getDicomInstanceUID());
		assertThat(dicom.getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	void testIoLoadDetailsNullIdFile() throws Exception {
		assertThat(dicomIoOperation.loadDetails(-1, 1, "someSeriesNumber")).isNull();
	}

	@Test
	void testIoLoadPatientFiles() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		FileDicom[] dicoms = dicomIoOperation.loadPatientFiles(foundFileDicom.getPatId());
		assertThat(dicoms[0].getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	void testIoExist() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		assertThat(dicomIoOperation.exist(foundFileDicom)).isTrue();
	}

	@Test
	void testIoSaveFileUpdate() throws Exception {
		long code = setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		foundFileDicom.setDicomSeriesDescription("Update");
		FileDicom updatedFileDicom = dicomIoOperation.saveFile(foundFileDicom);
		assertThat(updatedFileDicom.getDicomSeriesDescription()).isEqualTo("Update");
	}

	@Test
	void testDicomManagerFactoryGetManager() throws Exception {
		DicomManagerInterface manager = DicomManagerFactory.getManager();
		assertThat(manager).isInstanceOf(FileSystemDicomManager.class);
	}

	@Test
	void testDicomManagerFactoryGetMaxDicomSizeDefault() throws Exception {
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("4M");
	}

	@Test
	void testDicomManagerFactoryGetMaxDicomSizeLongDefault() throws Exception {
		Long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertThat(maxDicomSize).isEqualTo(_4M);
	}

	@Test
	void testSourceFilesGenerateSeriesNumber() throws Exception {
		String seriesNumber = SourceFiles.generateSeriesNumber(PATIENT_ID);
		assertThat(seriesNumber).isNotEmpty();
	}

	@Test
	void testSourceFilesLoadDicom() throws Exception {
		File file = getFile("case3c_002.dcm");
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		assertThat(dicomFile.getFileName()).isEqualTo("case3c_002.dcm");
		assertThat(dicomFile.getDicomInstitutionName()).isEqualTo("Anonymized Hospital");
		assertThat(dicomFile.getDicomStudyDescription()).isEqualTo("MRT Oberbauch");

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void testSourceFilesLoadDicomWhenImageFormatIsJpg() throws Exception {
		File file = getFile("image.0007.jpg");
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		String fileName = dicomFile.getFileName();
		assertThat(fileName).isEqualTo("image.0007.jpg");

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void testSourceFilesLoadDicomWhenImageFormatIsBadJpg() throws Exception {
		File file = getFile("BadJPGFile.jpg");
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		assertThatThrownBy(() -> SourceFiles.loadDicom(dicomFile, file, PATIENT_ID))
				.isInstanceOf(OHDicomException.class);
	}

	@Test
	void testSourceFilesPreloadDicom() throws Exception {
		File file = getFile("case3c_002.dcm");
		DicomInputStream dicomInputStream = new DicomInputStream(file);
		Attributes attributes = dicomInputStream.readDataset();
		LocalDateTime expectedSeriesDate = LocalDateTime.ofInstant(attributes.getDate(Tag.SeriesDateAndTime).toInstant(), ZoneId.systemDefault());
		LocalDateTime expectedStudyDate = LocalDateTime.ofInstant(attributes.getDate(Tag.StudyDateAndTime).toInstant(), ZoneId.systemDefault());
		FileDicom dicomFile = SourceFiles.preLoadDicom(file, 1);
		assertThat(dicomFile.getFileName()).isEqualTo("case3c_002.dcm");
		assertThat(dicomFile.getFrameCount()).isEqualTo(1);
		assertThat(expectedStudyDate).isCloseTo(dicomFile.getDicomStudyDate(), within(1, ChronoUnit.SECONDS));
		assertThat(expectedSeriesDate).isCloseTo(dicomFile.getDicomSeriesDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testSourceFilesPreloadDicomTest9() throws Exception {
		// has studydate but not seriesdate
		File file = getFile("test9signed.dcm");
		DicomInputStream dicomInputStream = new DicomInputStream(file);
		Attributes attributes = dicomInputStream.readDataset();
		LocalDateTime expectedStudyDate = LocalDateTime.ofInstant(attributes.getDate(Tag.StudyDateAndTime).toInstant(), ZoneId.systemDefault());
		FileDicom dicomFile = SourceFiles.preLoadDicom(file, 1);
		assertThat(dicomFile.getFileName()).isEqualTo("test9signed.dcm");
		assertThat(dicomFile.getFrameCount()).isEqualTo(1);
		assertThat(expectedStudyDate).isCloseTo(dicomFile.getDicomStudyDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testSourceFilesPreloadDicomTest16() throws Exception {
		// has studydate but not seriesdate
		File file = getFile("test16unsigned.dcm");
		DicomInputStream dicomInputStream = new DicomInputStream(file);
		Attributes attributes = dicomInputStream.readDataset();
		LocalDateTime expectedStudyDate = LocalDateTime.ofInstant(attributes.getDate(Tag.StudyDateAndTime).toInstant(), ZoneId.systemDefault());
		FileDicom dicomFile = SourceFiles.preLoadDicom(file, 1);
		assertThat(dicomFile.getFileName()).isEqualTo("test16unsigned.dcm");
		assertThat(dicomFile.getFrameCount()).isEqualTo(1);
		assertThat(expectedStudyDate).isCloseTo(dicomFile.getDicomStudyDate(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testSourceFilesPreloadBadDicomFormat() throws Exception {
		File file = getFile("BadDicomFile.dcm");
		assertThatThrownBy(() -> new DicomInputStream(file))
				.isInstanceOf(DicomStreamException.class);
	}

	@Test
	void testSourceFilesCountFiles() throws Exception {
		File file = getFile("dicomdir");
		int count = SourceFiles.countFiles(file, 1);
		assertThat(count).isPositive();
	}

	@Disabled
	// Reason ignored when running CI it generates this error (runs fine locally)
	//    java.awt.HeadlessException:
	// 	  No X11 DISPLAY variable was set, but this program performed an operation which requires it.
	@Test
	void testSourceFilesConstructorDirectoryNumberOfFiles() throws Exception {
		ThumbnailViewGui thumbnailViewGui = new ThumbnailViewGui();
		thumbnailViewGui.initialize();
		SourceFiles sourceFiles = new SourceFiles(new FileDicom(), new File("src/test/resources/org/isf/dicom/dicomdir/"), 2, 1, thumbnailViewGui,
				new DicomLoader(1, new JFrame()));
		assertThat(sourceFiles).isNotNull();
		assertThat(sourceFiles.working()).isTrue();
		while (sourceFiles.working()) {
			Thread.sleep(2000);
		}
		assertThat(sourceFiles.getLoaded()).isEqualTo(2);
		cleanupDicomFiles(2);
	}

	class ThumbnailViewGui extends AbstractThumbnailViewGui {

		@Override
		public void initialize() {

		}
	}

	class DicomLoader extends AbstractDicomLoader {

		public DicomLoader(int numfiles, JFrame owner) {
			super(numfiles, owner);
		}

		@Override
		public void setLoaded(int loaded) {
		}
	}

	@Test
	void testFileDicomEquals() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);

		assertThat(fileDicom)
				.isNotNull()
				.isEqualTo(fileDicom)
				.isNotEqualTo("someString");

		FileDicom fileDicom2 = testFileDicom.setup(dicomType, true);
		fileDicom2.setIdFile(99L);
		assertThat(fileDicom).isNotEqualTo(fileDicom2);

		fileDicom2.setIdFile(fileDicom.getIdFile());
		assertThat(fileDicom).isEqualTo(fileDicom2);
	}

	@Test
	void testFileDicomHashCode() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		// compute value
		int hashCode = fileDicom.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + fileDicom.getIdFile());
		// used computed value
		assertThat(fileDicom.hashCode()).isEqualTo(hashCode);
	}

	@Test
	void testFileDicomGetDicomType() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);
		assertThat(fileDicom.getDicomType()).isEqualTo(dicomType);
	}

	@Test
	void testFileDicomGetThumbnailsAsImage() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		assertThat(fileDicom.getDicomThumbnailAsImage()).isNull();
	}

	private static void cleanupDicomFiles(int patientId) {
		FileSystemUtils.deleteRecursively(new File("rsc-test/dicom/" + patientId));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}

	private File getFile(String fileName) {
		return new File(getClass().getResource(fileName).getFile());
	}

	private long setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}

	private void checkFileDicomIntoDb(long code) throws Exception {
		FileDicom foundFileDicom = dicomIoOperationRepository.findById(code).orElse(null);
		assertThat(foundFileDicom).isNotNull();
		testFileDicom.check(foundFileDicom);
	}
}
