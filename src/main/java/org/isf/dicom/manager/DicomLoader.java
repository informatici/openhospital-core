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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomStreamException;
import org.dcm4che3.util.SafeClose;
import org.isf.dicom.model.FileDicom;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDicomException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.file.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

public class DicomLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicomLoader.class);

	/**
	 * Load dicom file
	 *
	 * @param dicomFileDetail
	 * @param sourceFile
	 * @param patient
	 * @throws Exception
	 */

	public static void loadDicom(FileDicom dicomFileDetail, File sourceFile, int patient) throws Exception {
		if (".DS_Store".equals(sourceFile.getName())) {
			return;
		}

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
					throw new OHDicomException(
						new OHExceptionMessage(MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", sourceFile.getName())));
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
					throw new OHDicomException(
						new OHExceptionMessage(MessageBundle.formatMessage("angal.dicom.thefileisnotindicomformat.fmt.msg", sourceFile.getName())));
				}
				finally {
					SafeClose.close(dicomStream);
					SafeClose.close(byteArrayInputStream);
				}
			} else {
				throw new OHDicomException(
					new OHExceptionMessage(MessageBundle.formatMessage("angal.dicom.thefileisinanunknownformat.fmt.msg", sourceFile.getName())));
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
				} catch (DicomStreamException dicomStreamException) {
					throw new OHDicomException(
						new OHExceptionMessage(MessageBundle.formatMessage("angal.dicom.thefileisnotindicomformat.fmt.msg", sourceFile.getName())));
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
			if (sourceFile != null) {
				dicomFileDetail.setDicomData(sourceFile);
			}
			if (sourceFile.getName() != null) {
				dicomFileDetail.setFileName(sourceFile.getName());
			}
			if (accessionNumber != null) {
				dicomFileDetail.setDicomAccessionNumber(accessionNumber);
			}
			if (instanceUID != null) {
				dicomFileDetail.setDicomInstanceUID(instanceUID);
			}
			if (institutionName != null) {
				dicomFileDetail.setDicomInstitutionName(institutionName);
			}
			if (patientAddress != null) {
				dicomFileDetail.setDicomPatientAddress(patientAddress);
			}
			if (patientAge != null) {
				dicomFileDetail.setDicomPatientAge(patientAge);
			}
			if (patientBirthDate != null) {
				dicomFileDetail.setDicomPatientBirthDate(patientBirthDate);
			}
			if (patientID != null) {
				dicomFileDetail.setDicomPatientID(patientID);
			}
			if (patientName != null) {
				dicomFileDetail.setDicomPatientName(patientName);
			}
			if (patientSex != null) {
				dicomFileDetail.setDicomPatientSex(patientSex);
			}
			if (seriesDate != null) {
				dicomFileDetail.setDicomSeriesDate(seriesDate);
			}
			if (seriesDescription != null) {
				dicomFileDetail.setDicomSeriesDescription(seriesDescription);
			}
			if (seriesDescriptionCodeSequence != null) {
				dicomFileDetail.setDicomSeriesDescriptionCodeSequence(seriesDescriptionCodeSequence);
			}
			if (seriesInstanceUID != null) {
				dicomFileDetail.setDicomSeriesInstanceUID(seriesInstanceUID);
			}
			if (seriesNumber != null) {
				dicomFileDetail.setDicomSeriesNumber(seriesNumber);
			}
			if (seriesUID != null) {
				dicomFileDetail.setDicomSeriesUID(seriesUID);
			}
			if (studyDate != null) {
				dicomFileDetail.setDicomStudyDate(studyDate);
			}
			if (studyDescription != null) {
				dicomFileDetail.setDicomStudyDescription(studyDescription);
			}
			if (studyUID != null) {
				dicomFileDetail.setDicomStudyId(studyUID);
			}
			if (patient != 0) {
				dicomFileDetail.setPatId(patient);
			}
			if (scaled != null) {
				dicomFileDetail.setDicomThumbnail(scaled);
			}
			if (modality != null) {
				dicomFileDetail.setModality(modality);
			}
			dicomFileDetail.setIdFile(0); //it will trigger the DB save with SqlDicomManager
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
	public static BufferedImage loadImage(File sourceFile) throws IOException, OHDicomException {
		ImageReader reader;
		ImageReadParam param;
		BufferedImage originalImage;

		if (StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpg") ||
			StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpeg")) {
			reader = ImageIO.getImageReadersByFormatName("jpeg").next();
			ImageInputStream imageInputStream = ImageIO.createImageInputStream(sourceFile);

			reader.setInput(imageInputStream, false);

			originalImage = null;

			try {
				originalImage = reader.read(0);

				int orientation = checkOrientation(sourceFile);

				if (orientation != 1) {
					originalImage = autoRotate(originalImage, orientation);
					String fileType = StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".jpg") ? "jpg" : "jpeg";
					File tempFile = File.createTempFile(sourceFile.getName(), '.' + fileType);
					ImageIO.write(originalImage, fileType, tempFile);
					sourceFile = tempFile;
				}
			} catch (IIOException | RuntimeException exception) {
				throw new OHDicomException(
					new OHExceptionMessage("Error loading image from file: " + sourceFile.getName()));
			} finally {
				imageInputStream.close();
			}
		} else if (StringUtils.endsWithIgnoreCase(sourceFile.getName(), ".dcm")) {
			reader = ImageIO.getImageReadersByFormatName("DICOM").next();
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
				throw new OHDicomException(
					new OHExceptionMessage("Error loading DICOM image from file: " + sourceFile.getName()));
			} finally {
				SafeClose.close(dicomStream);
				SafeClose.close(byteArrayInputStream);
			}
		} else {
			throw new OHDicomException(
				new OHExceptionMessage("Unsupported image format: " + sourceFile.getName()));
		}

		return originalImage;
	}


	public static int checkOrientation(File sourceFile) {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(sourceFile);
			ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			int orientation = 1;

			try {
				orientation = exifIFD0Directory.getInt(ExifDirectoryBase.TAG_ORIENTATION);
			} catch (Exception ex) {
				LOGGER.debug("No EXIF information found for image: {}", sourceFile.getName());
			}

			return orientation;
		} catch (ImageProcessingException | IOException e) {
			LOGGER.error("Error checking image orientation: {}", e.getClass());
			return 1; // Default to no rotation if an error occurs
		}
	}

	public static LocalDateTime getSeriesDateTime(Attributes attributes) {
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

	public static BufferedImage autoRotate(BufferedImage originalImage, int orientation) {
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

}
