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
package org.isf.utils.table;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableSorter extends TableMap {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TableSorter.class);

	int[] indexes;
	Vector<Integer> sortingColumns = new Vector<>();
	boolean ascending = true;
	int compares;

	public TableSorter() {
		indexes = new int[0]; // for consistency
	}

	public TableSorter(TableModel model) {
		setModel(model);
	}

	@Override
	public void setModel(TableModel model) {
		super.setModel(model);
		reallocateIndexes();
	}

	public int compareRowsByColumn(int row1, int row2, int column) {
		TableModel data = model;

		// Check for nulls.

		Object o1 = data.getValueAt(row1, column);
		Object o2 = data.getValueAt(row2, column);

		// If both values are null, return 0.
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) { // Define null less than everything.
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		/*
		 * We copy all returned values from the getValue call in case an
		 * optimized model is reusing one object to return many values. The
		 * Number subclasses in the JDK are immutable and so will not be used in
		 * this way but other subclasses of Number might want to do this to save
		 * space and avoid unnecessary heap allocation.
		 */

		if ((o1 instanceof Integer) && (o2 instanceof Integer)) {
			Number n1 = (Number) data.getValueAt(row1, column);
			double d1 = n1.doubleValue();
			Number n2 = (Number) data.getValueAt(row2, column);
			double d2 = n2.doubleValue();

			if (d1 < d2) {
				return -1;
			} else if (d1 > d2) {
				return 1;
			} else {
				return 0;
			}
		} else if ((o1 instanceof String) && (o2 instanceof String)) {

			String str1 = data.getValueAt(row1, column).toString();
			String str2 = data.getValueAt(row2, column).toString();

			try {
				DateFormat myDateFormat = new SimpleDateFormat("dd/MM/yy");
				Date d1 = myDateFormat.parse(str1);
				Date d2 = myDateFormat.parse(str2);
				long n1 = d1.getTime();
				long n2 = d2.getTime();

				if (n1 < n2) {
					return -1;
				} else if (n1 > n2) {
					return 1;
				} else {
					return 0;
				}
			} catch (NumberFormatException | ParseException e3) {
				LOGGER.info("Compare ({}) with ({})", str1, str2);
				return str1.compareTo(str2);
			}
		} else {
			Object v1 = data.getValueAt(row1, column);
			String s1 = v1.toString();
			Object v2 = data.getValueAt(row2, column);
			String s2 = v2.toString();
			int result = s1.compareTo(s2);

			if (result < 0) {
				return -1;
			} else if (result > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public int compare(int row1, int row2) {
		compares++;
		for (int level = 0; level < sortingColumns.size(); level++) {
			Integer column = sortingColumns.elementAt(level);
			int result = compareRowsByColumn(row1, row2, column);
			if (result != 0) {
				return ascending ? result : -result;
			}
		}
		return 0;
	}

	public void reallocateIndexes() {
		int rowCount = model.getRowCount();

		// Set up a new array of indexes with the right number of elements
		// for the new data model.
		indexes = new int[rowCount];

		// Initialize with the identity mapping.
		for (int row = 0; row < rowCount; row++) {
			indexes[row] = row;
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		reallocateIndexes();

		super.tableChanged(e);
	}

	public void checkModel() {
		if (indexes.length != model.getRowCount()) {
			LOGGER.error("Sorter not informed of a change in model.");
		}
	}

	public void sort(Object sender) {
		checkModel();

		compares = 0;

		shuttlesort(indexes.clone(), indexes, 0, indexes.length);
	}

	public void n2sort() {
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = i + 1; j < getRowCount(); j++) {
				if (compare(indexes[i], indexes[j]) == -1) {
					swap(i, j);
				}
			}
		}
	}

	// This is a home-grown implementation which we have not had time
	// to research - it may perform poorly in some circumstances. It
	// requires twice the space of an in-place algorithm and makes
	// NlogN assignments shuttling the values between the two
	// arrays. The number of compares appears to vary between N-1 and
	// NlogN depending on the initial order but the main reason for
	// using it here is that, unlike qsort, it is stable.
	// FIXME: sorting {@link Date}s is failing.
	public void shuttlesort(int[] from, int[] to, int low, int high) {
		if (high - low < 2) {
			return;
		}
		int middle = (low + high) / 2;
		shuttlesort(to, from, low, middle);
		shuttlesort(to, from, middle, high);

		int p = low;
		int q = middle;

		/*
		 * This is an optional short-cut; at each recursive call, check to see
		 * if the elements in this subset are already ordered. If so, no further
		 * comparisons are needed; the sub-array can just be copied. The array
		 * must be copied rather than assigned otherwise sister calls in the
		 * recursion might get out of sync. When the number of elements is three
		 * they are partitioned so that the first set, [low, mid), has one
		 * element and and the second, [mid, high), has two. We skip the
		 * optimization when the number of elements is three or less as the
		 * first compare in the normal merge will produce the same sequence of
		 * steps. This optimization seems to be worthwhile for partially ordered
		 * lists but some analysis is needed to find out how the performance
		 * drops to Nlog(N) as the initial order diminishes - it may drop very
		 * quickly.
		 */

		if (high - low >= 4 && compare(from[middle - 1], from[middle]) <= 0) {
			if (high - low >= 0) {
				System.arraycopy(from, low, to, low, high - low);
			}
			return;
		}

		// A normal merge.

		for (int i = low; i < high; i++) {
			if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
				to[i] = from[p++];
			} else {
				to[i] = from[q++];
			}
		}
	}

	public void swap(int i, int j) {
		int tmp = indexes[i];
		indexes[i] = indexes[j];
		indexes[j] = tmp;
	}

	// The mapping only affects the contents of the data rows.
	// Pass all requests to these rows through the mapping array: "indexes".

	@Override
	public Object getValueAt(int aRow, int aColumn) {
		checkModel();
		return model.getValueAt(indexes[aRow], aColumn);
	}

	@Override
	public void setValueAt(Object aValue, int aRow, int aColumn) {
		checkModel();
		model.setValueAt(aValue, indexes[aRow], aColumn);
	}

	public void sortByColumn(int column) {
		sortByColumn(column, true);
	}

	public void sortByColumn(int column, boolean ascending) {
		this.ascending = ascending;
		sortingColumns.removeAllElements();
		sortingColumns.addElement(column);
		sort(this);
		super.tableChanged(new TableModelEvent(this));
	}

	// There is no-where else to put this.
	// Add a mouse listener to the Table to trigger a table sort
	// when a column heading is clicked in the JTable.
	public void addMouseListenerToHeaderInTable(JTable table) {
		final TableSorter sorter = this;
		final JTable tableView = table;
		tableView.setColumnSelectionAllowed(false);
		MouseAdapter listMouseListener = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				TableColumnModel columnModel = tableView.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				int column = tableView.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column != -1) {
					int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
					boolean ascending = (shiftPressed == 0);
					sorter.sortByColumn(column, ascending);
				}
			}
		};
		JTableHeader th = tableView.getTableHeader();
		th.addMouseListener(listMouseListener);
	}

	public void updateRowHeights(JTable table) {
		for (int row = 0; row < table.getRowCount(); row++) {
			int rowHeight = table.getRowHeight();

			for (int column = 0; column < table.getColumnCount(); column++) {
				Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
				rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
			}

			table.setRowHeight(row, rowHeight);
		}
	}

}
