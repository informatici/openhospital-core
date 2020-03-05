package org.isf.dicom.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;

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

/**
 * Magager for DICOM Files
 * 
 * @author Pietro Castellucci
 * @version 1.0.0
 */

public class SourceFiles extends Thread {

	private File file = null;
	private int patient = 0;
	private int filesCount = 0;
	private int filesLoaded = 0;
	private AbstractDicomLoader dicomLoader = null;
	private AbstractThumbnailViewGui thumbnail = null;

	public SourceFiles(File sourceFile, int patient, int filesCount, AbstractThumbnailViewGui thumbnail, AbstractDicomLoader frame) {
		this.patient = patient;
		this.file = sourceFile;
		this.filesCount = filesCount;
		this.thumbnail = thumbnail;
		this.dicomLoader = frame;
		start();
	}

	public void run() {
		loadDicomDir(file, patient);
		dicomLoader.setVisible(false);
		thumbnail.initialize();
	}

	/**
	 * load a DICOM directory
	 */
	private void loadDicomDir(File sourceFile, int patient) {
		// installLibs();
		File[] files = sourceFile.listFiles();

        for (File value : files) {

            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }

            if (!value.isDirectory()) {
                loadDicom(value, patient);
                filesLoaded++;
                dicomLoader.setLoaded(filesLoaded);
            }
            else if (!".".equals(value.getName()) && !"..".equals(value.getName()))
                loadDicomDir(value, patient);
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
		// System.out.println("working "+loaded+" < "+numeroFiles);
		return (filesLoaded < filesCount);
	}

	public int getLoaded() {
		return filesLoaded;
	}

	/**
	 * load dicom file
	 */
	@SuppressWarnings("unused")
	public synchronized static void loadDicom(File sourceFile, int paziente) {
		// installLibs();

		//System.out.println("File "+sourceFile.getName());

		if (".DS_Store".equals(sourceFile.getName()))
			return;

		try {
			String filetype = sourceFile.getName().substring(sourceFile.getName().lastIndexOf('.')+1);
			//System.out.println("filetype "+filetype);
			
			ImageReader reader;
			ImageReadParam param;
			Iterator<?> iter = null;
			if (filetype.equalsIgnoreCase("jpg") || filetype.equalsIgnoreCase("jpeg")) {
				iter = ImageIO.getImageReadersByFormatName("jpeg");
				reader = (ImageReader) iter.next();

				param = (JPEGImageReadParam) reader.getDefaultReadParam();
			}
			else if (filetype.equalsIgnoreCase("dcm")) {
				iter = ImageIO.getImageReadersByFormatName("DICOM");
				reader = (ImageReader) iter.next();

				param = (DicomImageReadParam) reader.getDefaultReadParam();
			}
			else {
				throw new OHDicomException(new OHExceptionMessage("", "format not supported", OHSeverityLevel.ERROR));
			}
				
			ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);
			
			reader.setInput(imageInputStream, false);

			BufferedImage original = null;

			try {
				original = reader.read(0, param);
			} catch (DicomCodingException dce) {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.dicom.err"), 
						MessageBundle.getMessage("angal.dicom.load.err") + " : " + sourceFile.getName(), OHSeverityLevel.ERROR));
			}

			imageInputStream.close();

			BufferedImage scaled = Scalr.resize(original, 100);

			String accessionNumber = "";
			String instanceUID = "";
			String institutionName = "";
			String patientAddress = "";
			String patientAge = "";
			String patientBirthDate = "";
			String patientID = "";
			String patientName = "";
			String patientSex = "";
			String seriesDate = "";
			String seriesDescription = "";
			String seriesDescriptionCodeSequence = "";
			String seriesInstanceUID = "";
			String seriesNumber = "";
			String seriesUID = "";
			String studyDate = "";
			String studyDescription = "";
			String studyUID = "";
			String modality = "";
			if (filetype.equals("jpg") || filetype.equals("jpeg")) {
			}
			else if (filetype.equals("dcm")) {
				DicomStreamMetaData dicomStreamMetaData = (DicomStreamMetaData) reader.getStreamMetadata();
				DicomObject dicomObject = dicomStreamMetaData.getDicomObject();
				
				patientID = dicomObject.getString(Tag.PatientID);
				// System.out.println("PatientID "+patientID);
				patientName = dicomObject.getString(Tag.PatientName);
				patientAddress = dicomObject.getString(Tag.PatientAddress);
				patientAge = dicomObject.getString(Tag.PatientAge);
				String acquisitionsInSeries = dicomObject.getString(Tag.AcquisitionsInSeries);
				String acquisitionsInStudy = dicomObject.getString(Tag.AcquisitionsInStudy);
				String applicatorDescription = dicomObject.getString(Tag.ApplicatorDescription);
				String dicomMediaRetrievalSequence = dicomObject.getString(Tag.DICOMMediaRetrievalSequence);
				String patientComments = dicomObject.getString(Tag.PatientComments);
				patientBirthDate = "";
				try {
					patientBirthDate = DateFormat.getDateInstance().format(dicomObject.getDate(Tag.PatientBirthDate));
				} catch (Exception ecc) {
				}
				patientSex = dicomObject.getString(Tag.PatientSex);
				modality = dicomObject.getString(Tag.Modality);
				studyUID = dicomObject.getString(Tag.StudyInstanceUID);
				studyDate = "";
				try {
					studyDate = DateFormat.getDateInstance().format(dicomObject.getDate(Tag.StudyDate, Tag.StudyTime));
				} catch (Exception ecc) {
				}
				accessionNumber = dicomObject.getString(Tag.AccessionNumber);
				studyDescription = dicomObject.getString(Tag.StudyDescription);
				String studyComments = dicomObject.getString(Tag.StudyComments);
				seriesUID = dicomObject.getString(Tag.SeriesInstanceUID);
				String directoryRecordType = dicomObject.getString(Tag.DirectoryRecordType);
				seriesInstanceUID = dicomObject.getString(Tag.SeriesInstanceUID);
				seriesNumber = dicomObject.getString(Tag.SeriesNumber);
				seriesDescriptionCodeSequence = dicomObject.getString(Tag.SeriesDescriptionCodeSequence);
				String sliceVector = dicomObject.getString(Tag.SliceVector);
				String sliceLocation = dicomObject.getString(Tag.SliceLocation);
				String sliceThickness = dicomObject.getString(Tag.SliceThickness);
				String sliceProgressionDirection = dicomObject.getString(Tag.SliceProgressionDirection);
				seriesDate = "";
				try {
					seriesDate = DateFormat.getDateInstance().format(dicomObject.getDate(Tag.SeriesDate, Tag.SeriesTime));
				} catch (Exception ecc) {
				}
				institutionName = dicomObject.getString(Tag.InstitutionName);
				seriesDescription = dicomObject.getString(Tag.SeriesDescription);
				instanceUID = dicomObject.getString(Tag.SOPInstanceUID);
			}
			
			// Loaded... ready to save the file

			FileDicom dicomFileDetail = new FileDicom();
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
			dicomFileDetail.setPatId(paziente);
			dicomFileDetail.setDicomThumbnail(scaled);
			dicomFileDetail.setModality(modality);
			try{
				DicomManagerFactory.getManager().saveFile(dicomFileDetail);
			}catch(OHServiceException ex){
				if(ex.getMessages() != null){
					for(OHExceptionMessage msg : ex.getMessages()){
						JOptionPane.showMessageDialog(null, msg.getMessage(), msg.getTitle() == null ? "" : msg.getTitle(), msg.getLevel().getSwingSeverity());
					}
				}
			}

		} catch (Exception ecc) {
			ecc.printStackTrace();
		}
	}
}
