package org.isf.patient.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.rowset.serial.SerialBlob;

import org.isf.generaldata.GeneralData;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.model.Patient;
import org.isf.patient.model.PatientProfilePhoto;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("fileSystemPatientPhotoManager")
public class FileSystemPatientPhotoManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemPatientPhotoManager.class);

	public boolean exist(String path, Integer patientId) {
		File patientIdFolder;
		try {
			patientIdFolder = this.getPatientDir(path, patientId);
			File f = new File(patientIdFolder, patientId + ".png");
			return (f.exists() && !f.isDirectory());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	public void loadInPatient(Patient patient, String path) throws OHServiceException {
		try {
			PatientProfilePhoto patientProfilePhoto = new PatientProfilePhoto();
			patient.setPatientProfilePhoto(patientProfilePhoto);
			patientProfilePhoto.setPatient(patient);
			if (exist(path, patient.getCode())) {
				Blob blob = this.load(patient.getCode(), path);
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				patientProfilePhoto.setPhoto(blobAsBytes);
			}
		} catch (OHServiceException | SQLException e) {
			LOGGER.error(e.getMessage(), e);
			// TODO write down error messages
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg"), OHSeverityLevel.ERROR));
		}
	}

	public void save(String path, Integer patId, byte[] blob) throws OHServiceException {
		try {
			File patientIdFolder = this.getPatientDir(path, patId);
			File data = new File(patientIdFolder, patId + ".png");
			save(data, blob);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg"), OHSeverityLevel.ERROR));
		}
	}

	private Blob load(Integer patientId, String path) throws OHServiceException {
		try {
			File patientIdFolder = this.getPatientDir(path, patientId);
			File fdc = new File(patientIdFolder, patientId + ".png");
			byte[] byteArray;
			try (FileInputStream fis = new FileInputStream(fdc)) {
				byteArray = new byte[fis.available()];
				fis.read(byteArray);
			}
			return new SerialBlob(byteArray);
		} catch (IOException | SQLException e) {
			LOGGER.error(e.getMessage(), e);
			// TODO write down error messages
			throw new OHServiceException(new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.formatMessage("angal.dicommanager.genericerror.fmt.msg"), OHSeverityLevel.ERROR));
		}
	}

	private File getPatientDir(String path, Integer patId) throws IOException {
		File f = new File(new File(path), patId.toString());
		recourse(f);
		return f;
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
