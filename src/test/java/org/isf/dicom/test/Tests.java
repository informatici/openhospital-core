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
package org.isf.dicom.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.aspectj.util.FileUtil;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.isf.OHCoreTestCase;
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
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class Tests extends OHCoreTestCase {

	public static final int PATIENT_ID = 0;
	public static final Long _4M = new Long(4194304);
	final String STUDY_DATE = "Mon Jan 01 10:22:33 AST 2001";
	final String SERIES_DATE = "Mon May 14 10:22:33 AST 2007";
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
	private FileDicom dicomFile;

	@BeforeClass
	public static void setUpClass() throws ParseException {
		testFileDicom = new TestDicom();
		testDicomType = new TestDicomType();
	}

	@Before
	public void setUp() throws OHException, OHDicomException {
		cleanH2InMemoryDb();
		Context.setApplicationContext(applicationContext);
		fileSystemDicomManager = new FileSystemDicomManager(_getDicomProperties());
		DicomType dicomType;
		dicomType = testDicomType.setup(true);
		dicomFile = testFileDicom.setup(dicomType, true);
	}

	private Properties _getDicomProperties() {
		Properties properties = new Properties();
		properties.setProperty("dicom.manager.impl", "FileSystemDicomManager");
		properties.setProperty("dicom.storage.filesystem", "rsc-test/dicom");
		return properties;
	}

	private static void _deleteSavedDicomFile() {
		FileUtil.deleteContents(new File("rsc-test/dicom/0"));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
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
	public void testIoLoadFileDicom() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		FileDicom dicom = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		FileDicom dicom2 = dicomIoOperation.loadDetails(new Long(foundFileDicom.getIdFile()), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
		assertThat(dicom2.getDicomInstanceUID()).isEqualTo(dicom.getDicomInstanceUID());
		assertThat(dicom.getDicomSeriesDescription()).isEqualTo(foundFileDicom.getDicomSeriesDescription());
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
	public void testIoSaveFile() throws Exception {
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		foundFileDicom.setDicomSeriesDescription("Update");
		dicomIoOperation.saveFile(foundFileDicom);
		FileDicom updateFileDicom = dicomIoOperationRepository.findOne(code);
		assertThat(updateFileDicom.getDicomSeriesDescription()).isEqualTo("Update");
	}

	@Test
	public void testSaveFile() throws Exception {
		fileSystemDicomManager.saveFile(dicomFile);
		_checkIfExists(dicomFile);
	}

	private void _checkIfExists(FileDicom dicomFile) throws IOException {
		File dicomFileDir = new File("rsc-test/dicom/0/TestSeriesNumber");
		FileReader fr = new FileReader(new File(dicomFileDir, "1.properties"));
		Properties dicomProperties = new Properties();
		dicomProperties.load(fr);
		fr.close();
		assertThat(dicomFileDir.listFiles()).hasSize(3);
		assertThat(dicomProperties.getProperty("dicomInstanceUID")).isEqualTo("TestInteanceUid");
	}

	@Test
	public void testLoadPatientFiles() throws Exception {
		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(PATIENT_ID);
		assertThat(fileDicoms).isNotEmpty();
	}

	@Test
	public void testLoadDetails() throws Exception {
		FileDicom fileDicom = fileSystemDicomManager.loadDetails(2, 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	public void testGetSerieDetail() throws Exception {
		Long[] result = fileSystemDicomManager.getSerieDetail(PATIENT_ID, "TestSeriesNumber");
		assertThat(result).isNotEmpty();
	}

	@Test
	public void tesExist() throws Exception {
		boolean fileExits = fileSystemDicomManager.exist(dicomFile);
		assertThat(fileExits).isTrue();
	}

	@Test
	public void testExistWhenDicomFileNoExist() throws OHServiceException {
		FileDicom dicomFile = new FileDicom();
		boolean fileExits = fileSystemDicomManager.exist(dicomFile);
		assertThat(fileExits).isFalse();
	}

	@Test
	public void testDeleteSerie() throws Exception {
		int idPaziente = 2;
		DicomType dicomType;
		dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber("SeriesNumber");
		dicomFile.setPatId(idPaziente);
		fileSystemDicomManager.saveFile(dicomFile);
		boolean serieDeleted = fileSystemDicomManager.deleteSerie(idPaziente, "SeriesNumber");
		assertThat(serieDeleted).isTrue();
	}

	@Test
	public void testDicomManagerFactoryGetManager() throws Exception {
		DicomManagerInterface manager = DicomManagerFactory.getManager();
		assertThat(manager).isInstanceOf(FileSystemDicomManager.class);
	}

	@Test
	public void testDicomManagerFactoryGetMaxDicomSize() throws Exception {
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("4M");
	}

	@Test
	public void testDicomManagerFactoryGetMaxDicomSizeLong() throws Exception {
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
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		assertThat(dicomFile.getFileName()).isEqualTo("case3c_002.dcm");
		assertThat(dicomFile.getDicomInstitutionName()).isEqualTo("Anonymized Hospital");
		assertThat(dicomFile.getDicomStudyDescription()).isEqualTo("MRT Oberbauch");
	}

	@Test
	public void testSourceFilesLoadDicomWhenImageFormatIsJpeg() throws Exception {
		File file = _getFile("image.0007.jpg");
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		String fileName = dicomFile.getFileName();
		assertThat(fileName).isEqualTo("image.0007.jpg");
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
		assertThat(count).isGreaterThan(0);
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

	private void _checkFileDicomIntoDb(long code) throws OHException {
		FileDicom foundFileDicom = dicomIoOperationRepository.findOne(code);
		testFileDicom.check(foundFileDicom);
	}
}
