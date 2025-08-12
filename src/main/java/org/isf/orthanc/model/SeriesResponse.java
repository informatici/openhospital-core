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
public class SeriesResponse extends BaseResponse {
	@SerializedName("MainDicomTags")
	private Series series;

	@SerializedName("ParentStudy")
	private String parentStudyId;

	@SerializedName("Instances")
	private List<String> instancesIds;

	@SerializedName("ExpectedNumberOfInstances")
	private String expectedNumberOfInstances;

	@SerializedName("Status")
	private String status;

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public String getParentStudyId() {
		return parentStudyId;
	}

	public void setParentStudyId(String parentStudyId) {
		this.parentStudyId = parentStudyId;
	}

	public List<String> getInstancesIds() {
		return instancesIds;
	}

	public void setInstancesIds(List<String> instancesIds) {
		this.instancesIds = instancesIds;
	}

	public String getExpectedNumberOfInstances() {
		return expectedNumberOfInstances;
	}

	public void setExpectedNumberOfInstances(String expectedNumberOfInstances) {
		this.expectedNumberOfInstances = expectedNumberOfInstances;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
