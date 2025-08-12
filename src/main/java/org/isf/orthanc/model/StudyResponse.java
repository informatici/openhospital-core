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

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class StudyResponse extends BaseResponse {
	@SerializedName("MainDicomTags")
	private Study study;

	@SerializedName("ParentPatient")
	private String parentPatientId;

	@SerializedName("PatientMainDicomTags")
	private Patient patient;

	@SerializedName("Series")
	private List<String> seriesIds;

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public String getParentPatientId() {
		return parentPatientId;
	}

	public void setParentPatientId(String parentPatientId) {
		this.parentPatientId = parentPatientId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<String> getSeriesIds() {
		return seriesIds;
	}

	public void setSeriesIds(List<String> seriesIds) {
		this.seriesIds = seriesIds;
	}
}
