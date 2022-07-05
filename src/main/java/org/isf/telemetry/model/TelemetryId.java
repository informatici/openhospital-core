/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.telemetry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class TelemetryId implements Serializable {

	private static final long serialVersionUID = -4277078758107847710L;

	@NotNull
	@Column(name = "TEL_UUID")
	private String softwareUUID;

	@NotNull
	@Column(name = "TEL_DBID")
	private String databaseUUID;

	@NotNull
	@Column(name = "TEL_HWID")
	private String hardwareUUID;

	@NotNull
	@Column(name = "TEL_OSID")
	private String operativeSystemUUID;

	public String getSoftwareUUID() {
		return softwareUUID;
	}

	public void setSoftwareUUID(String softwareUUID) {
		this.softwareUUID = softwareUUID;
	}

	public String getDatabaseUUID() {
		return databaseUUID;
	}

	public void setDatabaseUUID(String databaseUUID) {
		this.databaseUUID = databaseUUID;
	}

	public String getHardwareUUID() {
		return hardwareUUID;
	}

	public void setHardwareUUID(String hardwareUUID) {
		this.hardwareUUID = hardwareUUID;
	}

	public String getOperativeSystemUUID() {
		return operativeSystemUUID;
	}

	public void setOperativeSystemUUID(String operativeSystemUUID) {
		this.operativeSystemUUID = operativeSystemUUID;
	}
}
