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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.utils.time;

import java.time.LocalDateTime;

/**
 * -------------------------------------------------------------------
 * Static class RememberDates: useful class in order to remember the last date inserted when
 * are performed recursive inserting of past datas. The aim of the class is to avoid the user
 * to manually select the date in each new window
 * -----------------------------------------
 * modification history
 * =====================
 * 08/11/06 - ross - creazione
 * 09/11/06 - ross - modificata per fornire, la prima volta, la data di sistema (metodi get gregorian)
 * 11/08/10 - claudia - inserita la voce per PATIENTVACCINE
 * 11/12/14 - mwithi - completely changed the behaviour: no more {Date} type, only {GregorianCalendar}
 * 					   and only date kept, time is up to date.
 * -------------------------------------------------------------------
 */
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
