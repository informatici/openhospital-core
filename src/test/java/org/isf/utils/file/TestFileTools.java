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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

public class TestFileTools {

	@Ignore
	@Test
	public void testGetTimestampFromName() throws Exception {
		List<Date> dates = FileTools.getTimestampFromName("09-03-2020");
		assertThat(dates)
				.containsExactly(
						new Date("Mon Mar 09 00:00:00 EDT 2020"),
						new Date("Mon Mar 09 00:00:00 EDT 2020")
				);
		dates = FileTools.getTimestampFromName("01-04-2020 1238");
		assertThat(dates)
				.containsExactly(
						new Date("Wed Apr 01 00:00:00 EDT 2020"),
						new Date("Wed Apr 01 00:00:00 EDT 2020")
				);
		dates = FileTools.getTimestampFromName("02-05-2020_1122");
		assertThat(dates)
				.containsExactly(
						new Date("Sat May 02 00:00:00 EDT 2020"),
						new Date("Sat May 02 00:00:00 EDT 2020")
				);
		dates = FileTools.getTimestampFromName("03-06-2020");
		assertThat(dates)
				.containsExactly(
						new Date("Wed Jun 03 00:00:00 EDT 2020"),
						new Date("Wed Jun 03 00:00:00 EDT 2020")
				);
		dates = FileTools.getTimestampFromName("04/03/2020");
		assertThat(dates)
				.containsExactly(
						new Date("Wed Mar 04 00:00:00 EST 2020"),
						new Date("Wed Mar 04 00:00:00 EST 2020")
				);
		dates = FileTools.getTimestampFromName("05/05/20");
		assertThat(dates)
				.containsExactly(new Date("Tue May 05 00:00:00 EDT 2020"));
		dates = FileTools.getTimestampFromName("2021-12-22 1100");
		assertThat(dates)
				.containsExactly(
						new Date("Wed Dec 22 00:00:00 EST 2021"),
						new Date("Wed Dec 21 00:00:00 EST 2022"),
						new Date("Wed Dec 22 11:00:00 EST 2021"),
						new Date("Wed Dec 22 11:00:00 EST 2021")
				);
		dates = FileTools.getTimestampFromName("21-11-21_0922");
		assertThat(dates)
				.containsExactly(
						new Date("Sun Nov 21 00:00:00 EST 2021"),
						new Date("Sun Nov 21 09:22:00 EST 2021")
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
