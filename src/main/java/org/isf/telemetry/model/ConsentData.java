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
package org.isf.telemetry.model;

public class ConsentData {

	// ex. app version
	private Boolean application;
	// ex. OS version
	private Boolean oss;
	// ex. MySQL version
	private Boolean dbms;
	// ex. Italy
	private Boolean location;
	// ex. number of beds
	private Boolean hospital;
	// ex. application last used timestamp
	private Boolean time;

	public Boolean getApplication() {
		return application;
	}

	public void setApplication(Boolean application) {
		this.application = application;
	}

	public Boolean getOss() {
		return oss;
	}

	public void setOss(Boolean oss) {
		this.oss = oss;
	}

	public Boolean getDbms() {
		return dbms;
	}

	public void setDbms(Boolean dbms) {
		this.dbms = dbms;
	}

	public Boolean getLocation() {
		return location;
	}

	public void setLocation(Boolean location) {
		this.location = location;
	}

	public Boolean getHospital() {
		return hospital;
	}

	public void setHospital(Boolean hospital) {
		this.hospital = hospital;
	}

	public Boolean getTime() {
		return time;
	}

	public void setTime(Boolean time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ConsentData [application=" + application + ", oss=" + oss + ", dbms=" + dbms + ", location=" + location + ", hospital=" + hospital + ", time="
						+ time + ']';
	}

}
