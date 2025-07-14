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

import com.google.gson.annotations.SerializedName;

/**
 * @author Silevester D.
 * @since 1.15
 */
public class Series {
	@SerializedName("BodyPartExamined")
	private String bodyPartExamined;

	@SerializedName("ImageOrientationPatient")
	private String imageOrientationPatient;

	@SerializedName("Manufacturer")
	private String manufacturer;

	@SerializedName("Modality")
	private String modality;

	@SerializedName("OperatorsName")
	private String operatorsName;

	@SerializedName("ProtocolName")
	private String protocolName;

	@SerializedName("SeriesDescription")
	private String seriesDescription;

	@SerializedName("SeriesInstanceUID")
	private String instanceUID;

	@SerializedName("SeriesNumber")
	private String number;

	@SerializedName("StationName")
	private String stationName;

	public String getBodyPartExamined() {
		return bodyPartExamined;
	}

	public void setBodyPartExamined(String bodyPartExamined) {
		this.bodyPartExamined = bodyPartExamined;
	}

	public String getImageOrientationPatient() {
		return imageOrientationPatient;
	}

	public void setImageOrientationPatient(String imageOrientationPatient) {
		this.imageOrientationPatient = imageOrientationPatient;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getOperatorsName() {
		return operatorsName;
	}

	public void setOperatorsName(String operatorsName) {
		this.operatorsName = operatorsName;
	}

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public String getSeriesDescription() {
		return seriesDescription;
	}

	public void setSeriesDescription(String seriesDescription) {
		this.seriesDescription = seriesDescription;
	}

	public String getInstanceUID() {
		return instanceUID;
	}

	public void setInstanceUID(String instanceUID) {
		this.instanceUID = instanceUID;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
}
