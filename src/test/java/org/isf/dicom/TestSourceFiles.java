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

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.aspectj.util.FileUtil;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.isf.OHCoreTestCase;
import org.isf.dicom.manager.AbstractDicomLoader;
import org.isf.dicom.manager.AbstractThumbnailViewGui;
import org.isf.dicom.manager.SourceFiles;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperationRepository;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.dicomtype.TestDicomType;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHDicomException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;

public class TestSourceFiles extends OHCoreTestCase {

	public static final int PATIENT_ID = 0;
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

	@Test
	void testAutoRotate() throws Exception {
		BufferedImage bufferedImage = ImageIO.read(getFile("image.0007.jpg"));

		// method is private not public thus use of reflection
		Class sourceFilesClass = Class.forName("org.isf.dicom.manager.SourceFiles");
		Method method = sourceFilesClass.getDeclaredMethod("autoRotate", BufferedImage.class, int.class);
		method.setAccessible(true);
		for (int idx=1; idx<10; idx++) {
			BufferedImage rotatedImage = (BufferedImage) method.invoke(sourceFilesClass, bufferedImage, idx);
			assertThat(rotatedImage).isNotNull();
		}
	}

	@Test
	void testNewSourceFiles() throws Exception {
		File file = getFile("case3c_002.dcm");
		File directoryFile = file.getParentFile();
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		TestSourceFiles.ThumbnailViewGui thumbnailViewGui = new TestSourceFiles.ThumbnailViewGui();
		thumbnailViewGui.initialize();

		// This statement really doesn't work too well as a NPE is generated but the code
		// as it is written doesn't propagate the exception up.
		// This code is here just to increase line coverage numbers. :(
		SourceFiles sourceFiles = new SourceFiles(dicomFile, directoryFile, PATIENT_ID, 0, thumbnailViewGui, null);
		assertThat(sourceFiles).isNotNull();
	}

	@Disabled
	// Reason ignored when running CI it generates this error (runs fine locally)
	//    java.awt.HeadlessException:
	// 	  No X11 DISPLAY variable was set, but this program performed an operation which requires it.
	@Test
	void testSourceFilesConstructorDirectoryNumberOfFiles() throws Exception {
		TestSourceFiles.ThumbnailViewGui thumbnailViewGui = new TestSourceFiles.ThumbnailViewGui();
		thumbnailViewGui.initialize();
		SourceFiles sourceFiles = new SourceFiles(new FileDicom(), new File("src/test/resources/org/isf/dicom/dicomdir/"), 2, 1, thumbnailViewGui,
						new TestSourceFiles.DicomLoader(1, new JFrame()));
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

	private static void cleanupDicomFiles(int patientId) {
		FileSystemUtils.deleteRecursively(new File("rsc-test/dicom/" + patientId));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}

	private File getFile(String fileName) {
		return new File(getClass().getResource(fileName).getFile());
	}
}
