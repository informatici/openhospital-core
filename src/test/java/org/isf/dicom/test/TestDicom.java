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
package org.isf.dicom.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;

import org.isf.dicom.model.FileDicom;
import org.isf.dicomtype.model.DicomType;
import org.isf.utils.exception.OHException;

public class TestDicom {

	private static String DICOM_DATE_FORMAT_ZONED = "EEE MMM dd HH:mm:ss z yyyy";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DICOM_DATE_FORMAT_ZONED, new Locale("en"));
	private static final String DATE_TIME_STRING = "Sat Aug 01 10:02:03 AST 2020";

	private Blob dicomData = createRandomBlob(100);
	private int patId = 0;
	private String fileName = "TestFileName";
	private String dicomAccessionNumber = "TestAccessionNumber";
	private String dicomInstitutionName = "TestInsitutionName";
	private String dicomPatientID = "TestPatientId";
	private String dicomPatientName = "TestPatientName";
	private String dicomPatientAddress = "TestPatientAddress";
	private String dicomPatientAge = "TestPatientAge";
	private String dicomPatientSex = "TestPatientSex";
	private String dicomPatientBirthDate = "TestPatientBirth";
	private String dicomStudyId = "TestStudyId";
	private LocalDateTime dicomStudyDate = LocalDateTime.parse(DATE_TIME_STRING, DATE_TIME_FORMATTER);
	private String dicomStudyDescription = "TestStudyDescription";
	private String dicomSeriesUID = "TestSeriesUid";
	private String dicomSeriesInstanceUID = "TestSeriesInstanceUid";
	private String dicomSeriesNumber = "TestSeriesNumber";
	private String dicomSeriesDescriptionCodeSequence = "TestSeriesDescription";
	private LocalDateTime dicomSeriesDate = LocalDateTime.parse(DATE_TIME_STRING, DATE_TIME_FORMATTER);
	private String dicomSeriesDescription = "TestSeriesDescription";
	private String dicomInstanceUID = "TestInteanceUid";
	private String modality = "TestModality";
	private Blob dicomThumbnail = createRandomBlob(66);

	public TestDicom() throws ParseException {
	}

	public FileDicom setup(DicomType dicomType, boolean usingSet) throws OHException {
		FileDicom dicom;

		if (usingSet) {
			dicom = new FileDicom();
			setParameters(dicom, dicomType);
		} else {
			// Create FileDicom with all parameters 
			dicom = new FileDicom(patId, dicomData, 0, fileName, dicomAccessionNumber, dicomInstitutionName, dicomPatientID,
					dicomPatientName, dicomPatientAddress, dicomPatientAge, dicomPatientSex, dicomPatientBirthDate,
					dicomStudyId, dicomStudyDate, dicomStudyDescription, dicomSeriesUID, dicomSeriesInstanceUID,
					dicomSeriesNumber, dicomSeriesDescriptionCodeSequence, dicomSeriesDate, dicomSeriesDescription,
					dicomInstanceUID, modality, dicomThumbnail, dicomType);
		}
		return dicom;
	}

	public void setParameters(FileDicom dicom, DicomType dicomType) {
		dicom.setDicomAccessionNumber(dicomAccessionNumber);
		dicom.setDicomData(dicomData);
		dicom.setDicomInstanceUID(dicomInstanceUID);
		dicom.setDicomInstitutionName(dicomInstitutionName);
		dicom.setDicomPatientAddress(dicomPatientAddress);
		dicom.setDicomPatientAge(dicomPatientAge);
		dicom.setDicomPatientBirthDate(dicomPatientBirthDate);
		dicom.setDicomPatientID(dicomPatientID);
		dicom.setDicomPatientName(dicomPatientName);
		dicom.setDicomPatientSex(dicomPatientSex);
		dicom.setDicomSeriesDate(dicomSeriesDate);
		dicom.setDicomSeriesDescription(dicomSeriesDescription);
		dicom.setDicomSeriesDescriptionCodeSequence(dicomSeriesDescriptionCodeSequence);
		dicom.setDicomSeriesInstanceUID(dicomSeriesInstanceUID);
		dicom.setDicomSeriesNumber(dicomSeriesNumber);
		dicom.setDicomSeriesUID(dicomSeriesUID);
		dicom.setDicomStudyDate(dicomStudyDate);
		dicom.setDicomStudyDescription(dicomStudyDescription);
		dicom.setDicomStudyId(dicomStudyId);
		dicom.setDicomThumbnail(dicomThumbnail);
		dicom.setFileName(fileName);
		dicom.setPatId(patId);
		dicom.setModality(modality);
		dicom.setDicomType(dicomType);
	}

	public void check(FileDicom dicom) {
		assertThat(dicom.getDicomAccessionNumber()).isEqualTo(dicomAccessionNumber);
		assertThat(dicom.getDicomInstanceUID()).isEqualTo(dicomInstanceUID);
		assertThat(dicom.getDicomInstitutionName()).isEqualTo(dicomInstitutionName);
		assertThat(dicom.getDicomPatientAddress()).isEqualTo(dicomPatientAddress);
		assertThat(dicom.getDicomPatientAge()).isEqualTo(dicomPatientAge);
		assertThat(dicom.getDicomPatientBirthDate()).isEqualTo(dicomPatientBirthDate);
		assertThat(dicom.getDicomPatientID()).isEqualTo(dicomPatientID);
		assertThat(dicom.getDicomPatientName()).isEqualTo(dicomPatientName);
		assertThat(dicom.getDicomPatientSex()).isEqualTo(dicomPatientSex);
		assertThat(dicom.getDicomSeriesDate()).isEqualTo(dicomSeriesDate);
		assertThat(dicom.getDicomSeriesDescription()).isEqualTo(dicomSeriesDescription);
		assertThat(dicom.getDicomSeriesDescriptionCodeSequence()).isEqualTo(dicomSeriesDescriptionCodeSequence);
		assertThat(dicom.getDicomSeriesInstanceUID()).isEqualTo(dicomSeriesInstanceUID);
		assertThat(dicom.getDicomSeriesNumber()).isEqualTo(dicomSeriesNumber);
		assertThat(dicom.getDicomSeriesUID()).isEqualTo(dicomSeriesUID);
		assertThat(dicom.getDicomStudyDate()).isEqualTo(dicomStudyDate);
		assertThat(dicom.getDicomStudyDescription()).isEqualTo(dicomStudyDescription);
		assertThat(dicom.getDicomStudyId()).isEqualTo(dicomStudyId);
		assertThat(dicom.getFileName()).isEqualTo(fileName);
		assertThat(dicom.getPatId()).isEqualTo(patId);
		assertThat(dicom.getModality()).isEqualTo(modality);
	}

	public Blob createRandomBlob(int byteCount) {
		Blob blob = null;
		byte[] data;

		data = new byte[byteCount];
		new Random().nextBytes(data);
		try {
			blob = new SerialBlob(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return blob;
	}

}
