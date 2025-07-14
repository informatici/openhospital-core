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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class BaseResponse {
	@SerializedName("ID")
	private String id;

	@SerializedName("IsStable")
	private Boolean isStable;

	@SerializedName("Type")
	private String objectType;

	@SerializedName("Labels")
	private List<String> labels;

	@SerializedName("LastUpdate")
	private String lastUpdate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getStable() {
		return isStable;
	}

	public void setStable(Boolean stable) {
		isStable = stable;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Parse last update date into LocalDateTime
	 *
	 * @return {@link LocalDateTime} instance
	 */
	public LocalDateTime lastUpdateToLocalDateTime() {
		if (lastUpdate == null || lastUpdate.isEmpty()) {
			return  null;
		}
		return LocalDateTime.parse(lastUpdate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
	}
}
