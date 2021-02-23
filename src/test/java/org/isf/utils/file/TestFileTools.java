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
package org.isf.utils.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class TestFileTools {

	@Test
	public void testGetTimestampFromName() throws Exception {

		// Variations of "2021-12-22"
		List<Date> dates = FileTools.getTimestampFromName("2021-12-22");
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2021, 11, 22, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text2021-12-22_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);
		dates = FileTools.getTimestampFromName("2021-12-22_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		// Variations of "2021-12-22 1101"
		dates = FileTools.getTimestampFromName("2021-12-22 1101");
		Calendar calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2021, 11, 22, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		Calendar calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2021, 11, 22, 11, 1, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22 1101");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22 1101_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("2021-12-22 1101_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "2021-12-22 110135"
		dates = FileTools.getTimestampFromName("2021-12-22 110135");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2021, 11, 22, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2021, 11, 22, 11, 1, 35);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22 110135");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text2021-12-22 110135_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("2021-12-22 110135_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "2021-12-22_1101"
		dates = FileTools.getTimestampFromName("2021-12-22_1101");
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2021, 11, 22, 11, 1, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2021, 11, 22, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22_1101");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22_1101_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("2021-12-22_1101_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "2021-12-22_110142"
		dates = FileTools.getTimestampFromName("2021-12-22_110142");
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2021, 11, 22, 11, 1, 42);
		calendarTime.set(Calendar.MILLISECOND, 0);
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2021, 11, 22, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22_110142");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_2021-12-22_110142_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("2021-12-22_110142_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09-03-2020"
		dates = FileTools.getTimestampFromName("09-03-2020");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 9, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-2020");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text09-03-2020_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-2020_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		// Variations of "09-03-2020 1212"
		dates = FileTools.getTimestampFromName("09-03-2020 1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-2020 1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-2020 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-2020 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09-03-2020_1212"
		dates = FileTools.getTimestampFromName("09-03-2020_1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-2020_1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-2020_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-2020_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09/03/2020"
		dates = FileTools.getTimestampFromName("09/03/2020");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 9, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/2020");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text09/03/2020_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("09/03/2020_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		// Variations of "09/03/2020 1212"
		dates = FileTools.getTimestampFromName("09/03/2020 1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/2020 1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text09/03/2020 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09/03/2020 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09/03/2020_1212"
		dates = FileTools.getTimestampFromName("09/03/2020_1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/2020_1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/2020_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09/03/2020_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09-03-20"
		dates = FileTools.getTimestampFromName("09-03-20");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 9, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		// TODO: this date string does not match
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-20");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-20_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-20_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		// Variations of "09-03-20 1212"
		dates = FileTools.getTimestampFromName("09-03-20 1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-20 1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text09-03-20 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-20 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09-03-20_1212"
		dates = FileTools.getTimestampFromName("09-03-20_1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-20_1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09-03-20_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-20_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09/03/20"
		dates = FileTools.getTimestampFromName("09/03/20");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 9, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/20");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/20_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("09/03/20_text.txt");
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		// Variations of "09/03/20 1212"
		dates = FileTools.getTimestampFromName("09/03/20 1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/20 1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/20 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09-03-2020 1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		// Variations of "09/03/20_1212"
		dates = FileTools.getTimestampFromName("09/03/20_1122");
		calendarNoTime = Calendar.getInstance(TimeZone.getDefault());
		calendarNoTime.set(2020, 2, 9, 0, 0, 0);
		calendarNoTime.set(Calendar.MILLISECOND, 0);
		calendarTime = Calendar.getInstance(TimeZone.getDefault());
		calendarTime.set(2020, 2, 9, 11, 22, 0);
		calendarTime.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text09/03/20_1122");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("some-Text_09/03/20_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);

		dates = FileTools.getTimestampFromName("09/03/20_1122_text.txt");
		assertThat(dates)
				.containsExactly(
						calendarNoTime.getTime(),
						calendarTime.getTime()
				);
	}

	@Test
	public void testHumanReadableByteCountParse() throws Exception {
		assertThat(FileTools.humanReadableByteCountParse("1024B")).isEqualTo(1024L);
		assertThat(FileTools.humanReadableByteCountParse("4M")).isEqualTo(4194304L);
		assertThat(FileTools.humanReadableByteCountParse("16M")).isEqualTo(16777216L);
		assertThat(FileTools.humanReadableByteCountParse("256M")).isEqualTo(268435456L);
		assertThat(FileTools.humanReadableByteCountParse("1G")).isEqualTo(1073741824L);
	}

	@Test
	public void testHumanReadableByteCount() throws Exception {
		assertThat(FileTools.humanReadableByteCount(1024L, Locale.US)).isEqualTo("1.0 B");
		assertThat(FileTools.humanReadableByteCount(4194304L, Locale.US)).isEqualTo("4.0 M");
		assertThat(FileTools.humanReadableByteCount(16777216L, Locale.US)).isEqualTo("16.0 M");
		assertThat(FileTools.humanReadableByteCount(268435456L, Locale.US)).isEqualTo("256.0 M");
		assertThat(FileTools.humanReadableByteCount(1073741824L, Locale.US)).isEqualTo("1.0 G");
	}
}
