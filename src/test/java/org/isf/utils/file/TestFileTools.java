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
		List<Date> dates = FileTools.getTimestampFromName("09-03-2020");
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 9, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("01-04-2020 1238");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 3, 1, 12, 38, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("02-05-20_1122");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 4, 2, 11, 22, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("03-06-2020");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 5, 3, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("04/03/2020");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 2, 4, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("05/05/20");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2020, 4, 5, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("2021-12-22 1100");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2021, 11, 22, 11, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
				);

		dates = FileTools.getTimestampFromName("21-11-21_0922");
		calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(2021, 10, 21, 9, 22, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		assertThat(dates)
				.containsExactly(
						calendar.getTime()
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
