/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.dicom.manager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.dcm4che3.util.SafeClose;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;
import org.isf.dicom.model.FileDicom;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.isf.utils.file.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * Manager for DICOM Files
 *
 * @author Pietro Castellucci
 * @version 1.0.0
 */
public class SourceFiles extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceFiles.class);

	private File file;
	private FileDicom fileDicom;
	private int patient;
	private int filesCount;
	private int filesLoaded = 0;
	private AbstractDicomLoader dicomLoader;
	private AbstractThumbnailViewGui thumbnail;

	public SourceFiles(FileDicom fileDicom, File sourceFile, int patient, int filesCount, AbstractThumbnailViewGui thumbnail, AbstractDicomLoader frame) {
		this.patient = patient;
		this.file = sourceFile;
		this.fileDicom = fileDicom;
		this.filesCount = filesCount;
		this.thumbnail = thumbnail;
		this.dicomLoader = frame;
		start();
	}

	@Override
	public void run() {
		try {
			loadDicomDir(fileDicom, file, patient);
		} catch (Exception e) {
			LOGGER.error("loadDicomDir", e);
		}
		dicomLoader.setVisible(false);
		thumbnail.initialize();
	}

	/**
	 * Load a DICOM directory
	 *
	 * @throws Exception
	 */
	private void loadDicomDir(FileDicom fileDicom, File sourceFile, int patient) throws Exception {
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
			} catch (Exception e) {}

			if (!value.isDirectory()) {
				try {
					loadDicom(fileDicom, value, patient);
				} catch (Exception e) {
					if (e instanceof OHDicomException) {
						LOGGER.error("loadDicomDir: {}", ((OHDicomException) e).getMessages().get(0).getMessage());
					} else {
						throw e;
					}
				}
				filesLoaded++;
				dicomLoader.setLoaded(filesLoaded);
			} else if (!".".equals(value.getName()) && !"..".equals(value.getName()))
				loadDicomDir(fileDicom, value, patient);
		}
	}

	public static boolean checkSize(File sourceFile) throws OHDicomException {

		return DicomManagerFactory.getMaxDicomSizeLong() > sourceFile.length();
	}

	public static int countFiles(File sourceFile, int patient) throws OHDicomException {
		int num = 0;

		File[] files = sourceFile.listFiles();

		for (File value : files) {
			if (!value.isDirectory()) {
				if (!checkSize(value)) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.formatMessage("angal.dicom.afileinthefolderistoobigpleasesetdicommaxsizeindicomproperties.fmt.msg",
									DicomManagerFactory.getMaxDicomSize()),
							OHSeverityLevel.ERROR));
				}
				num++;
			} else if (!".".equals(value.getName()) && !"..".equals(value.getName()))
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
	 * PreLoad dicom file for validation in gui with some
	 * data from filesystem
	 *
	 * @param sourceFile
	 * @param numfiles
	 */
	public static FileDicom preLoadDicom(File sourceFile, int numfiles) {
		FileDicom dicomFileDetail = new FileDicom();
		try {
			String fileName = sourceFile.getName();
			LocalDateTime seriesDate = null;
			LocalDateTime studyDate = null;
			boolean isJpeg = StringUtils.endsWithIgnoreCase(fileName, ".jpg") || StringUtils.endsWithIgnoreCase(fileName, ".jpeg");
			boolean isDicom = StringUtils.endsWithIgnoreCase(fileName, ".dcm");
			if (isJpeg) {
				studyDate = FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
			} else if (isDicom) {
				DicomInputStream dicomInputStream;
				try {
			        dicomInputStream = new DicomInputStream(sourceFile);
				} catch(DicomStreamException dicomStreamException) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					                                                  MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", fileName),
					                                                  OHSeverityLevel.ERROR));
				}
				Attributes attributes = dicomInputStream.readDataset();
				seriesDate = getSeriesDateTime(attributes);
				studyDate = getStudyDateTime(attributes);
				if (attributes.contains(Tag.SeriesNumber)) {
					dicomFileDetail.setDicomSeriesNumber(attributes.getString(Tag.SeriesNumber));
				} else {
					LOGGER.error("DICOM: Unparsable SeriesNumber");
				}
			} else {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
				                                                  MessageBundle.formatMessage("angal.dicom.dicomformatnotsupported.fmt.msg", fileName),
				                                                  OHSeverityLevel.ERROR));
			}
			dicomFileDetail.setFrameCount(numfiles);
			dicomFileDetail.setDicomData(sourceFile);
			dicomFileDetail.setFileName(fileName);
			dicomFileDetail.setDicomSeriesDate(seriesDate);
			dicomFileDetail.setDicomStudyDate(studyDate);

			return dicomFileDetail;

		} catch (OHDicomException e) {
			LOGGER.error(e.getMessages().get(0).getMessage());
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return dicomFileDetail;
	}

	private static LocalDateTime getSeriesDateTime(Attributes attributes) {
		if (attributes.contains(Tag.SeriesDate) && attributes.getString(Tag.SeriesDate) != null) {
			return LocalDateTime.ofInstant(attributes.getDate(Tag.SeriesDateAndTime).toInstant(), ZoneId.systemDefault());
		}
		LOGGER.error("DICOM: Unparsable SeriesDate: date={}  time={}", attributes.getString(Tag.SeriesDate), attributes.getString(Tag.SeriesTime));
        return null;
	}

	public static LocalDateTime getStudyDateTime(Attributes attributes) {
		if (attributes.contains(Tag.StudyDate) && attributes.getString(Tag.StudyDate) != null) {
			return LocalDateTime.ofInstant(attributes.getDate(Tag.StudyDateAndTime).toInstant(), ZoneId.systemDefault());
		}
		LOGGER.error("DICOM: Unparsable StudyDate: date={}  time={}", attributes.getString(Tag.StudyDate), attributes.getString(Tag.StudyTime));
		return null;
	}

	/**
	 * Load dicom file
	 *
	 * @param dicomFileDetail
	 * @param sourceFile
	 * @param patient
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static synchronized void loadDicom(FileDicom dicomFileDetail, File sourceFile, int patient) throws Exception {
		// installLibs();

		if (".DS_Store".equals(sourceFile.getName()))
			return;

		try {
			boolean isJpeg = StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpg") || StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpeg");
			boolean isDicom = StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".dcm");

			ImageReader reader;
			ImageReadParam param;
			BufferedImage originalImage;
			Iterator<?> iter;
			if (isJpeg) {
				iter = ImageIO.getImageReadersByFormatName("jpeg");
				if (!iter.hasNext()) {
					LOGGER.error("Could not instantiate JPEGImageReader");
					throw new IIOException("Could not instantiate JPEGImageReader");
				}
				reader = (ImageReader) iter.next();
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);

				reader.setInput(imageInputStream, false);

				originalImage = null;

				try {
					originalImage = reader.read(0);

					int orientation = checkOrientation(sourceFile);

					if (orientation != 1) {
						originalImage = autoRotate(originalImage, orientation);
						String fileType = StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpg") ? "jpg" : "jpeg";
						File f = File.createTempFile(sourceFile.getName(), '.' + fileType);
						ImageIO.write(originalImage, fileType, f);
						sourceFile = f;
					}
				} catch (IIOException | RuntimeException exception) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", sourceFile.getName()),
							OHSeverityLevel.ERROR));
				}
				imageInputStream.close();
			} else if (isDicom) {
				iter = ImageIO.getImageReadersByFormatName("DICOM");
				reader = (ImageReader) iter.next();
				param = reader.getDefaultReadParam();
				DicomInputStream dicomStream = null;
				ByteArrayInputStream byteArrayInputStream = null;
				try {
					byte[] data = Files.readAllBytes(Paths.get(sourceFile.getAbsolutePath()));
					byteArrayInputStream = new ByteArrayInputStream(data);
					dicomStream = new DicomInputStream(byteArrayInputStream);
					reader.setInput(dicomStream);
					originalImage = reader.read(0, param);
				} catch (IOException | RuntimeException exception) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
							MessageBundle.formatMessage("angal.dicom.thefileisnotindicomformat.fmt.msg", sourceFile.getName()), OHSeverityLevel.ERROR));
				}
				finally {
					SafeClose.close(dicomStream);
					SafeClose.close(byteArrayInputStream);
				}
			} else {
				throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
						MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", sourceFile.getName()),
						OHSeverityLevel.ERROR));
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
			LocalDateTime seriesDate = dicomFileDetail.getDicomSeriesDate();
			String seriesDescription = dicomFileDetail.getDicomSeriesDescription();
			String seriesDescriptionCodeSequence = dicomFileDetail.getDicomSeriesDescriptionCodeSequence();
			String seriesNumber = dicomFileDetail.getDicomSeriesNumber();
			String seriesInstanceUID = dicomFileDetail.getDicomSeriesInstanceUID();
			String seriesUID = dicomFileDetail.getDicomSeriesUID();
			LocalDateTime studyDate = dicomFileDetail.getDicomStudyDate();
			String studyDescription = dicomFileDetail.getDicomStudyDescription();
			String studyUID = dicomFileDetail.getDicomStudyId();
			String modality = dicomFileDetail.getModality();
			if (isJpeg) {
				//overridden by the user
				seriesDate = seriesDate != null ? seriesDate : FileTools.getTimestamp(sourceFile); //get last modified date (creation date)
				studyDate = studyDate != null ? studyDate : FileTools.getTimestamp(sourceFile); //get last modified date (creation date)

				//set by the system
				seriesNumber = !seriesNumber.isEmpty() ? seriesNumber : generateSeriesNumber(patient);
				seriesInstanceUID = !seriesInstanceUID.isEmpty() ? seriesInstanceUID : "<org_root>." + seriesNumber;
				
				//in loadDicomDir loop this is generated because is missing in JPG/JPEG files, reset to avoid duplicates
				studyUID = ""; 
			} else if (isDicom) {

				DicomInputStream dicomInputStream;
				try {
					dicomInputStream = new DicomInputStream(sourceFile);
				} catch(DicomStreamException dicomStreamException) {
					throw new OHDicomException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
																	  MessageBundle.formatMessage("angal.dicom.thefileisnotindicomformat.fmt.msg", sourceFile.getName()),
																	  OHSeverityLevel.ERROR));
				}
				Attributes attributes = dicomInputStream.readDataset();

				//overridden by the user
				seriesDescription = seriesDescription != null ? seriesDescription : attributes.getString(Tag.SeriesDescription);
				studyDate = studyDate != null ? studyDate : getStudyDateTime(attributes);
				seriesDate = seriesDate != null ? seriesDate : getSeriesDateTime(attributes);

				//set by DICOM properties
				patientID = attributes.getString(Tag.PatientID) == null ? patientID : attributes.getString(Tag.PatientID);
				patientName = attributes.getString(Tag.PatientName) == null ? patientName : attributes.getString(Tag.PatientName);
				patientAddress = attributes.getString(Tag.PatientAddress) == null ? patientAddress : attributes.getString(Tag.PatientAddress);
				patientAge = attributes.getString(Tag.PatientAge) == null ? patientAge : attributes.getString(Tag.PatientAge);
				//String acquisitionsInSeries = attributes.getString(Tag.AcquisitionsInSeries);
				//String acquisitionsInStudy = attributes.getString(Tag.AcquisitionsInStudy);
				//String applicatorDescription = attributes.getString(Tag.ApplicatorDescription);
				//String dicomMediaRetrievalSequence = attributes.getString(Tag.DICOMMediaRetrievalSequence);
				//String patientComments = dicomObject.attributes(Tag.PatientComments);
				try {
					patientBirthDate = attributes.getDate(Tag.PatientBirthDate) == null ?
							patientBirthDate :
							DateFormat.getDateInstance().format(attributes.getDate(Tag.PatientBirthDate));
				} catch (Exception ecc) {
				}
				patientSex = attributes.getString(Tag.PatientSex) == null ? patientSex : attributes.getString(Tag.PatientSex);
				modality = attributes.getString(Tag.Modality) == null ? modality : attributes.getString(Tag.Modality);
				studyUID = attributes.getString(Tag.StudyInstanceUID) == null ? studyUID : attributes.getString(Tag.StudyInstanceUID);
				accessionNumber = attributes.getString(Tag.AccessionNumber) == null ? accessionNumber : attributes.getString(Tag.AccessionNumber);
				studyDescription = attributes.getString(Tag.StudyDescription) == null ? studyDescription : attributes.getString(Tag.StudyDescription);
				seriesUID = attributes.getString(Tag.SeriesInstanceUID) == null ? seriesUID : attributes.getString(Tag.SeriesInstanceUID);
				seriesInstanceUID = attributes.getString(Tag.SeriesInstanceUID) == null ? seriesInstanceUID : attributes.getString(Tag.SeriesInstanceUID);
				seriesNumber = attributes.getString(Tag.SeriesNumber) == null ? generateSeriesNumber(patient) : attributes.getString(Tag.SeriesNumber);
				seriesDescriptionCodeSequence = attributes.getString(Tag.SeriesDescriptionCodeSequence) == null ?
						seriesDescriptionCodeSequence :
						attributes.getString(Tag.SeriesDescriptionCodeSequence);
				institutionName = attributes.getString(Tag.InstitutionName) == null ? institutionName : attributes.getString(Tag.InstitutionName);
				instanceUID = attributes.getString(Tag.SOPInstanceUID) == null ? instanceUID : attributes.getString(Tag.SOPInstanceUID);
			}

			// Loaded... Update dicomFileDetail
			if (sourceFile != null)
				dicomFileDetail.setDicomData(sourceFile);
			if (sourceFile.getName() != null)
				dicomFileDetail.setFileName(sourceFile.getName());
			if (accessionNumber != null)
				dicomFileDetail.setDicomAccessionNumber(accessionNumber);
			if (instanceUID != null)
				dicomFileDetail.setDicomInstanceUID(instanceUID);
			if (institutionName != null)
				dicomFileDetail.setDicomInstitutionName(institutionName);
			if (patientAddress != null)
				dicomFileDetail.setDicomPatientAddress(patientAddress);
			if (patientAge != null)
				dicomFileDetail.setDicomPatientAge(patientAge);
			if (patientBirthDate != null)
				dicomFileDetail.setDicomPatientBirthDate(patientBirthDate);
			if (patientID != null)
				dicomFileDetail.setDicomPatientID(patientID);
			if (patientName != null)
				dicomFileDetail.setDicomPatientName(patientName);
			if (patientSex != null)
				dicomFileDetail.setDicomPatientSex(patientSex);
			if (seriesDate != null)
				dicomFileDetail.setDicomSeriesDate(seriesDate);
			if (seriesDescription != null)
				dicomFileDetail.setDicomSeriesDescription(seriesDescription);
			if (seriesDescriptionCodeSequence != null)
				dicomFileDetail.setDicomSeriesDescriptionCodeSequence(seriesDescriptionCodeSequence);
			if (seriesInstanceUID != null)
				dicomFileDetail.setDicomSeriesInstanceUID(seriesInstanceUID);
			if (seriesNumber != null)
				dicomFileDetail.setDicomSeriesNumber(seriesNumber);
			if (seriesUID != null)
				dicomFileDetail.setDicomSeriesUID(seriesUID);
			if (studyDate != null)
				dicomFileDetail.setDicomStudyDate(studyDate);
			if (studyDescription != null)
				dicomFileDetail.setDicomStudyDescription(studyDescription);
			if (studyUID != null)
				dicomFileDetail.setDicomStudyId(studyUID);
			if (patient != 0)
				dicomFileDetail.setPatId(patient);
			if (scaled != null)
				dicomFileDetail.setDicomThumbnail(scaled);
			if (modality != null)
				dicomFileDetail.setModality(modality);
			dicomFileDetail.setIdFile(0); //it trigger the DB save with SqlDicomManager
			try {
				DicomManagerFactory.getManager().saveFile(dicomFileDetail);
				//dicomFileDetail.setDicomSeriesNumber(dicom.getDicomSeriesNumber()); //series number could be generated if missing.
			} catch (OHServiceException ex) {
				if (ex.getMessages() != null) {
					throw new OHDicomException(ex.getCause(), ex.getMessages());
				}
			}

		} catch (OHDicomException ecc) {
			throw ecc;
		}
	}

	public static int checkOrientation(File sourceFile) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(sourceFile);
		ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		int orientation = 1;
		try {
			orientation = exifIFD0Directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
		} catch (Exception ex) {
			LOGGER.debug("No EXIF information found for image: {}", sourceFile.getName());
		}
		return orientation;
	}

	private static BufferedImage autoRotate(BufferedImage originalImage, int orientation)
			throws MetadataException {
		switch (orientation) {
			case 1:
				break;
			case 2: // Flip X
				originalImage = Scalr.rotate(originalImage, Rotation.FLIP_HORZ);
				break;
			case 3: // PI rotation
				originalImage = Scalr.rotate(originalImage, Rotation.CW_180);
				break;
			case 4: // Flip Y
				originalImage = Scalr.rotate(originalImage, Rotation.FLIP_VERT);
				break;
			case 5: // - PI/2 and Flip X
				originalImage = Scalr.rotate(originalImage, Rotation.CW_90);
				originalImage = Scalr.rotate(originalImage, Rotation.FLIP_HORZ);
				break;
			case 6: // -PI/2 and -width
				originalImage = Scalr.rotate(originalImage, Rotation.CW_90);
				break;
			case 7: // PI/2 and Flip
				originalImage = Scalr.rotate(originalImage, Rotation.CW_90);
				originalImage = Scalr.rotate(originalImage, Rotation.FLIP_VERT);
				break;
			case 8: // PI / 2
				originalImage = Scalr.rotate(originalImage, Rotation.CW_270);
				break;
			default:
				break;
		}

		return originalImage;
	}

	/**
	 * Creates a new unique series number
	 *
	 * @return the new unique code.
	 * @throws OHServiceException if an error occurs during the code generation.
	 */
	public static String generateSeriesNumber(int patient) throws OHServiceException {
		Random random = new Random();
		long candidateCode;
		boolean exists;
		do {
			candidateCode = Math.abs(random.nextLong());
			exists = DicomManagerFactory.getManager().exist(patient, String.valueOf(candidateCode));

		} while (exists);

		return String.valueOf(candidateCode);
	}
}
