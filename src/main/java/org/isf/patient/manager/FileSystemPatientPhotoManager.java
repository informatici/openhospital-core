/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
