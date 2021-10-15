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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.exolab.castor.types.DateTime;

/**
 * Created by nicosalvato on 2016-08-25.
 * Contact: nicosalvato@gmail.com
 */
public class DateConverters {
    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     * @param datetime {@link GregorianCalendar} object.
     * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     */
    public static String convertToSQLDate(GregorianCalendar datetime) {
        if (datetime == null) {
	        return null;
        }
        return convertToSQLDate(datetime.getTime());
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     * @param datetime {@link Date} input.
     * @return the date in format <code>yyyy-MM-dd HH:mm:ss</code>.
     */
    public static String convertToSQLDate(Date datetime) {
        if (datetime == null) {
	        return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(datetime);
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd</code>.
     * @param date {@link Date} object.
     * @return the date in format <code>yyyy-MM-dd</code>.
     */
    public static String convertToSQLDateLimited(GregorianCalendar date) {
        if (date == null) {
	        return null;
        }
        return convertToSQLDateLimited(date.getTime());
    }

    /**
     * Returns a {@link String} representing the date in format <code>yyyy-MM-dd</code>.
     * @param date {@link Date} object.
     * @return the date in format <code>yyyy-MM-dd</code>.
     */
    public static String convertToSQLDateLimited(Date date) {
        if (date == null) {
	        return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * Converts a {@link LocalDateTime} to a {@link Date}.
     * @param calendar the calendar to convert.
     * @return the converted value or <code>null</code> if the passed value is <code>null</code>.
     */
    public static Date toDate(LocalDateTime localDateTime) {
	    return Optional.ofNullable(localDateTime)
			    .map(ldt -> Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()))
			    .orElse(null);
    }
    
    public static DateTime toDateTime(GregorianCalendar calendar) {
        if (calendar == null) {
	        return null;
        }
        return new DateTime(calendar.getTimeInMillis());
    }

    /**
     * Converts the specified {@link java.sql.Date} to a {@link GregorianCalendar}.
     * @param date the date to convert.
     * @return the converted date.
     */
    public static GregorianCalendar toCalendar(Date date) {
	    if (date == null) {
		    return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    return calendar;
    }

}
