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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockResultSet {

	private final Map<String, Integer> columnIndices;
	private final Object[][] data;
	private int rowIndex;
	private String[] columnNames;

	private MockResultSet(String[] columnNames, Object[][] data) {
		// create a map of column name to column index
		this.columnIndices = IntStream.range(0, columnNames.length)
						.boxed()
						.collect(Collectors.toMap(
										k -> columnNames[k],
										Function.identity(),
										(a, b) -> {
											throw new RuntimeException("Duplicate column " + a);
										},
										java.util.LinkedHashMap::new
						));
		this.data = data;
		this.columnNames = columnNames;
		this.rowIndex = -1;
	}

	private ResultSet buildMock() throws SQLException {
		final ResultSet rs = mock(ResultSet.class);

		// mock rs.next()
		doAnswer(invocation -> {
			rowIndex++;
			return rowIndex < data.length;
		}).when(rs).next();

		// mock rs.getString(columnName)
		doAnswer(invocation -> {
			String columnName = invocation.getArgument(0, String.class);
			int columnIndex = columnIndices.get(columnName);
			return (String) data[rowIndex][columnIndex];
		}).when(rs).getString(anyString());

		// mock rs.getObject(columnIndex)
		doAnswer(invocation -> {
			int index = invocation.getArgument(0, Integer.class);
			return data[rowIndex][index - 1];
		}).when(rs).getObject(anyInt());

		ResultSetMetaData rsmd = mock(ResultSetMetaData.class);

		// mock rsmd.getColumnCount()
		doReturn(columnIndices.size()).when(rsmd).getColumnCount();

		// mock rs.getMetaData()
		doReturn(rsmd).when(rs).getMetaData();

		doAnswer(invocation -> {
			int index = invocation.getArgument(0, Integer.class);
			return columnNames[index - 1];
		}).when(rsmd).getColumnName(anyInt());

		return rs;
	}

	/**
	 * Creates the mock ResultSet.
	 * @param columnNames the names of the columns
	 * @param data
	 * @return a mocked ResultSet
	 * @throws SQLException
	 */
	static ResultSet create(
					final String[] columnNames,
					final Object[][] data)
					throws SQLException {
		return new MockResultSet(columnNames, data).buildMock();
	}
}
