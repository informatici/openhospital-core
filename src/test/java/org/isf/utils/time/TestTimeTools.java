/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class TestTimeTools {

	@Test
	public void testGetDaysBetweenDatesDate() {
		Date dateFrom = new Date(114, 10, 3, 0, 0, 0);
		Date dateTo = new Date(121, 10, 3, 23, 59, 59);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, true)).isEqualTo(2557);
		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo, false)).isEqualTo(2557);

		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo)).isEqualTo(2557);
		assertThat(TimeTools.getDaysBetweenDates(dateFrom, dateTo)).isEqualTo(2557);
	}

	@Test
	public void testGetXBetweenDatesGregorian() {
		GregorianCalendar dateFrom = new GregorianCalendar(2014, 10, 3, 0, 0, 0);
		GregorianCalendar dateTo = new GregorianCalendar(2021, 10, 3, 23, 59, 59);

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
		Date day1 = new Date(114, 10, 3, 0, 0, 0);
		Date day2 = new Date(114, 10, 3, 10, 10, 0);
		assertThat(TimeTools.isSameDay(day1, day2)).isTrue();

		Date day3 = new Date(121, 10, 3, 23, 59, 59);
		assertThat(TimeTools.isSameDay(day1, day3)).isFalse();
	}

	@Test
	public void testIsSameDayGregorian() {
		GregorianCalendar day1 = new GregorianCalendar(2014, 10, 3, 0, 0, 0);
		GregorianCalendar day2 = new GregorianCalendar(2014, 10, 3, 10, 10, 0);
		assertThat(TimeTools.isSameDay(day1, day2)).isTrue();

		GregorianCalendar day3 = new GregorianCalendar(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.isSameDay(day1, day3)).isFalse();
	}

	@Test
	public void testFormatDateTimeGregorian() {
		GregorianCalendar dateTime = new GregorianCalendar(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTime(dateTime, null)).isEqualTo("2021-11-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeDate() {
		Date dateTime = new Date(121, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTime(dateTime, null)).isEqualTo("2021-11-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeReportGregorian() {
		GregorianCalendar dateTime = new GregorianCalendar(2021, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTimeReport(dateTime)).isEqualTo("2021-11-03 23:59:59");
	}

	@Test
	public void testFormatDateTimeReportDate() {
		Date dateTime = new Date(121, 10, 3, 23, 59, 59);
		assertThat(TimeTools.formatDateTimeReport(dateTime)).isEqualTo("2021-11-03 23:59:59");
	}

	@Test
	public void testFormatAge() {
		assertThat(TimeTools.getFormattedAge(null)).isEmpty();
		Date dateTime = new Date(121, 10, 3, 23, 59, 59);
		// If message bundles were accessbile the age would look something like the pattern below
		//assertThat(TimeTools.getFormattedAge(dateTime)).isEqualTo("{0}y {1}m {2}d");
		assertThat(TimeTools.getFormattedAge(dateTime)).isEqualTo("angal.agepattern.txt");
	}

	@Test
	public void testParseDate() throws Exception {
		assertThat(TimeTools.parseDate("2021-11-03 23:59:59", "yyyy-MM-dd HH:mm:ss", false))
				.isEqualTo(new GregorianCalendar(2021, 10, 3, 23, 59, 59));
		assertThat(TimeTools.parseDate("2021-11-03 23:59:59", "yyyy-MM-dd HH:mm:ss", true))
				.isEqualTo(new GregorianCalendar(2021, 10, 3));
	}
}
