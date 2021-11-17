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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TableMap extends AbstractTableModel
           implements TableModelListener {

	private static final long serialVersionUID = 1L;
	protected TableModel model;

  public TableModel getModel() {
    return model;
  }

  public void setModel(TableModel model) {
    this.model = model;
    model.addTableModelListener(this);
  }

  // By default, implement TableModel by forwarding all messages
  // to the model.

  @Override
  public Object getValueAt(int aRow, int aColumn) {
    return model.getValueAt(aRow, aColumn);
  }

  @Override
  public void setValueAt(Object aValue, int aRow, int aColumn) {
    model.setValueAt(aValue, aRow, aColumn);
  }

  @Override
  public int getRowCount() {
    return (model == null) ? 0 : model.getRowCount();
  }

  @Override
  public int getColumnCount() {
    return (model == null) ? 0 : model.getColumnCount();
  }

  @Override
  public String getColumnName(int aColumn) {
    return model.getColumnName(aColumn);
  }

  @Override
  public Class<?> getColumnClass(int aColumn) {
    return model.getColumnClass(aColumn);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
     return model.isCellEditable(row, column);
  }
//
// Implementation of the TableModelListener interface,
//
  // By default forward all events to all the listeners.
  @Override
  public void tableChanged(TableModelEvent e) {
    fireTableChanged(e);
  }
}
