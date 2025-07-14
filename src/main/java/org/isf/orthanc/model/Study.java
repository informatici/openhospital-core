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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class Study {
	@SerializedName("AccessionNumber")
	private String accessionNumber;

	@SerializedName("InstitutionName")
	private String institutionName;

	@SerializedName("ReferringPhysicianName")
	private String referringPhysicianName;

	@SerializedName("StudyDate")
	private String date;

	@SerializedName("StudyTime")
	private String time;

	@SerializedName("StudyDescription")
	private String description;

	@SerializedName("StudyID")
	private String id;

	@SerializedName("StudyInstanceUID")
	private String instanceUID;

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getReferringPhysicianName() {
		return referringPhysicianName;
	}

	public void setReferringPhysicianName(String referringPhysicianName) {
		this.referringPhysicianName = referringPhysicianName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceUID() {
		return instanceUID;
	}

	public void setInstanceUID(String instanceUID) {
		this.instanceUID = instanceUID;
	}

	/**
	 * Parse study date into LocalDate
	 * @return {@link LocalDate} instance
	 */
	public LocalDate dateToLocalDate() {
		if (date == null || date.isEmpty()) {
			return  null;
		}

		return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	/**
	 * Parse study time into LocalTime
	 * @return {@link LocalTime} instance
	 */
	public LocalTime timeToLocalTime() {
		if (time == null || time.isEmpty()) {
			return  null;
		}

		return LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmmss"));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Study study = (Study) o;
		return Objects.equals(id, study.id) && Objects.equals(instanceUID, study.instanceUID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, instanceUID);
	}
}
