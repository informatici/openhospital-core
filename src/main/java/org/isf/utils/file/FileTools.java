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
package org.isf.utils.file;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mwithi
 */
public class FileTools {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileTools.class);

	private static final String[] BINARY_UNITS = { "B", "M", "G" }; //Byte, Megabyte, Gigabyte 

	// TODO: consider more specific versions of \d that do "some" validation
	// DAY:   (0[1-9]|[12][0-9]|3[01])
	// MONTH: (0[1-9]|1[012])
	// YEAR:  (2\d\d\d)

	// HOUR:  (0[0-9]|1[0-9]|2[0-3])
	// MIN:   ([0-5][0-9])

	private static final String[][] dateTimeFormats = {
			{ "yyyy-MM-dd_HHmmss", "(?<![0-9])(\\d{4}-\\d{2}-\\d{2}_\\d{6})(?![0-9])" },
			{ "yyyy-MM-dd HHmmss", "(?<![0-9])(\\d{4}-\\d{2}-\\d{2} \\d{6})(?![0-9])" },
			{ "yyyy-MM-dd_HHmm", "(?<![0-9])(\\d{4}-\\d{2}-\\d{2}_\\d{4})(?![0-9])" },
			{ "yyyy-MM-dd HHmm", "(?<![0-9])(\\d{4}-\\d{2}-\\d{2} \\d{4})(?![0-9])" },
			{ "yyyy-MM-dd", "(?<![0-9])(\\d{4}-\\d{2}-\\d{2})(?![0-9])" },

			{ "yyyyMMdd_HHmmss", "(?<![0-9])(\\d{4}\\d{2}\\d{2}_\\d{6})(?![0-9])" },
			{ "yyyyMMdd HHmmss", "(?<![0-9])(\\d{4}\\d{2}\\d{2} \\d{6})(?![0-9])" },
			{ "yyyyMMdd_HHmm", "(?<![0-9])(\\d{4}\\d{2}\\d{2}_\\d{4})(?![0-9])" },
			{ "yyyyMMdd HHmm", "(?<![0-9])(\\d{4}\\d{2}\\d{2} \\d{4})(?![0-9])" },
			{ "yyyyMMdd", "(?<![0-9])(\\d{4}\\d{2}\\d{2})(?![0-9])" },

			{ "dd-MM-yyyy_HHmm", "(?<![0-9])(\\d{2}-\\d{2}-\\d{4}_\\d{4})(?![0-9])" },
			{ "dd-MM-yyyy HHmm", "(?<![0-9])(\\d{2}-\\d{2}-\\d{4} \\d{4})(?![0-9])" },
			{ "dd-MM-yyyy", "(?<![0-9])(\\d{2}-\\d{2}-\\d{4})(?![0-9])" },

			{ "dd-MM-yy_HHmm", "(?<![0-9])(\\d{2}-\\d{2}-\\d{2}_\\d{4})(?![0-9])" },
			{ "dd-MM-yy HHmm", "(?<![0-9])(\\d{2}-\\d{2}-\\d{2} \\d{4})(?![0-9])" },
			{ "dd-MM-yy", "(?<![0-9])(\\d{2}-\\d{2}-\\d{2})(?![0-9])" },

			{ "dd/MM/yyyy_HHmm", "(?<![0-9])(\\d{2}/\\d{2}/\\d{4}_\\d{4})(?![0-9])" },
			{ "dd/MM/yyyy HHmm", "(?<![0-9])(\\d{2}/\\d{2}/\\d{4} \\d{4})(?![0-9])" },
			{ "dd/MM/yyyy", "(?<![0-9])(\\d{2}/\\d{2}/\\d{4})(?![0-9])" },

			{ "dd/MM/yy_HHmm", "(?<![0-9])(\\d{2}/\\d{2}/\\d{2}_\\d{4})(?![0-9])" },
			{ "dd/MM/yy HHmm", "(?<![0-9])(\\d{2}/\\d{2}/\\d{2} \\d{4})(?![0-9])" },
			{ "dd/MM/yy", "(?<![0-9])(\\d{2}/\\d{2}/\\d{2})(?![0-9])" },
	};

	private FileTools() {
	}

	/**
	 * Retrieves the last modified date
	 *
	 * @param file
	 * @return
	 */
	public static LocalDateTime getTimestamp(File file) {
		if (file == null) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), TimeZone.getDefault().toZoneId());
	}

	/**
	 * Retrieve timestamp from filename
	 *
	 * @param file
	 * @return the list of retrieved date or <code>null</code> if nothing is found
	 */
	public static List<Date> getTimestampFromName(File file) {
		return getTimestampFromName(file.getName());
	}

	/**
	 * Retrieves the timestamp from formattedString
	 * using these date and time formats:<br>
	 * <p>
	 * - yyyy-MM-dd -> "2020-02-01"<br>
	 * - dd-MM-yyyy -> "02-03-2020"<br>
	 * - dd-MM-yy -> "03-04-20"<br>
	 * - dd/MM/yyyy -> "04/05/2020"<br>
	 * - dd/MM/yy -> "05/06/20"<br>
	 * - yyyy-MM-dd HHmm -> "2020-07-06 0123"<br>
	 * - yyyy-MM-dd HHmmss -> "2020-08-07 012345"<br>
	 * - yyyy-MM-dd_HHmmss -> "2020-09-08_012345"<br>
	 * - dd-MM-yy_HHmm -> "20-10-09_0123"<br>
	 * - yyyy-MM-dd HHmm -> "2020-10-09 0123"<br>
	 * - yyyy-MM-dd_HHmm -> "2020-10-09_0123" <br>
	 *
	 * @param formattedString
	 * @return the list of retrieved date (first null)
	 */
	public static List<Date> getTimestampFromName(String formattedString) {
		List<Date> datesFound = new ArrayList<>(1);
		return getTimestampFromName(formattedString, datesFound);
	}

	private static List<Date> getTimestampFromName(String formattedString, List<Date> datesFound) {
		if (null == formattedString || StringUtils.isEmpty(formattedString)) {
			return datesFound;
		}
		for (String[] dateTimeFormat : dateTimeFormats) {
			String format = dateTimeFormat[0];
			String regex = dateTimeFormat[1];

			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(formattedString);
				if (matcher.find()) {
					// only return the longest match
					datesFound.add(sdf.parse(matcher.group(1)));
					return datesFound;
				}
			} catch (ParseException e) {
				LOGGER.error("ParseException (no date patterns found) in '{}'", formattedString);
			}
		}
		return datesFound;
	}

	public static String humanReadableByteCount(final long bytes, final Locale locale) {
		final int base = 1024;

		// When using the smallest unit no decimal point is needed, because it's the exact number.
		if (bytes < base) {
			return bytes + " " + BINARY_UNITS[0];
		}

		final int exponent = (int) (Math.log(bytes) / Math.log(base));
		final String unit = BINARY_UNITS[exponent - 1];
		return String.format(locale, "%.1f %s", bytes / Math.pow(base, exponent), unit);
	}

	public static Long humanReadableByteCountParse(String string) throws OHException {
		final int base = 1024;
		long size;

		try {
			if (string.contains(BINARY_UNITS[0])) { // Byte
				size = (long) (Long.parseLong(string.split(BINARY_UNITS[0])[0]) * Math.pow(base, 0));

			} else if (string.contains(BINARY_UNITS[1])) { // Megabyte
				size = (long) (Long.parseLong(string.split(BINARY_UNITS[1])[0]) * Math.pow(base, 2));

			} else if (string.contains(BINARY_UNITS[2])) { // Gigabyte
				size = (long) (Long.parseLong(string.split(BINARY_UNITS[2])[0]) * Math.pow(base, 3));

			} else {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new OHException(MessageBundle.formatMessage("angal.dicom.unknownsizeformatpleasesetdicommaxsizeproperty.fmt.msg", string));
		}
		return size;
	}

}
