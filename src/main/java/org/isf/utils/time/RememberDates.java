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
package org.isf.utils.time;

import java.time.LocalDateTime;

public class RememberDates {

	private static LocalDateTime lastOpdVisitDate;
	private static LocalDateTime lastAdmInDate;
	private static LocalDateTime lastLabExamDate;
	private static LocalDateTime lastBillDate;
	private static LocalDateTime lastPatientVaccineDate;

	//------------  opd visit -----------------------------
	public static LocalDateTime getLastOpdVisitDate() {
		return lastOpdVisitDate;
	}

	public static void setLastOpdVisitDate(LocalDateTime visitDate) {
		lastOpdVisitDate = TimeTools.truncateToSeconds(visitDate);
	}

	//------------  laboratory exam -----------------------
	public static LocalDateTime getLastLabExamDate() {
		return lastLabExamDate;
	}

	public static void setLastLabExamDate(LocalDateTime labDate) {
		lastLabExamDate = TimeTools.truncateToSeconds(labDate);
	}

	//------------  admission date -----------------------
	public static LocalDateTime getLastAdmInDate() {
		return lastAdmInDate;
	}

	public static void setLastAdmInDate(LocalDateTime inDate) {
		lastAdmInDate = TimeTools.truncateToSeconds(inDate);
	}
	
	//------------ bill date -----------------------
	public static LocalDateTime getLastBillDate() {
		return lastBillDate;
	}

	public static void setLastBillDate(LocalDateTime billDate) {
		lastBillDate = TimeTools.truncateToSeconds(billDate);
	}
	
	//------------  patient vaccine-----------------------
	public static LocalDateTime getLastPatientVaccineDate() {
		return lastPatientVaccineDate;
	}

	public static void setLastPatineVaccineDate(LocalDateTime vaccineDate) {
		lastPatientVaccineDate = TimeTools.truncateToSeconds(vaccineDate);
	}

}
