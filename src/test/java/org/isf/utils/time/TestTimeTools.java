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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TestTimeTools {

	@Test
	public void testGetDaysBetweenDatesDate() {
		LocalDate dateFrom = LocalDate.of(114, 10, 3);
		LocalDate dateTo = LocalDate.of(121, 10, 3);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, true)).isEqualTo(2557);
		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, false)).isEqualTo(2557);
	}

	@Test
	public void testGetXBetweenLocaleDateTime() {
		LocalDateTime dateFrom = LocalDateTime.of(2014, 10, 3, 0, 0, 0);
		LocalDateTime dateTo = LocalDateTime.of(2021, 10, 3, 23, 59, 59);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, true)).isEqualTo(2557);
		assertThat(TimeTools.getWeeksBetweenDates(dateFrom, dateTo, true)).isEqualTo(365);
		assertThat(TimeTools.getMonthsBetweenDates(dateFrom, dateTo, true)).isEqualTo(84);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, false)).isEqualTo(2557);
		assertThat(TimeTools.getWeeksBetweenDates(dateFrom, dateTo, false)).isEqualTo(365);
		assertThat(TimeTools.getMonthsBetweenDates(dateFrom, dateTo, false)).isEqualTo(84);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo)).isEqualTo(2557);
		assertThat(TimeTools.getWeeksBetweenDates(dateFrom, dateTo)).isEqualTo(365);
		assertThat(TimeTools.getMonthsBetweenDates(dateFrom, dateTo)).isEqualTo(84);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo)).isEqualTo(2557);
		assertThat(TimeTools.getWeeksBetweenDates(dateFrom, dateTo)).isEqualTo(365);
		assertThat(TimeTools.getMonthsBetweenDates(dateFrom, dateTo)).isEqualTo(84);
	}

	@Test
	public void testIsSameDayDate() {
		LocalDate day1 = LocalDate.of(114, 10, 3);
		LocalDate day2 = LocalDate.of(114, 10, 3);
		assertThat(TimeTools.isSameDay(day1, day2)).isTrue();

		LocalDate day3 = LocalDate.of(121, 10, 4);
		assertThat(TimeTools.isSameDay(day1, day3)).isFalse();
	}

	@Test
	public void testIsSameDayLocalDateTime() {
		LocalDateTime day1 = LocalDateTime.of(2014, 10, 3, 0, 0, 0);
		LocalDateTime day2 = LocalDateTime.of(2014, 10, 3, 10, 10, 0);
		assertThat(TimeTools.isSameDay(day1, day2)).isTrue();

		LocalDateTime day3 = LocalDateTime.of(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.isSameDay(day1, day3)).isFalse();
	}

	@Test
	public void testFormatLocalDateTime() {
		LocalDateTime dateTime = LocalDateTime.of(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTime(dateTime, null)).isEqualTo("2021-10-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeDate() {
		LocalDateTime dateTime = LocalDateTime.of(121, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTime(dateTime, null)).isEqualTo("0121-10-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeReportLocalDateTime() {
		LocalDateTime dateTime = LocalDateTime.of(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTimeReport(dateTime)).isEqualTo("2021-10-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeReportDate() {
		LocalDate date = LocalDate.of(121, 10, 3);
		assertThat(TimeTools.formatDateTimeReport(date)).isEqualTo("0121-10-03 00:00:00");
	}

	@Test
	public void testFormatAge() {
		assertThat(TimeTools.getFormattedAge(null)).isEmpty();
		LocalDate dateTime = LocalDate.of(121, 10, 3);
		// If message bundles were accessbile the age would look something like the pattern below
		//assertThat(TimeTools.getFormattedAge(dateTime)).isEqualTo("{0}y {1}m {2}d");
		assertThat(TimeTools.getFormattedAge(dateTime)).isEqualTo("angal.agepattern.txt");
	}

	@Test
	public void testParseDate() throws Exception {
		assertThat(TimeTools.parseDate("2021-11-03 23:59:59", "yyyy-MM-dd HH:mm:ss", false))
				.isEqualTo(LocalDateTime.of(2021, 11, 3, 23, 59, 59));
		assertThat(TimeTools.parseDate("2021-11-03", "yyyy-MM-dd", true))
				.isEqualTo(LocalDateTime.of(2021, 11, 3, 0, 0, 0));
	}
}
