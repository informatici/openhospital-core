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

import org.aspectj.util.FileUtil;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.isf.dicom.manager.DicomManagerFactory;
import org.isf.dicom.manager.DicomManagerInterface;
import org.isf.dicom.manager.FileSystemDicomManager;
import org.isf.dicom.manager.SourceFiles;
import org.isf.dicom.model.FileDicom;
import org.isf.dicom.service.DicomIoOperations;
import org.isf.dicomtype.model.DicomType;
import org.isf.dicomtype.test.TestDicomType;
import org.isf.dicomtype.test.TestDicomTypeContext;
import org.isf.menu.manager.Context;
import org.isf.utils.db.DbJpaUtil;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests  
{
	public static final int PATIENT_ID = 0;
	public static final Long _4M = new Long(4194304);
	final String STUDY_DATE = "Mon Jan 01 10:22:33 AST 2001";
	final String SERIES_DATE = "Mon May 14 10:22:33 AST 2007";
	private static DbJpaUtil jpa;
	private static TestDicom testFileDicom;
	private static TestDicomContext testFileDicomContext;
	private static TestDicomType testDicomType;
	private static TestDicomTypeContext testDicomTypeContext;

    @Autowired
    DicomIoOperations dicomIoOperation;

	@Autowired
	private ApplicationContext applicationContext;

	private DicomManagerInterface fileSystemDicomManager;
	private FileDicom dicomFile;

	@BeforeClass
    public static void setUpClass() throws ParseException {
    	jpa = new DbJpaUtil();
    	testFileDicom = new TestDicom();
    	testFileDicomContext = new TestDicomContext();
    	testDicomType = new TestDicomType();
    	testDicomTypeContext = new TestDicomTypeContext();
    	
        return;
    } 

    @Before
    public void setUp() throws OHException, OHDicomException {
        jpa.open();
		Context.setApplicationContext(applicationContext);
		fileSystemDicomManager = new FileSystemDicomManager(_getDicomProperties());
		DicomType dicomType;
		dicomType = testDicomType.setup(true);
		dicomFile = testFileDicom.setup(dicomType, true);
        _saveContext();
		
		return;
    }

	private Properties _getDicomProperties(){
		Properties properties = new Properties();
		properties.setProperty("dicom.manager.impl", "FileSystemDicomManager");
		properties.setProperty("dicom.storage.filesystem", "rsc-test/dicom");
		return properties;
	}

	@After
    public void tearDown() throws Exception
    {
        _restoreContext();   
        
        jpa.flush();
        jpa.close();
                
        return;
    }
    
    @AfterClass
    public static void tearDownClass() throws OHException 
    {
    	testFileDicom = null;
    	testFileDicomContext = null;
		_deleteSavedDicomFile();

		return;
    }

	private static void _deleteSavedDicomFile() {
		FileUtil.deleteContents(new File("rsc-test/dicom/0"));
		FileUtil.deleteContents(new File("rsc-test/dicom/dicom.storage"));
	}


	@Test
	public void testFileDicomGets() 
	{
		long code = 0;
			

		try 
		{	
			DicomType dicomType = testDicomType.setup(false);
			code = _setupTestFileDicom(false);
			_checkFileDicomIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
				
		return;
	}
	
	
	@Test
	public void testFileDicomSets()
	{
		long code = 0;
			

		try 
		{		
			code = _setupTestFileDicom(true);
			_checkFileDicomIntoDb(code);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	
	@Test
	public void testIoGetSerieDetail() 
	{
		long code = 0;
		
		
		try 
		{		
			code = _setupTestFileDicom(false);
			FileDicom foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			Long[] dicoms = dicomIoOperation.getSerieDetail(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
			
			assertEquals(1, dicoms.length);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}


	@Test
	public void testIoDeleteSerie() 
	{
		long code = 0;
		boolean result = false;
		

		try 
		{		
			code = _setupTestFileDicom(false);
			FileDicom foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			result = dicomIoOperation.deleteSerie(foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());

			assertTrue(result);
			result = dicomIoOperation.isCodePresent(code);
			assertFalse(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}
	
	
	@Test
	public void testIoLoadFileDicom() 
	{
		long code = 0;
		

		try 
		{		
			code = _setupTestFileDicom(false);
			FileDicom foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			FileDicom dicom = dicomIoOperation.loadDetails(foundFileDicom.getIdFile(), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
			FileDicom dicom2 = dicomIoOperation.loadDetails(new Long(foundFileDicom.getIdFile()), foundFileDicom.getPatId(), foundFileDicom.getDicomSeriesNumber());
			
			assertEquals(dicom.getDicomInstanceUID(), dicom2.getDicomInstanceUID());
			assertEquals(foundFileDicom.getDicomSeriesDescription(), dicom.getDicomSeriesDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}


	@Test
	public void testIoLoadPatientFiles() throws Exception{
		long code = _setupTestFileDicom(false);
		FileDicom foundFileDicom = (FileDicom) jpa.find(FileDicom.class, code);
		FileDicom[] dicoms = dicomIoOperation.loadPatientFiles(foundFileDicom.getPatId());
		assertEquals(foundFileDicom.getDicomSeriesDescription(), dicoms[0].getDicomSeriesDescription());
	}

	
	@Test
	public void testIoExist() 
	{
		long code = 0;
		boolean result = false;
		

		try 
		{		
			code = _setupTestFileDicom(false);
			FileDicom foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			result = dicomIoOperation.exist(foundFileDicom);

			assertTrue(result);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		
		return;
	}
	
		
	@Test
	public void testIoSaveFile() 
	{
		long code = 0;
			
		
		try 
		{		
			code = _setupTestFileDicom(false);
			FileDicom foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			foundFileDicom.setDicomSeriesDescription("Update");
			dicomIoOperation.saveFile(foundFileDicom);
			FileDicom updateFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
			
			assertEquals("Update", updateFileDicom.getDicomSeriesDescription());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();		
			fail();
		}
		
		return;
	}




	@Test
	public void testSaveFile() throws OHServiceException, IOException {
		fileSystemDicomManager.saveFile(dicomFile);
		_checkIfExists(dicomFile);
	}

	private boolean _checkIfExists(FileDicom dicomFile) throws IOException {
		File dicomFileDir = new File("rsc-test/dicom/0/TestSeriesNumber");
		FileReader fr = new FileReader(new File(dicomFileDir, "1.properties"));
		Properties dicomProperties = new Properties();
		dicomProperties.load(fr);
		fr.close();
		assertEquals(3, dicomFileDir.listFiles().length);
		assertEquals("TestInteanceUid",  dicomProperties.getProperty("dicomInstanceUID"));


		return false;
	}

	@Test
	public void testLoadPatientFiles() throws OHServiceException {
		FileDicom[] fileDicoms = fileSystemDicomManager.loadPatientFiles(PATIENT_ID);
		assertTrue(fileDicoms.length > 0);

	}

	@Test
	public void testLoadDetails() throws OHServiceException {

		FileDicom fileDicom = fileSystemDicomManager
				.loadDetails(2, 1, "TestSeriesNumber");
		testFileDicom.check(fileDicom);


	}

	@Test
	public void testGetSerieDetail() throws OHServiceException {
		Long[] result = fileSystemDicomManager.getSerieDetail(PATIENT_ID, "TestSeriesNumber");
		assertTrue(result.length > 0);
	}


	@Test
	public void tesExist() throws OHServiceException {
		boolean fileExits = fileSystemDicomManager.exist(dicomFile);
		assertTrue(fileExits);
	}

	@Test
	public void testExistWhenDicomFileNoExist() throws OHServiceException {
		FileDicom dicomFile = new FileDicom();
		boolean fileExits = fileSystemDicomManager.exist(dicomFile);
		assertFalse(fileExits);
	}


	@Test
	public void testDeleteSerie() throws OHServiceException, OHException {
		int idPaziente = 2;
		DicomType dicomType;
		dicomType = testDicomType.setup(true);
		FileDicom dicomFile = testFileDicom.setup(dicomType, true);
		dicomFile.setDicomSeriesNumber("SeriesNumber");
		dicomFile.setPatId(idPaziente);
		fileSystemDicomManager.saveFile(dicomFile);
		boolean serieDeleted = fileSystemDicomManager.deleteSerie(idPaziente, "SeriesNumber");
		assertTrue(serieDeleted);
	}


	@Test
	public void testDicomManagerFactoryGetManager() throws OHDicomException {
		DicomManagerInterface manager = DicomManagerFactory.getManager();
		assertThat(manager, instanceOf(FileSystemDicomManager.class));
	}

	@Test
	public void testDicomManagerFactoryGetMaxDicomSize() throws OHDicomException {
		String maxDicomSize = DicomManagerFactory.getMaxDicomSize();
		assertEquals("4M", maxDicomSize);
	}



	@Test
	public void testDicomManagerFactoryGetMaxDicomSizeLong() throws OHDicomException {
		Long maxDicomSize = DicomManagerFactory.getMaxDicomSizeLong();
		assertEquals(_4M, maxDicomSize);
	}

	@Test
	public void testSourceFilesGenerateSeriesNumber() throws OHServiceException {
		String seriesNumber = SourceFiles.generateSeriesNumber(PATIENT_ID);
		assertFalse(seriesNumber.isEmpty());

	}

	@Test
	public void testSourceFilesLoadDicom() throws Exception {
		File file = _getFile("case3c_002.dcm");
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		assertEquals("case3c_002.dcm", dicomFile.getFileName());
		assertEquals("Anonymized Hospital", dicomFile.getDicomInstitutionName());
		assertEquals("MRT Oberbauch", dicomFile.getDicomStudyDescription());

	}
	@Test
	public void testSourceFilesLoadDicomWhenImageFormatIsJpeg() throws Exception {
		File file = _getFile("image.0007.jpg");
		SourceFiles.loadDicom(dicomFile, file, PATIENT_ID);
		String fileName = dicomFile.getFileName();
		assertEquals("image.0007.jpg", fileName);


	}

	@Test
	public void testSourceFilesPreloadDicom() throws Exception {
		File file = _getFile("case3c_002.dcm");
		Date expectedStudyDate = _getDicomObject(file).getDate(Tag.StudyDate, Tag.StudyTime);
		Date expectedSeriesDate= _getDicomObject(file).getDate(Tag.SeriesDate, Tag.SeriesTime);


		FileDicom dicomFile = SourceFiles.preLoadDicom(file, 1);

		assertEquals("case3c_002.dcm", dicomFile.getFileName());
		assertEquals(1, dicomFile.getFrameCount());
		assertTrue(_areDatesEquals(expectedStudyDate, dicomFile.getDicomStudyDate()));
		assertTrue(_areDatesEquals(expectedSeriesDate, dicomFile.getDicomSeriesDate()));

	}

	private DicomObject _getDicomObject(File sourceFile) throws IOException {
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
		ImageReader reader = (ImageReader) iter.next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
		reader.setInput(imageInputStream, false);
		DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
		return dicomStreamMetaData.getDicomObject();

	}

	private boolean _areDatesEquals(Date date, Date date2){
		return date.compareTo(date2) == 0;
	}

	@Test
	public void testSourceFilesCountFiles() throws Exception {
		File file = _getFile("dicomdir");
		int count = SourceFiles.countFiles(file, 1);
		assertTrue(count > 0);

	}


	private File _getFile(String fileName){
		return new File(getClass().getResource(fileName).getFile());
	}





	private void _saveContext() throws OHException
    {
		testFileDicomContext.saveAll(jpa);
		testDicomTypeContext.saveAll(jpa);
        		
        return;
    }
	
    private void _restoreContext() throws OHException 
    {
		testFileDicomContext.deleteNews(jpa);
		testDicomTypeContext.deleteNews(jpa);
        
        return;
    }
        
	private long _setupTestFileDicom(
			boolean usingSet) throws OHException 
	{
		FileDicom dicom;
		DicomType dicomType;
		
		dicomType = testDicomType.setup(true);
		
    	jpa.beginTransaction();	
    	dicom = testFileDicom.setup(dicomType, usingSet);
    	jpa.persist(dicomType);
		jpa.persist(dicom);
    	jpa.commitTransaction();
    	
		return dicom.getIdFile();
	}
		
	private void  _checkFileDicomIntoDb(
			long code) throws OHException 
	{
		FileDicom foundFileDicom;
		

		foundFileDicom = (FileDicom)jpa.find(FileDicom.class, code); 
		testFileDicom.check(foundFileDicom);
		
		return;
	}	
}
