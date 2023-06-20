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
package org.isf.utils.treetable;

/*
 * Copyright 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultTreeSelectionModel;

/**
 * This example shows how to create a simple JTreeTable component, 
 * by using a JTree as a renderer (and editor) for the cells in a 
 * particular column in the JTable.
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class JTreeTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected TreeTableCellRenderer tree;

	public JTreeTable(TreeTableModel treeTableModel) {
		super();

		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel, this);

		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share their row selection models.
		tree.setSelectionModel(new DefaultTreeSelectionModel() {
			/**
		 * 
		 */
			private static final long serialVersionUID = 1L;

			// Extend the implementation of the constructor, as if:
			/* public this() */{
				setSelectionModel(listSelectionModel);
			}
		});
		// Make the tree and table row heights the same.
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint the
	 * renderers and editors and overriding setBounds() below is not the right
	 * thing to do for an editor. Returning -1 for the editing row in this case,
	 * ensures the editor is never painted.
	 */
	@Override
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	//
	// The editor used to interact with tree nodes, a JTree.
	//

	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
			return tree;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}

	public void setModel(TreeTableModel treeTableModel) {
		tree = new TreeTableCellRenderer(treeTableModel, this);

		// Install a tableModel representing the visible rows in the tree. 
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));
		tree.setSelectionModel(new DefaultTreeSelectionModel() {

			private static final long serialVersionUID = 1L;

			// Extend the implementation of the constructor, as if:
			/* public this() */ {
				setSelectionModel(listSelectionModel);
			}
		});
		// Make the tree and table row heights the same. 
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor. 
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));
	}

	public TreeTableCellRenderer getTree() {
		return tree;
	}

}
