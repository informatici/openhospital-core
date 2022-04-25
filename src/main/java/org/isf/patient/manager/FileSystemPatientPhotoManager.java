package org.isf.patient.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("fileSystemPatientPhotoManager")
public class FileSystemPatientPhotoManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemPatientPhotoManager.class);

	public boolean exist(String path, Integer patientId) {
		File patientIdFolder = new File(path);
		File f = new File(patientIdFolder, patientId + ".jpg");
		return (f.exists() && !f.isDirectory());
	}

	public void loadInPatient(Patient patient, String path) throws OHServiceException {
		try {
			PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();
			patient.setPatientProfilePhoto(patientProfilePhoto);
			patientProfilePhoto.setPatient(patient);
			if (exist(path, patient.getCode())) {
				Blob blob = this.load(patient.getCode(), path);
				int blobLength;
				blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				patientProfilePhoto.setPhoto(blobAsBytes);
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new OHServiceException(
					new OHExceptionMessage(MessageBundle.getMessage("angal.patient.patientphoto.error.title"),
							MessageBundle.formatMessage("angal.patient.patientphoto.error.filenotfound.msg"),
							OHSeverityLevel.ERROR));
		}

	}

	public void save(String path, Integer patId, byte[] blob) throws OHServiceException {
		try {
			File patientIdFolder = new File(path);
			this.recourse(patientIdFolder);
			File data = new File(patientIdFolder, patId + ".jpg");
			save(data, blob);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg"), OHSeverityLevel.ERROR));
		}
	}

	private Blob load(Integer patientId, String path) throws OHServiceException {
		try {
			File patientIdFolder = new File(path);
			File fdc = new File(patientIdFolder, patientId + ".jpg");
			byte[] byteArray;
			try (FileInputStream fis = new FileInputStream(fdc)) {
				byteArray = new byte[fis.available()];
				fis.read(byteArray);
			}
			return new SerialBlob(byteArray);
		} catch (IOException | SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new OHServiceException(
					new OHExceptionMessage(MessageBundle.getMessage("angal.patient.patientphoto.error.title"),
							MessageBundle.formatMessage("angal.patient.patientphoto.error.filenotfound.msg"),
							OHSeverityLevel.ERROR));
		}
	}

	private void recourse(File f) throws IOException {
		if (f.exists()) {
			return;
		}
		File fp = f.getParentFile();

		if (fp != null) {
			recourse(fp);
		}

		if (!f.exists()) {
			f.mkdir();
		}
	}

	private void save(File outFile, byte[] content) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(outFile)) {
			fos.write(content);
			fos.flush();
		}
	}

}
