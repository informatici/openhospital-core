/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.orthanc.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class Instance {
	@SerializedName("AcquisitionNumber")
	private String acquisitionNumber;

	@SerializedName("ImageOrientationPatient")
	private String imageOrientationPatient;

	@SerializedName("ImagePositionPatient")
	private String imagePositionPatient;

	@SerializedName("InstanceCreationDate")
	private String creationDate;

	@SerializedName("InstanceCreationTime")
	private String creationTime;

	@SerializedName("InstanceNumber")
	private String instanceNumber;

	@SerializedName("SOPInstanceUID")
	private String instanceUID;

	public String getAcquisitionNumber() {
		return acquisitionNumber;
	}

	public void setAcquisitionNumber(String acquisitionNumber) {
		this.acquisitionNumber = acquisitionNumber;
	}

	public String getImageOrientationPatient() {
		return imageOrientationPatient;
	}

	public void setImageOrientationPatient(String imageOrientationPatient) {
		this.imageOrientationPatient = imageOrientationPatient;
	}

	public String getImagePositionPatient() {
		return imagePositionPatient;
	}

	public void setImagePositionPatient(String imagePositionPatient) {
		this.imagePositionPatient = imagePositionPatient;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public String getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getInstanceUID() {
		return instanceUID;
	}

	public void setInstanceUID(String instanceUID) {
		this.instanceUID = instanceUID;
	}

	/**
	 * Parse instance creation date into LocalDate
	 * @return {@link LocalDate} instance
	 */
	public LocalDate creationDateToLocalDate() {
		if (creationDate == null || creationDate.isEmpty()) {
			return  null;
		}

		return LocalDate.parse(creationDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	/**
	 * Parse instance creation time into LocalTime
	 * @return {@link LocalTime} instance
	 */
	public LocalTime creationTimeToLocalTime() {
		if (creationTime == null || creationTime.isEmpty()) {
			return  null;
		}

		return LocalTime.parse(creationTime, DateTimeFormatter.ofPattern("HHmmss"));
	}
}
