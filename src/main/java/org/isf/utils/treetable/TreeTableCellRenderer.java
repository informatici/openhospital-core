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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;

// 
// The renderer used to display the tree nodes, a JTree.  
//
public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	protected int visibleRow;
	protected JTreeTable jTreeTable;
	
	public TreeTableCellRenderer(TreeModel model, JTreeTable jtreetable) { 
		super(model); 
		this.jTreeTable = jtreetable;
	}

	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, this.jTreeTable.getHeight());
	}

	@Override
	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		visibleRow = row;
		return this;
	}

}
