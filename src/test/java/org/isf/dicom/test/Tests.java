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

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;

import org.aspectj.util.FileUtil;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
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
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.service.DicomTypeIoOperationRepository;
import org.isf.dicomtype.test.TestDicomType;
import org.isf.menu.manager.Context;
import org.isf.utils.exception.OHException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;

public class Tests extends OHCoreTestCase {

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

	@BeforeClass
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
	}

	@Before
	public void setUp() throws Exception {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
	}

	@Test
	public void testFileDicomGets() throws Exception {
		long code = _setupTestFileDicom(false);
		_checkFileDicomIntoDb(code);
	}

	@Test
	public void testFileDicomSets() throws Exception {
		long code = _setupTestFileDicom(true);
		_checkFileDicomIntoDb(code);
	}

	@Test
	public void testIoGetSerieDetail() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		Long[] dicoms = dicomIoOperation.getSerieDetail(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicoms).hasSize(1);
	}

	@Test
	public void testIoDeleteSerie() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		boolean result = dicomIoOperation.deleteSerie(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());

		assertThat(result).isTrue();
		result = dicomIoOperation.isCodePresent(code);
		assertThat(result).isFalse();
	}

	@Test
	public void testIoLoadDetails() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		FileDicom dicom = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		FileDicom dicom2 = dicomIoOperation.loadDetails(new Long(foundFileDicom.getIdFile()), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicom2.getDicomInstanceUID()).isEqualTo(dicom.getDicomInstanceUID());
		assertThat(dicom.getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	public void testIoLoadDetailsNullIdFile() throws Exception {
		assertThat(dicomIoOperation.loadDetails(null, 1, "someSeriesNumber")).isNull();
	}

	@Test
	public void testIoLoadPatientFiles() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		FileDicom[] dicoms = dicomIoOperation.loadPatientFiles(foundFileDicom.getPatId());
		assertThat(dicoms[0].getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
	}

	@Test
	public void testIoExist() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		boolean result = dicomIoOperation.exist(foundFileDicom);
		assertThat(result).isTrue();
	}

	@Test
	public void testIoSaveFileUpate() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		foundFileDicom.setDicomSeriesDescription("Update");
		dicomIoOperation.saveFile(foundFileDicom);
		FileDicom updateFileDicom = dicomIoOperationRepository.findOne(code);
		assertThat(updateFileDicom.getDicomSeriesDescription()).isEqualTo("Update");
	}

	@Test
	public void testDicomManagerFactoryGetManager() throws Exception {
		DicomManagerInterface manager = DicomManagerFactory.getManager();
		assertThat(manager).isInstanceOf(FileSystemDicomManager.class);
	}

	@Test
	public void testDicomManagerFactoryGetMaxDicomSizeDefault() throws Exception {
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("4M");
	}

	@Test
	public void testDicomManagerFactoryGetMaxDicomSizeLongDefault() throws Exception {
		Long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertThat(maxDicomSize).isEqualTo(_4M);
	}

	@Test
	public void testSourceFilesGenerateSeriesNumber() throws Exception {
		String seriesNumber = SourceFiles.generateSeriesNumber(PATIENT_ID);
		assertThat(seriesNumber).isNotEmpty();
	}

	@Test
	public void testSourceFilesLoadDicom() throws Exception {
		File file = _getFile("case3c_002.dcm");
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		assertThat(dicomFile.getFileName()).isEqualTo("case3c_002.dcm");
		assertThat(dicomFile.getDicomInstitutionName()).isEqualTo("Anonymized Hospital");
		assertThat(dicomFile.getDicomStudyDescription()).isEqualTo("MRT Oberbauch");

		_cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	public void testSourceFilesLoadDicomWhenImageFormatIsJpeg() throws Exception {
		File file = _getFile("image.0007.jpg");
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		String fileName = dicomFile.getFileName();
		assertThat(fileName).isEqualTo("image.0007.jpg");

		_cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	public void testSourceFilesPreloadDicom() throws Exception {
		File file = _getFile("case3c_002.dcm");
		Date expectedStudyDate = _getDicomObject(file).getDate(Tag.StudyDate, Tag.StudyTime);
		Date expectedSeriesDate = _getDicomObject(file).getDate(Tag.SeriesDate, Tag.SeriesTime);
		FileDicom dicomFile = SourceFiles.preLoadDicom(file, 1);
		assertThat(dicomFile.getFileName()).isEqualTo("case3c_002.dcm");
		assertThat(dicomFile.getFrameCount()).isEqualTo(1);
		assertThat(_areDatesEquals(expectedStudyDate, dicomFile.getDicomStudyDate())).isTrue();
		assertThat(_areDatesEquals(expectedSeriesDate, dicomFile.getDicomSeriesDate())).isTrue();
	}

	private DicomObject _getDicomObject(File sourceFile) throws Exception {
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
		ImageReader reader = iter.next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
		reader.setInput(imageInputStream, false);
		DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
		return dicomStreamMetaData.getDicomObject();
	}

	private boolean _areDatesEquals(Date date, Date date2) {
		return date.compareTo(date2) == 0;
	}

	@Test
	public void testSourceFilesCountFiles() throws Exception {
		File file = _getFile("dicomdir");
		int count = SourceFiles.countFiles(file, 1);
		assertThat(count).isPositive();
	}

	@Ignore
	// Reason ignored when running CI it generates this error:
	//    java.awt.HeadlessException:
	// 	  No X11 DISPLAY variable was set, but this program performed an operation which requires it.
	@Test
	public void testSourceFilesConstructorDirectoryNumberOfFiles() throws Exception {
		ThumbnailViewGui thumbnailViewGui = new ThumbnailViewGui();
		thumbnailViewGui.initialize();
		SourceFiles sourceFiles = new SourceFiles(new FileDicom(), new File("src/test/resources/org/isf/dicom/test/dicomdir/"), 2, 1, thumbnailViewGui,
				new DicomLoader(1, new JFrame()));
		assertThat(sourceFiles).isNotNull();
		assertThat(sourceFiles.working()).isTrue();
		while (sourceFiles.working()) {
			Thread.sleep(2000);
		}
		assertThat(sourceFiles.getLoaded()).isEqualTo(1);
		_cleanupDicomFiles(2);
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
	public void testFileDicomEquals() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);

		assertThat(fileDicom.equals(fileDicom)).isTrue();
		assertThat(fileDicom)
				.isNotNull()
				.isNotEqualTo("someString");

		FileDicom fileDicom2 = testFileDicom.setup(dicomType, true);
		fileDicom2.setIdFile(99L);
		assertThat(fileDicom).isNotEqualTo(fileDicom2);

		fileDicom2.setIdFile(fileDicom.getIdFile());
		assertThat(fileDicom).isEqualTo(fileDicom2);
	}

	@Test
	public void testFileDicomHashCode() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		// compute value
		int hashCode = fileDicom.hashCode();
		assertThat(hashCode).isEqualTo(23 * 133 + fileDicom.getIdFile());
		// used computed value
		assertThat(fileDicom.hashCode()).isEqualTo(hashCode);
	}

	@Test
	public void testFileDicomGetDicomType() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom fileDicom = testFileDicom.setup(dicomType, true);
		assertThat(fileDicom.getDicomType()).isEqualTo(dicomType);
	}

	@Test
	public void testFileDicomGetThumbnailasImage() throws Exception {
		DicomType dicomType = testDicomType.setup(false);
		FileDicom fileDicom = testFileDicom.setup(dicomType, false);
		assertThat(fileDicom.getDicomThumbnailAsImage()).isNull();
	}

	private static void _cleanupDicomFiles(int patientId) {
		FileSystemUtils.deleteRecursively(new File("rsc-test/dicom/" + patientId));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}

	private File _getFile(String fileName) {
		return new File(getClass().getResource(fileName).getFile());
	}

	private long _setupTestFileDicom(boolean usingSet) throws OHException {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicom = testFileDicom.setup(dicomType, usingSet);
		dicomTypeIoOperationRepository.saveAndFlush(dicomType);
		dicomIoOperationRepository.saveAndFlush(dicom);
		return dicom.getIdFile();
	}

	private void _checkFileDicomIntoDb(long code) throws Exception {
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		testFileDicom.check(foundFileDicom);
	}
}
