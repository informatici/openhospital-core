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
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.dicom.model;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BLOB related to a {@link FileDicom}
 */
@Entity
@Table(name = "OH_DICOM_DATA")
public class DicomData {

	private static final Logger LOGGER = LoggerFactory.getLogger(DicomData.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DMD_DATA_ID")
	private long code;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DMD_FILE_ID")
	private FileDicom fileDicom;

	@Column(name = "DMD_DATA")
	@Lob
	private Blob data;

	public DicomData() {
	}

	public DicomData(Blob data) {
		this.data = data;
	}

	public FileDicom getFileDicom() {
		return fileDicom;
	}

	public void setFileDicom(FileDicom fileDicom) {
		this.fileDicom = fileDicom;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	/**
	 * Loads bytes of DICOM file and stores it in a Blob type
	 *
	 * @param dicomFile the dicomFile to set
	 */
	public void setData(File dicomFile) {
		try (FileInputStream fis = new FileInputStream(dicomFile)) {
			byte[] byteArray = new byte[fis.available()];
			fis.read(byteArray);
			this.data = new SerialBlob(byteArray);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
	}
}
