/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.utils.excel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TestExcelExporter {

	private Timestamp timeStamp = new Timestamp(new GregorianCalendar(2024, 1, 1).getTimeInMillis());
	private BigDecimal bigDecimal = new BigDecimal(98);
	private Date date = new Date(1234567l);

	String[] columns = new String[] { "Id", "Name", "Hourly Rate", "Part Time", "TimeStamp", "BigDecimal", "Long", "Date" };

	Object[][] tableData = new Object[][] {
					{ 1, "John", 40.0, false, timeStamp, bigDecimal, -1l, date },
					{ 2, "Rambo", 70.0, false, timeStamp, bigDecimal, -1l, date },
					{ 3, "Zorro", 60.0, true, timeStamp, bigDecimal, -1l, date},
	};
	JTable table;
	ExcelExporter excelExporter;

	@Mock
	FileOutputStream fileOutputStream;

	@TempDir
	File tempDir;

	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		table = new JTable(tableData, columns);
		excelExporter = new ExcelExporter();
	}

	@AfterEach
	void closeService() throws Exception {
		closeable.close();
	}

	@Test
	void testExportTableToCSV() throws Exception {
		File outputFile = new File(tempDir, "exportTableToCSV");
		excelExporter.exportTableToCSV(table, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportTableToExcel() throws Exception {
		File outputFile = new File(tempDir, "exportTableToExcel");
		excelExporter.exportTableToExcel(table, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportTableToExcelOLD() throws Exception {
		File outputFile = new File(tempDir, "exportTableToExcelOLD");
		excelExporter.exportTableToExcelOLD(table, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportDataToExcel() throws Exception {
		File outputFile = new File(tempDir, "exportDataToExcel");
		excelExporter.exportDataToExcel(new MyCollection(), outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportDataToExcelOLD() throws Exception {
		File outputFile = new File(tempDir, "exportDataToExcelOLD");
		excelExporter.exportDataToExcelOLD(new MyCollection(), outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportResultSetToExcel() throws Exception {
		ResultSet mockResultSet = MockResultSet.create(
						new String[] { "name", "age", "weight", "empty", "timestamp", "long" }, //columns
						new Object[][] { // data
										{ "Alice", 20, 100.0, null, timeStamp, 99l },
										{ "Bob", 35, 175.0, null, timeStamp, 99l },
										{ "Charles", 50, 150.0, null, timeStamp, 99l },
										{ "Frank", 44, 212.0, null, timeStamp, 99l },
										{ "Jane", 22, 112.5, null, timeStamp, 99l }
						});
		File outputFile = new File(tempDir, "exportResultSetToExcel");
		excelExporter.exportResultsetToExcel(mockResultSet, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportResultSetToExcelOLD() throws Exception {
		ResultSet mockResultSet = MockResultSet.create(
						new String[] { "name", "age", "weight", "empty" }, //columns
						new Object[][] { // data
										{ "Alice", 20, 100.0, null },
										{ "Bob", 35, 175.0, null },
										{ "Charles", 50, 150.0, null },
										{ "Frank", 44, 212.0, null },
										{ "Jane", 22, 112.5, null }
						});
		File outputFile = new File(tempDir, "exportResultSetToExcelOLD");
		excelExporter.exportResultsetToExcelOLD(mockResultSet, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportResultSetToCSV() throws Exception {
		ResultSet mockResultSet = MockResultSet.create(
						new String[] { "name", "age", "weight", "empty", "timestamp" }, //columns
						new Object[][] { // data
										{ "Alice", 20, 100.0, null, timeStamp },
										{ "Bob", 35, 175.0, null, timeStamp },
										{ "Charles", 50, 150.0, null, timeStamp },
										{ "Frank", 44, 212.0, null, timeStamp },
										{ "Jane", 22, 112.5, null, timeStamp }
						});
		File outputFile = new File(tempDir, "exportResultSetToCSV");
		excelExporter.exportResultsetToCSV(mockResultSet, outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportDataToCSV() throws Exception {
		File outputFile = new File(tempDir, "exportDataToCSV");
		excelExporter.exportDataToCSV(new MyCollection(), outputFile);
		assertThat(Files.exists(outputFile.toPath())).isTrue();
	}

	@Test
	void testExportTableToCSVIsRfc4180() throws Exception {
		String[] trickyColumns = new String[] { "Code", "Note", "Qty" };
		Object[][] trickyData = new Object[][] {
						{ "A;1", "plain", 12345 },
						{ "quote\"here", "line\nbreak", 67890 },
		};
		JTable trickyTable = new JTable(trickyData, trickyColumns);
		File outputFile = new File(tempDir, "exportTableToCSV.csv");
		excelExporter.exportTableToCSV(trickyTable, outputFile);

		byte[] bytes = Files.readAllBytes(outputFile.toPath());
		// UTF-8 BOM
		assertThat(bytes[0]).isEqualTo((byte) 0xEF);
		assertThat(bytes[1]).isEqualTo((byte) 0xBB);
		assertThat(bytes[2]).isEqualTo((byte) 0xBF);

		String content = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");
		String[] lines = content.split("\r\n", -1);
		// header has no trailing separator
		assertThat(lines[0]).isEqualTo("Code;Note;Qty");
		// a field containing the separator is wrapped in double quotes; integral ids keep no grouping separator
		assertThat(lines[1]).isEqualTo("\"A;1\";plain;12345");
		// an embedded double quote is doubled and an embedded newline forces quoting
		assertThat(content).contains("\"quote\"\"here\"");
		assertThat(content).contains("\"line\nbreak\"");
		assertThat(content).contains(";67890");
	}

	@Test
	void testExportDataToCSVHasNoTrailingSeparator() throws Exception {
		File outputFile = new File(tempDir, "exportDataToCSV.csv");
		excelExporter.exportDataToCSV(new MyCollection(), outputFile);

		byte[] bytes = Files.readAllBytes(outputFile.toPath());
		assertThat(bytes[0]).isEqualTo((byte) 0xEF);
		String content = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");
		for (String line : content.split("\r\n", -1)) {
			assertThat(line).doesNotEndWith(";");
		}
	}

	@Test
	void testExportTableDispatchesToCSVByExtension() throws Exception {
		File outputFile = new File(tempDir, "dispatch.csv");
		excelExporter.exportTable(table, outputFile);
		// the CSV path writes a UTF-8 BOM, the Excel paths do not
		byte[] bytes = Files.readAllBytes(outputFile.toPath());
		assertThat(bytes[0]).isEqualTo((byte) 0xEF);
	}

	class MyCollection implements Collection {

		int rows = 2;
		int currentRow = 0;
		Map<Object, Object> data = Map.of(
						"one", "b",
						"two", "c",
						"three", 0d,
						"four", timeStamp,
						"five", 99l,
						"six", bigDecimal,
						"seven", date
		);

		@Override
		public int size() {
			return 0;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public boolean contains(Object o) {
			return false;
		}
		@Override
		public Iterator iterator() {
			return new Iterator() {

				@Override
				public boolean hasNext() {
					currentRow++;
					if (currentRow > rows) {
						return false;
					}
					return true;
				}
				@Override
				public Object next() {
					return data;
				}
			};
		}
		@Override
		public Object[] toArray() {
			return new Object[0];
		}
		@Override
		public boolean add(Object o) {
			return false;
		}
		@Override
		public boolean remove(Object o) {
			return false;
		}
		@Override
		public boolean addAll(Collection c) {
			return false;
		}
		@Override
		public void clear() {

		}
		@Override
		public boolean retainAll(Collection c) {
			return false;
		}
		@Override
		public boolean removeAll(Collection c) {
			return false;
		}
		@Override
		public boolean containsAll(Collection c) {
			return false;
		}
		@Override
		public Object[] toArray(Object[] a) {
			return new Object[0];
		}
	}

}
