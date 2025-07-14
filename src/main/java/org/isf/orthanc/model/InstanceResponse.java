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
public class InstanceResponse {
	@SerializedName("ID")
	private String id;

	@SerializedName("FileSize")
	private Integer fileSize;

	@SerializedName("FileUuid")
	private String fileUuid;

	@SerializedName("IndexInSeries")
	private Integer indexInSeries;

	@SerializedName("Labels")
	private List<String> labels;

	@SerializedName("MainDicomTags")
	private Instance instance;

	@SerializedName("ParentSeries")
	private String parentSeriesId;

	@SerializedName("Type")
	private String objectType;

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public String getParentSeriesId() {
		return parentSeriesId;
	}

	public void setParentSeriesId(String parentSeriesId) {
		this.parentSeriesId = parentSeriesId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public void setFileUuid(String fileUuid) {
		this.fileUuid = fileUuid;
	}

	public Integer getIndexInSeries() {
		return indexInSeries;
	}

	public void setIndexInSeries(Integer indexInSeries) {
		this.indexInSeries = indexInSeries;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
}
