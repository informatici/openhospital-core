/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2026 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class RememberDatesTest {

	@Test
	void setLastOpdVisitDate_shouldSetLastOpdVisitDate() {
		LocalDateTime date = LocalDateTime.of(2014, 10, 3, 0, 0, 0);
		RememberDates.setLastOpdVisitDate(date);
		assertThat(RememberDates.getLastOpdVisitDate()).isEqualTo(date);
	}

	@Test
	void setLastLabExamDate_shouldSetLastLabExamDate() {
		LocalDateTime date = LocalDateTime.of(2014, 11, 3, 0, 0, 0);
		RememberDates.setLastLabExamDate(date);
		assertThat(RememberDates.getLastLabExamDate()).isEqualTo(date);
	}

	@Test
	void setLastAdmInDate_shouldSetLastAdmInDate() {
		LocalDateTime date = LocalDateTime.of(2016, 11, 3, 0, 0, 0);
		RememberDates.setLastAdmInDate(date);
		assertThat(RememberDates.getLastAdmInDate()).isEqualTo(date);
	}

	@Test
	void setLastBillDate_shouldSetLastBillDate() {
		LocalDateTime date = LocalDateTime.of(2016, 1, 3, 0, 0, 0);
		RememberDates.setLastBillDate(date);
		assertThat(RememberDates.getLastBillDate()).isEqualTo(date);
	}

	@Test
	void setLastPatineVaccineDate_shouldSetLastPatientVaccineDate() {
		LocalDateTime date = LocalDateTime.of(2016, 1, 28, 0, 0, 0);
		RememberDates.setLastPatineVaccineDate(date);
		assertThat(RememberDates.getLastPatientVaccineDate()).isEqualTo(date);
	}

}
