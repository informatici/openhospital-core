package org.isf.dicom.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
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
		String seriesNumber = null;
		try {
			seriesNumber = generateSeriesNumber(patient);
		} catch (OHServiceException e1) {
			seriesNumber = "";
		}

        for (File value : files) {

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }

            if (!value.isDirectory()) {
                loadDicom(fileDicom, value, patient, seriesNumber);
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
			String seriesDate = "";
			String studyDate = "";
			boolean isJpeg = fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
			boolean isDicom = fileName.toLowerCase().endsWith(".dcm");
			
			ImageReader reader;
			Iterator<?> iter = null;
			if (isJpeg) {
				
				seriesDate = DateFormat.getDateInstance().format(FileTools.getTimestamp(sourceFile)); //get last modified date (creation date)
				studyDate = DateFormat.getDateInstance().format(FileTools.getTimestamp(sourceFile)); //get last modified date (creation date)
			}
			else if (isDicom) {
				iter = ImageIO.getImageReadersByFormatName("DICOM");
				reader = (ImageReader) iter.next();
				
				DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicomObject = dicomStreamMetaData.getDicomObject();
				try {
					seriesDate = DateFormat.getDateInstance().format(dicomObject.getDate(Tag.SeriesDate, Tag.SeriesTime));
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable SeriesDate");
				}
				try {
					studyDate = DateFormat.getDateInstance().format(dicomObject.getDate(Tag.StudyDate, Tag.StudyTime));
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable StudyDate");
				}
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
	public synchronized static void loadDicom(FileDicom dicomFileDetail, File sourceFile, int patient, String generatedSeriesNumber) {
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
			String seriesDate = dicomFileDetail.getDicomSeriesDate();
			String seriesDescription = dicomFileDetail.getDicomSeriesDescription();
			String seriesDescriptionCodeSequence = dicomFileDetail.getDicomSeriesDescriptionCodeSequence();
			String seriesNumber = dicomFileDetail.getDicomSeriesNumber();
			String seriesInstanceUID = dicomFileDetail.getDicomSeriesInstanceUID();
			String seriesUID = dicomFileDetail.getDicomSeriesUID();
			String studyDate = dicomFileDetail.getDicomStudyDate();
			String studyDescription = dicomFileDetail.getDicomStudyDescription();
			String studyUID = dicomFileDetail.getDicomStudyId();
			String modality = dicomFileDetail.getModality();
			if (isJpeg) {
				seriesNumber = !seriesNumber.isEmpty() ? seriesNumber : generateSeriesNumber(patient);
				seriesDate = !seriesDate.isEmpty() ? seriesDate : DateFormat.getDateInstance().format(FileTools.getTimestamp(sourceFile)); //get last modified date (creation date)
				studyDate = !studyDate.isEmpty() ? studyDate : DateFormat.getDateInstance().format(FileTools.getTimestamp(sourceFile)); //get last modified date (creation date)
				seriesInstanceUID = !seriesInstanceUID.isEmpty() ? seriesInstanceUID : "<org_root>." + seriesNumber;
			}
			else if (isDicom) {
				DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicomObject = dicomStreamMetaData.getDicomObject();
				
				patientID = !patientID.isEmpty() ? patientID : dicomObject.getString(Tag.PatientID);
				// System.out.println("PatientID "+patientID);
				patientName = !patientName.isEmpty() ? patientName : dicomObject.getString(Tag.PatientName);
				patientAddress = !patientAddress.isEmpty() ? patientAddress : dicomObject.getString(Tag.PatientAddress);
				patientAge = !patientAge.isEmpty() ? patientAge : dicomObject.getString(Tag.PatientAge);
				String acquisitionsInSeries = dicomObject.getString(Tag.AcquisitionsInSeries);
				String acquisitionsInStudy = dicomObject.getString(Tag.AcquisitionsInStudy);
				String applicatorDescription = dicomObject.getString(Tag.ApplicatorDescription);
				String dicomMediaRetrievalSequence = dicomObject.getString(Tag.DICOMMediaRetrievalSequence);
				String patientComments = dicomObject.getString(Tag.PatientComments);
				try {
					patientBirthDate = !patientBirthDate.isEmpty() ? patientBirthDate : DateFormat.getDateInstance().format(dicomObject.getDate(Tag.PatientBirthDate));
				} catch (Exception ecc) {
				}
				patientSex = !patientSex.isEmpty() ? patientSex : dicomObject.getString(Tag.PatientSex);
				modality = !modality.isEmpty() ? modality : dicomObject.getString(Tag.Modality);
				studyUID = !studyUID.isEmpty() ? studyUID : dicomObject.getString(Tag.StudyInstanceUID);
				try {
					studyDate = !studyDate.isEmpty() ? studyDate : DateFormat.getDateInstance().format(dicomObject.getDate(Tag.StudyDate, Tag.StudyTime));
				} catch (Exception ecc) {
					System.out.println("DICOM: Unparsable StudyDate");
				}
				accessionNumber = !accessionNumber.isEmpty() ? accessionNumber : dicomObject.getString(Tag.AccessionNumber);
				studyDescription = !studyDescription.isEmpty() ? studyDescription : dicomObject.getString(Tag.StudyDescription);
				String studyComments = dicomObject.getString(Tag.StudyComments);
				seriesUID = !seriesUID.isEmpty() ? seriesUID : dicomObject.getString(Tag.SeriesInstanceUID);
				String directoryRecordType = dicomObject.getString(Tag.DirectoryRecordType);
				seriesInstanceUID = !seriesInstanceUID.isEmpty() ? seriesInstanceUID : dicomObject.getString(Tag.SeriesInstanceUID);
				seriesNumber = !seriesNumber.isEmpty() ? seriesNumber : dicomObject.getString(Tag.SeriesNumber);
				seriesDescriptionCodeSequence = !seriesDescriptionCodeSequence.isEmpty() ? seriesDescriptionCodeSequence : dicomObject.getString(Tag.SeriesDescriptionCodeSequence);
				String sliceVector = dicomObject.getString(Tag.SliceVector);
				String sliceLocation = dicomObject.getString(Tag.SliceLocation);
				String sliceThickness = dicomObject.getString(Tag.SliceThickness);
				String sliceProgressionDirection = dicomObject.getString(Tag.SliceProgressionDirection);
				try {
					seriesDate = !seriesDate.isEmpty() ? seriesDate : DateFormat.getDateInstance().format(dicomObject.getDate(Tag.SeriesDate, Tag.SeriesTime));
				} catch (Exception ecc) {
				}
				institutionName = !institutionName.isEmpty() ? institutionName : dicomObject.getString(Tag.InstitutionName);
				seriesDescription = !seriesDescription.isEmpty() ? seriesDescription : dicomObject.getString(Tag.SeriesDescription);
				instanceUID = !instanceUID.isEmpty() ? instanceUID : dicomObject.getString(Tag.SOPInstanceUID);
			}
			
			// Loaded... ready to save the file
			dicomFileDetail.setDicomData(sourceFile);
			dicomFileDetail.setFileName(sourceFile.getName());
			dicomFileDetail.setDicomAccessionNumber(accessionNumber);
			dicomFileDetail.setDicomInstanceUID(instanceUID);
			dicomFileDetail.setDicomInstitutionName(institutionName);
			dicomFileDetail.setDicomPatientAddress(patientAddress);
			dicomFileDetail.setDicomPatientAge(patientAge);
			dicomFileDetail.setDicomPatientBirthDate(patientBirthDate);
			dicomFileDetail.setDicomPatientID(patientID);
			dicomFileDetail.setDicomPatientName(patientName);
			dicomFileDetail.setDicomPatientSex(patientSex);
			dicomFileDetail.setDicomSeriesDate(seriesDate);
			dicomFileDetail.setDicomSeriesDescription(seriesDescription);
			dicomFileDetail.setDicomSeriesDescriptionCodeSequence(seriesDescriptionCodeSequence);
			dicomFileDetail.setDicomSeriesInstanceUID(seriesInstanceUID);
			dicomFileDetail.setDicomSeriesNumber(seriesNumber);
			dicomFileDetail.setDicomSeriesUID(seriesUID);
			dicomFileDetail.setDicomStudyDate(studyDate);
			dicomFileDetail.setDicomStudyDescription(studyDescription);
			dicomFileDetail.setDicomStudyId(studyUID);
			dicomFileDetail.setIdFile(0);
			dicomFileDetail.setPatId(patient);
			dicomFileDetail.setDicomThumbnail(scaled);
			dicomFileDetail.setModality(modality);
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
