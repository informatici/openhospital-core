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
package org.isf.xmpp.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.isf.xmpp.service.Server;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

public class ComplexCellRender extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;
	protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
	Server server;
	ImageIcon online= new ImageIcon("rsc/icons/greenlight_label.png");
	ImageIcon offline= new ImageIcon("rsc/icons/greylight_label.png");

	public ComplexCellRender(Server server2) {
		server = server2;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		Color theForeground = null;

		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		Roster roster = server.getRoster();
		Presence presence;
		presence = roster.getPresence(((RosterEntry) value).getUser());
		if (presence.isAvailable()) {
			renderer.setIcon(online);
			renderer.setFont(new Font("Arial", Font.BOLD, 14));
		} else {
			renderer.setIcon(offline);
			renderer.setFont(new Font("Arial", Font.ITALIC, 14));
			renderer.setForeground(Color.GRAY);

		}
		if (!isSelected) {
			renderer.setForeground(theForeground);
		}

		renderer.setText(((RosterEntry) value).getName());
		return renderer;
	}

}
