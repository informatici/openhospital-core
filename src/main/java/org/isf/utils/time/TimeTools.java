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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.isf.generaldata.MessageBundle;
import org.isf.utils.db.DbQueryLogger;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some useful functions for time calculations.
 *
 * @author Mwithi
 */
public class TimeTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(TimeTools.class);

	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	private TimeTools() {
	}

	/**
	 * Returns <code>true</code> if the DATE part is the same (no matter the time)
	 *
	 * @param aDate
	 * @param today
	 * @return
	 */
	public static boolean isSameDay(LocalDate aDate, LocalDate today) {
		return today.equals(aDate);
	}

	public static boolean isSameDay(LocalDateTime aDate, LocalDateTime today) {
		LocalDate d1 = aDate.toLocalDate();
		LocalDate d2 = today.toLocalDate();
		return isSameDay(d1, d2);
	}

	/**
	 * Returns the difference in days between two dates
	 *
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getDaysBetweenDates(LocalDateTime from, LocalDateTime to, boolean ignoreTime) {
		if (ignoreTime) {
			from = from.withHour(0).withMinute(0).withSecond(0);
			to = to.withHour(0).withMinute(0).withSecond(0);
		}
		return (int) ChronoUnit.DAYS.between(from, to);
	}

	/**
	 * Returns the difference in days between two dates
	 *
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getDaysBetweenDates(LocalDate from, LocalDate to, boolean ignoreTime) {
		// LocalDate does not have time notion
		// TODO remove or change signature of this legacy function
		return (int) ChronoUnit.DAYS.between(from, to);
	}

	/**
	 * Returns the difference in weeks between two dates
	 *
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getWeeksBetweenDates(LocalDateTime from, LocalDateTime to, boolean ignoreTime) {
		if (ignoreTime) {
			from = from.withHour(0).withMinute(0).withSecond(0);
			to = to.withHour(0).withMinute(0).withSecond(0);
		}
		return (int) ChronoUnit.WEEKS.between(from, to);
	}

	/**
	 * Returns the difference in months between two dates
	 *
	 * @param from
	 * @param to
	 * @param ignoreTime - if <code>True</code> only dates will be compared
	 * @return the number of days, negative if from is after to
	 */
	public static int getMonthsBetweenDates(LocalDateTime from, LocalDateTime to, boolean ignoreTime) {
		if (ignoreTime) {
			from = from.withHour(0).withMinute(0).withSecond(0);
			to = to.withHour(0).withMinute(0).withSecond(0);
		}
		return (int) ChronoUnit.MONTHS.between(from, to);
	}

	/**
	 * Return the age in the format {years}y {months}m {days}d or with other locale pattern
	 *
	 * @param birthDate - the birthdate
	 * @return string with the formatted age
	 * @author Mwithi
	 */
	public static String getFormattedAge(LocalDate birthDate) {
		String pattern = MessageBundle.getMessage("angal.agepattern.txt");
		String age = "";
		if (birthDate != null) {
			LocalDate now = LocalDate.now();
			Period period = Period.between(birthDate, now);
			age = MessageFormat.format(pattern, period.getYears(), period.getMonths(), period.getDays());
		}
		return age;
	}

	/**
	 * Return a string representation of the dateTime with the given pattern
	 *
	 * @param dateTime - a LocalDateTime object
	 * @param pattern - the pattern. If <code>null</code> "yyyy-MM-dd HH:mm:ss" will be used
	 * @return the String representation of the LocalDateTime
	 */
	public static String formatDateTime(LocalDateTime dateTime, String pattern) {
		if (pattern == null) {
			pattern = YYYY_MM_DD_HH_MM_SS;
		}
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return dateTime.format(format);
	}

	/**
	 * Return a string representation of the dateTime in the form "yyyy-MM-dd HH:mm:ss"
	 *
	 * @param time - a LocalDateTime object
	 * @return the String representation of the LocalDateTime
	 */
	public static String formatDateTimeReport(LocalDateTime time) {
		return formatDateTime(time, null);
	}

	/**
	 * Return a string representation of the dateTime in the form "yyyy-MM-dd HH:mm:ss"
	 *
	 * @param date - a Date object
	 * @return the String represetation of the Date
	 */
	public static String formatDateTimeReport(LocalDate date) {
		LocalDateTime time = date.atStartOfDay();
		return formatDateTime(time, null);
	}

	/**
	 * Return the first instance of the current date
	 *
	 * @return
	 */
	public static LocalDateTime getDateToday0() {
		return LocalDateTime.now().with(LocalTime.MIN);
	}

	/**
	 * Return the last instance of the current date
	 *
	 * @return
	 */
	public static LocalDateTime getDateToday24() {
		return LocalDateTime.now().with(LocalTime.MAX);
	}

	/**
	 * Return a {@link LocalDateTime} representation of the string using the given pattern
	 *
	 * @param string - a String object to be passed
	 * @param pattern - the pattern. If <code>null</code> "yyyy-MM-dd HH:mm:ss" will be used
	 * @param noTime - if <code>True</code> the time will be 00:00:00, actual time otherwise.
	 * @return the String representation of the LocalDateTime
	 */
	public static LocalDateTime parseDate(String string, String pattern, boolean noTime) {
		if (pattern == null) {
			pattern = YYYY_MM_DD_HH_MM_SS;
		}
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime dateTime;
		if (noTime) {
			/**
			 * regarding to https://stackoverflow.com/questions/27454025/unable-to-obtain-localdatetime-from-temporalaccessor-when-parsing-localdatetime
			 * Java does not accept a bare Date value as DateTime
			 */
			dateTime = LocalDate.parse(string, format).atStartOfDay();
		} else {
			dateTime = LocalDateTime.parse(string, format);
		}
		return dateTime;
	}

	public static LocalDateTime getBeginningOfDay(LocalDateTime date) {
		return date.with(LocalTime.MIN);
	}

	public static LocalDateTime getBeginningOfNextDay(LocalDateTime date) {
		return date.plusDays(1).with(LocalTime.MIN);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the difference in days between two dates
	 *
	 * @param from
	 * @param to
	 * @return the number of days
	 */
	public static int getDaysBetweenDates(LocalDateTime from, LocalDateTime to) {
		return (int) ChronoUnit.DAYS.between(from, to);
	}

	/**
	 * Returns the difference in weeks between two dates
	 *
	 * @param from
	 * @param to
	 * @return the number of weeks
	 */
	public static int getWeeksBetweenDates(LocalDateTime from, LocalDateTime to) {
		return (int) ChronoUnit.WEEKS.between(from, to);
	}

	/**
	 * Returns the difference in months between two dates
	 *
	 * @param from
	 * @param to
	 * @return the number of months
	 */
	public static int getMonthsBetweenDates(LocalDateTime from, LocalDateTime to) {
		return (int) ChronoUnit.MONTHS.between(from, to);
	}

	public static LocalDateTime getDate(String strDate, String format) throws ParseException {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			return LocalDateTime.parse(strDate, formatter);
		} catch (DateTimeParseException e) {
			if (!format.equals("dd/MM/yyyy")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				return LocalDateTime.parse(strDate, formatter);
			}
		}
		return null;
	}

	/**
	 * Return the date and time of the server
	 *
	 * @return DateTime
	 * @author hadesthanos
	 */
	public static LocalDateTime getServerDateTime() {
		String query = " SELECT NOW( ) as time ";
		DbQueryLogger dbQuery = new DbQueryLogger();
		try {
			ResultSet resultSet = dbQuery.getData(query, true);
			while (resultSet.next()) {
				String date = resultSet.getString("time");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				return LocalDateTime.parse(date, formatter);
			}
		} catch (SQLException | OHException | DateTimeParseException exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return null;
	}

	/**
	 * Convert LocalDateTime -> String using format "dd/MM/yy"
	 *
	 * @param time - a Calendar datetime
	 * @return a String representing the Calendar in the format "dd/MM/yy"
	 * @deprecated use formatDateTime(LocalDateTime dateTime, String pattern) instead
	 */
	@Deprecated
	public static String getConvertedString(LocalDateTime time) {
		if (time == null) {
			return MessageBundle.getMessage("angal.malnutrition.nodate.msg");
		}
		LocalDate date = time.toLocalDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
		return date.format(formatter);
	}

	/**
	 * Convert String -> Date using pattern "ddMMyy" and the server time
	 * ( SELECT NOW() as time )
	 *
	 * @param string - a date in the form ddMMyy
	 * @return a Calendar datetime
	 * @throws ParseException
	 * @deprecated use getDate(String strDate, String format) instead
	 */
	@Deprecated
	public static LocalDateTime convertToDate(String string) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
		LocalDateTime date = LocalDateTime.parse(string, formatter);
		LocalDateTime serverDateTime = TimeTools.getServerDateTime();
		return date.with(serverDateTime.toLocalTime());
	}
}
