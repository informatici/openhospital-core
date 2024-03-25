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

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Properties;

import org.aspectj.util.FileUtil;
import org.isf.OHCoreTestCase;
import org.isf.dicom.manager.DicomManagerFactory;
import org.isf.dicom.manager.DicomManagerInterface;
import org.isf.dicom.manager.FileSystemDicomManager;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;

class TestFileSystemDicomManager extends OHCoreTestCase {

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
	void testNewFileSystemDicomManagerMissingProperty() throws Exception {
		assertThatThrownBy(() ->
		{
			Properties properties = new Properties();
			properties.setProperty("dicom.manager.impl", "FileSystemDicomManager");
			new FileSystemDicomManager(properties);
		})
				.isInstanceOf(OHDicomException.class);
	}

	@Test
	void testDicomManagerFactoryGetMaxDicomSizeSetValue() throws Exception {
		DicomManagerFactory.getManager();
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		Properties properties = new Properties();
		properties.setProperty("dicom.max.size", "2M");
		props.set(clazz, properties);
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertThat(maxDicomSize).isEqualTo("2M");
		// reset/clear properties
		props.set(clazz, new Properties());
	}

	@Test
	void testDicomManagerFactoryGetMaxDicomSizeLongSetValue() throws Exception {
		DicomManagerFactory.getManager();
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		Properties properties = new Properties();
		properties.setProperty("dicom.max.size", "2M");
		props.set(clazz, properties);
		long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertThat(maxDicomSize).isEqualTo(2097152L);
		// reset/clear properties
		props.set(clazz, new Properties());
	}

	@Test
	void testDicomManagerFactoryGetMaxDicomSizeLongException() throws Exception {
		assertThatThrownBy(() ->
		{
			DicomManagerFactory.getManager();
			Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
			Field props = clazz.getDeclaredField("props");
			props.setAccessible(true);
			Properties properties = new Properties();
			properties.setProperty("dicom.max.size", "aString");
			props.set(clazz, properties);
			DicomManagerFactory.getMaxDicomSizeLong();
		})
				.isInstanceOf(OHDicomException.class);
		Class clazz = Class.forName("org.isf.dicom.manager.DicomManagerFactory");
		// reset/clear properties
		Field props = clazz.getDeclaredField("props");
		props.setAccessible(true);
		props.set(clazz, new Properties());
	}

	@Test
	void testSaveFile() throws Exception {
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
	void testLoadPatientFiles() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		fileSystemDicomManager.saveFile(dicomFile);

		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(0);
		assertThat(fileDicoms).hasSize(1);
		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void testLoadDetails() throws Exception {
		FileDicom fileDicom = fileSystemDicomManager.loadDetails(2, 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void testLoadDetailsLongObject() throws Exception {
		FileDicom fileDicom = fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);
	}

	@Test
	void testLoadDetailsLongObjectSeriesNumberNull() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, null)).isNull();
	}

	@Test
	void testLoadDetailsLongObjectSeriesNumberZeroLength() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "     ")).isNull();
	}

	@Test
	void testLoadDetailsLongObjectIdFileNull() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(null, 1, "TestSeriesNumber")).isNull();
	}

	@Test
	void testLoadDetailsLongObjectSeriesNumberNullString() throws Exception {
		assertThat(fileSystemDicomManager.loadDetails(Long.valueOf(2), 1, "NuLl")).isNull();
	}

	@Test
	void testGetSeriesDetail() throws Exception {
		Long[] result = fileSystemDicomManager.getSeriesDetail(1, "TestSeriesNumber");
		assertThat(result).isNotEmpty();
	}

	@Test
	void testGetSeriesDetailSeriesNumberNull() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, null)).isNull();
	}

	@Test
	void testGetSeriesDetailSeriesNumberEmptyString() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, "     ")).isNull();
	}

	@Test
	void testGetSeriesDetailSeriesNumberNullString() throws Exception {
		assertThat(fileSystemDicomManager.getSeriesDetail(1, "nuLL")).isNull();
	}

	@Test
	void testExist() throws Exception {
		DicomType dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		fileSystemDicomManager.saveFile(dicomFile);

		boolean exists = fileSystemDicomManager.exist(dicomFile);
		assertThat(exists).isTrue();

		cleanupDicomFiles(dicomFile.getPatId());
	}

	@Test
	void testExistWhenDicomFileNoExist() throws OHServiceException {
		FileDicom dicomFile = new FileDicom();
		boolean exists = fileSystemDicomManager.exist(dicomFile);
		assertThat(exists).isFalse();
	}

	@Test
	void testDeleteSeries() throws Exception {
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
	void testDeleteSeriesDetailSeriesNumberNull() throws Exception {
		assertThatThrownBy(() ->
				fileSystemDicomManager.deleteSeries(1, null))
			.isInstanceOf(OHDicomException.class);
	}

	@Test
	void testDeleteSeriesDetailSeriesNumberEmptyString() throws Exception {
		assertThatThrownBy(() ->
			fileSystemDicomManager.deleteSeries(1, "     "))
			.isInstanceOf(OHDicomException.class);
	}

	@Test
	void testDeleteSeriesDetailSeriesNumberNullString() throws Exception {
		assertThatThrownBy(() ->
			fileSystemDicomManager.deleteSeries(1, "nuLL"))
			.isInstanceOf(OHDicomException.class);
	}

	private static void cleanupDicomFiles(int patientId) {
		FileSystemUtils.deleteRecursively(new File("rsc-test/dicom/" + patientId));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}
}
