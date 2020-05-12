package org.isf.dicom.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageio.plugins.dcm.DicomStreamMetaData;
import org.dcm4che2.io.DicomCodingException;
import org.imgscalr.Scalr;
import org.isf.dicom.model.FileDicom;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.file.FileTools;

/**
 * Magager for DICOM Files
 * 
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public class SourceFiles extends Thread {

	private File file = null;
	private FileDicom fileDicom = null;
	private int patient = 0;
	private int filesCount = 0;
	private int filesLoaded = 0;
	private AbstractDicomLoader dicomLoader = null;
	private AbstractThumbnailViewGui thumbnail = null;
	
	public SourceFiles(FileDicom fileDicom, File sourceFile, int patient, int filesCount, AbstractThumbnailViewGui thumbnail, AbstractDicomLoader frame) {
		this.patient = patient;
		this.file = sourceFile;
		this.fileDicom = fileDicom;
		this.filesCount = filesCount;
		this.thumbnail = thumbnail;
		this.dicomLoader = frame;
		start();
	}

	public void run() {
		loadDicomDir(fileDicom, file, patient);
		dicomLoader.setVisible(false);
		thumbnail.initialize();
	}

	/**
	 * load a DICOM directory
	 */
	private void loadDicomDir(FileDicom fileDicom, File sourceFile, int patient) {
		// installLibs();
		File[] files = sourceFile.listFiles();
		String seriesNumber = fileDicom.getDicomSeriesNumber();
		if (seriesNumber == null || seriesNumber.isEmpty()) {
			try {
				seriesNumber = generateSeriesNumber(patient);
				fileDicom.setDicomSeriesNumber(seriesNumber);
			} catch (OHServiceException e1) {
				seriesNumber = "";
			}
		}
        for (File value : files) {

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }

            if (!value.isDirectory()) {
                loadDicom(fileDicom, value, patient);
                filesLoaded++;
                dicomLoader.setLoaded(filesLoaded);
            }
            else if (!".".equals(value.getName()) && !"..".equals(value.getName()))
                loadDicomDir(fileDicom, value, patient);
        }
	}

	public static int countFiles(File sourceFile, int patient) {
		int num = 0;

		File[] files = sourceFile.listFiles();

        for (File value : files) {
            if (!value.isDirectory())
                num++;
            else if (!".".equals(value.getName()) && !"..".equals(value.getName()))
                num = num + countFiles(value, patient);
        }
		return num;
	}

	public boolean working() {
		return (filesLoaded < filesCount);
	}

	public int getLoaded() {
		return filesLoaded;
	}
	
	/**
	 * preLoad dicom file for validation in gui with some
	 * data from filesystem
	 * @param sourceFile
	 * @param patient
	 * @param seriesNumber 
	 */
	public static FileDicom preLoadDicom(File sourceFile, int numfiles) {
		FileDicom dicomFileDetail = new FileDicom();
		try {
			String fileName = sourceFile.getName();
			Date seriesDate = null;
			Date studyDate = null;
			boolean isJpeg = fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
			boolean isDicom = fileName.toLowerCase().endsWith(".dcm");
			
			ImageReader reader;
			Iterator<?> iter = null;
			if (isJpeg) {
				
				seriesDate = FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
				studyDate = FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
			}
			else if (isDicom) {
				iter = ImageIO.getImageReadersByFormatName("DICOM");
				reader = (ImageReader) iter.next();
				
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
				
				reader.setInput(imageInputStream, false);
				
				DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicomObject = dicomStreamMetaData.getDicomObject();
				try {
					seriesDate = dicomObject.getDate(Tag.SeriesDate, Tag.SeriesTime);
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable SeriesDate");
				}
				try {
					studyDate = dicomObject.getDate(Tag.StudyDate, Tag.StudyTime);
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable StudyDate");
				}
				dicomFileDetail.setDicomSeriesNumber(dicomObject.getString(Tag.SeriesNumber));
			}
			else {
				throw new OHDicomException(new OHExceptionMessage("", "format not supported", OHSeverityLevel.ERROR));
			}
			dicomFileDetail.setFrameCount(numfiles);
			dicomFileDetail.setDicomData(sourceFile);
			dicomFileDetail.setFileName(fileName);
			dicomFileDetail.setDicomSeriesDate(seriesDate);
			dicomFileDetail.setDicomStudyDate(studyDate);
			
			return dicomFileDetail;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dicomFileDetail;
	}

	/**
	 * load dicom file
	 * @param sourceFile
	 * @param patient
	 * @param seriesNumber 
	 */
	@SuppressWarnings("unused")
	public synchronized static void loadDicom(FileDicom dicomFileDetail, File sourceFile, int patient) {
		// installLibs();

		//System.out.println("File "+sourceFile.getName());

		if (".DS_Store".equals(sourceFile.getName()))
			return;

		try {
			boolean isJpeg = sourceFile.getName().toLowerCase().endsWith(".jpg") || 
					sourceFile.getName().toLowerCase().endsWith(".jpeg");
			boolean isDicom = sourceFile.getName().toLowerCase().endsWith(".dcm");
			
			ImageReader reader;
			ImageReadParam param;
			BufferedImage originalImage;
			Iterator<?> iter = null;
			if (isJpeg) {
				iter = ImageIO.getImageReadersByFormatName("jpeg");
				reader = (ImageReader) iter.next();
				//param = (JPEGImageReadParam) reader.getDefaultReadParam ();
				
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
				
				reader.setInput(imageInputStream, false);
				
				originalImage = null;

				try {
					originalImage = reader.read(0); //, param); //TODO: handle big sizes images (java.lang.IndexOutOfBoundsException: imageIndex out of bounds!)
				} catch (DicomCodingException dce) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.dicom.err"), 
							MessageBundle.getMessage("angal.dicom.load.err") + " : " + sourceFile.getName(), OHSeverityLevel.ERROR));
				}

				imageInputStream.close();
			}
			else if (isDicom) {
				iter = ImageIO.getImageReadersByFormatName("DICOM");
				reader = (ImageReader) iter.next();

				param = (DicomImageReadParam) reader.getDefaultReadParam();
				
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
				
				reader.setInput(imageInputStream, false);

				originalImage = null;

				try {
					originalImage = reader.read(0, param);
				} catch (DicomCodingException dce) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.dicom.err"), 
							MessageBundle.getMessage("angal.dicom.load.err") + " : " + sourceFile.getName(), OHSeverityLevel.ERROR));
				}

				imageInputStream.close();
			}
			else {
				throw new OHDicomException(new OHExceptionMessage("", "format not supported", OHSeverityLevel.ERROR));
			}
			
			BufferedImage scaled = Scalr.resize(originalImage, 100);
			
			
			String accessionNumber = dicomFileDetail.getDicomAccessionNumber();
			String instanceUID = dicomFileDetail.getDicomInstanceUID();
			String institutionName = dicomFileDetail.getDicomInstitutionName();
			String patientAddress = dicomFileDetail.getDicomPatientAddress();
			String patientAge = dicomFileDetail.getDicomPatientAge();
			String patientBirthDate = dicomFileDetail.getDicomPatientBirthDate();
			String patientID = String.valueOf(patient);
			String patientName = dicomFileDetail.getDicomPatientName();
			String patientSex = dicomFileDetail.getDicomPatientSex();
			Date seriesDate = dicomFileDetail.getDicomSeriesDate();
			String seriesDescription = dicomFileDetail.getDicomSeriesDescription();
			String seriesDescriptionCodeSequence = dicomFileDetail.getDicomSeriesDescriptionCodeSequence();
			String seriesNumber = dicomFileDetail.getDicomSeriesNumber();
			String seriesInstanceUID = dicomFileDetail.getDicomSeriesInstanceUID();
			String seriesUID = dicomFileDetail.getDicomSeriesUID();
			Date studyDate = dicomFileDetail.getDicomStudyDate();
			String studyDescription = dicomFileDetail.getDicomStudyDescription();
			String studyUID = dicomFileDetail.getDicomStudyId();
			String modality = dicomFileDetail.getModality();
			if (isJpeg) {
				//overriden by the user
				seriesDate = seriesDate != null ? seriesDate : FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
				studyDate = studyDate != null ? studyDate : FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
				
				//set by the system
				seriesNumber = !seriesNumber.isEmpty() ? seriesNumber : generateSeriesNumber(patient);
				seriesInstanceUID = !seriesInstanceUID.isEmpty() ? seriesInstanceUID : "<org_root>." + seriesNumber;
			}
			else if (isDicom) {
				DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicomObject = dicomStreamMetaData.getDicomObject();
				
				//overriden by the user
				seriesDescription = seriesDescription != null ? seriesDescription : dicomObject.getString(Tag.SeriesDescription);
				try {
					studyDate = studyDate != null ? studyDate : dicomObject.getDate(Tag.StudyDate, Tag.StudyTime);
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable StudyDate");
				}
				try {
					seriesDate = seriesDate != null ? seriesDate : dicomObject.getDate(Tag.SeriesDate, Tag.SeriesTime);
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable StudyDate");
				}

				//set by DICOM properties
				patientID = dicomObject.getString(Tag.PatientID) == null ? patientID : dicomObject.getString(Tag.PatientID);
				patientName = dicomObject.getString(Tag.PatientName) == null ? patientName : dicomObject.getString(Tag.PatientName);
				patientAddress = dicomObject.getString(Tag.PatientAddress) == null ? patientAddress : dicomObject.getString(Tag.PatientAddress);
				patientAge = dicomObject.getString(Tag.PatientAge) == null ? patientAge : dicomObject.getString(Tag.PatientAge);
				String acquisitionsInSeries = dicomObject.getString(Tag.AcquisitionsInSeries);
				String acquisitionsInStudy = dicomObject.getString(Tag.AcquisitionsInStudy);
				String applicatorDescription = dicomObject.getString(Tag.ApplicatorDescription);
				String dicomMediaRetrievalSequence = dicomObject.getString(Tag.DICOMMediaRetrievalSequence);
				String patientComments = dicomObject.getString(Tag.PatientComments);
				try {
					patientBirthDate = dicomObject.getDate(Tag.PatientBirthDate) == null ? patientBirthDate : DateFormat.getDateInstance().format(dicomObject.getDate(Tag.PatientBirthDate));
				} catch (Exception ecc) {
				}
				patientSex = dicomObject.getString(Tag.PatientSex) == null ? patientSex : dicomObject.getString(Tag.PatientSex);
				modality = dicomObject.getString(Tag.Modality) == null ? modality : dicomObject.getString(Tag.Modality);
				studyUID = dicomObject.getString(Tag.StudyInstanceUID) == null ? studyUID : dicomObject.getString(Tag.StudyInstanceUID);
				accessionNumber = dicomObject.getString(Tag.AccessionNumber) == null ? accessionNumber : dicomObject.getString(Tag.AccessionNumber);
				studyDescription = dicomObject.getString(Tag.StudyDescription) == null ? studyDescription : dicomObject.getString(Tag.StudyDescription);
				String studyComments = dicomObject.getString(Tag.StudyComments);
				seriesUID = dicomObject.getString(Tag.SeriesInstanceUID) == null ? seriesUID : dicomObject.getString(Tag.SeriesInstanceUID);
				String directoryRecordType = dicomObject.getString(Tag.DirectoryRecordType);
				seriesInstanceUID = dicomObject.getString(Tag.SeriesInstanceUID) == null ? seriesInstanceUID : dicomObject.getString(Tag.SeriesInstanceUID);
				seriesNumber = dicomObject.getString(Tag.SeriesNumber) == null ? seriesNumber : dicomObject.getString(Tag.SeriesNumber);
				seriesDescriptionCodeSequence = dicomObject.getString(Tag.SeriesDescriptionCodeSequence) == null ? seriesDescriptionCodeSequence : dicomObject.getString(Tag.SeriesDescriptionCodeSequence);
				String sliceVector = dicomObject.getString(Tag.SliceVector);
				String sliceLocation = dicomObject.getString(Tag.SliceLocation);
				String sliceThickness = dicomObject.getString(Tag.SliceThickness);
				String sliceProgressionDirection = dicomObject.getString(Tag.SliceProgressionDirection);
				institutionName = dicomObject.getString(Tag.InstitutionName) == null ? institutionName : dicomObject.getString(Tag.InstitutionName);
				instanceUID = dicomObject.getString(Tag.SOPInstanceUID) == null ? instanceUID : dicomObject.getString(Tag.SOPInstanceUID);
			}
			
			// Loaded... Update dicomFileDetail
			if (sourceFile != null) dicomFileDetail.setDicomData(sourceFile);
			if (sourceFile.getName() != null) dicomFileDetail.setFileName(sourceFile.getName());
			if (accessionNumber != null) dicomFileDetail.setDicomAccessionNumber(accessionNumber);
			if (instanceUID != null) dicomFileDetail.setDicomInstanceUID(instanceUID);
			if (institutionName != null) dicomFileDetail.setDicomInstitutionName(institutionName);
			if (patientAddress != null) dicomFileDetail.setDicomPatientAddress(patientAddress);
			if (patientAge != null) dicomFileDetail.setDicomPatientAge(patientAge);
			if (patientBirthDate != null) dicomFileDetail.setDicomPatientBirthDate(patientBirthDate);
			if (patientID != null) dicomFileDetail.setDicomPatientID(patientID);
			if (patientName != null) dicomFileDetail.setDicomPatientName(patientName);
			if (patientSex != null) dicomFileDetail.setDicomPatientSex(patientSex);
			if (seriesDate != null) dicomFileDetail.setDicomSeriesDate(seriesDate);
			if (seriesDescription != null) dicomFileDetail.setDicomSeriesDescription(seriesDescription);
			if (seriesDescriptionCodeSequence != null) dicomFileDetail.setDicomSeriesDescriptionCodeSequence(seriesDescriptionCodeSequence);
			if (seriesInstanceUID != null) dicomFileDetail.setDicomSeriesInstanceUID(seriesInstanceUID);
			if (seriesNumber != null) dicomFileDetail.setDicomSeriesNumber(seriesNumber);
			if (seriesUID != null) dicomFileDetail.setDicomSeriesUID(seriesUID);
			if (studyDate != null) dicomFileDetail.setDicomStudyDate(studyDate);
			if (studyDescription != null) dicomFileDetail.setDicomStudyDescription(studyDescription);
			if (studyUID != null) dicomFileDetail.setDicomStudyId(studyUID);
			if (patient != 0) dicomFileDetail.setPatId(patient);
			if (scaled != null) dicomFileDetail.setDicomThumbnail(scaled);
			if (modality != null) dicomFileDetail.setModality(modality);
			dicomFileDetail.setIdFile(0); //it trigger the DB save with SqlDicomManager
			try{
				DicomManagerFactory.getManager().saveFile(dicomFileDetail);
				//dicomFileDetail.setDicomSeriesNumber(dicom.getDicomSeriesNumber()); //series number could be generated if missing.
			}catch(OHServiceException ex){
				if(ex.getMessages() != null){
					throw new OHDicomException(ex.getCause(), ex.getMessages());
				}
			}

		} catch (Exception ecc) {
			ecc.printStackTrace();
		}
	}
	
	/**
	 * Creates a new unique series number
	 * @return the new unique code.
	 * @throws OHServiceException if an error occurs during the code generation.
	 */
	public static String generateSeriesNumber(int patient) throws OHServiceException
	{
		Random random = new Random();
		long candidateCode = 0;
		boolean exists = false;
		do 
		{
			candidateCode = Math.abs(random.nextLong());
			exists = DicomManagerFactory.getManager().exist(patient, String.valueOf(candidateCode));

		} while (exists != false); 

		return String.valueOf(candidateCode);
	}
}
