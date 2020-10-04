/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.xmpp.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class XmppData {
	public static String DOMAIN;
	public static int PORT;
	private static XmppData xmppData;

	private XmppData() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("rsc" + File.separator + "xmpp.properties"));
			DOMAIN = p.getProperty("DOMAIN");
			PORT = Integer.parseInt(p.getProperty("PORT"));

		} catch (Exception e) {
			DOMAIN = "127.0.0.1";
			PORT = 5222;
		}

	}

	public static XmppData getXmppData() {
		if (xmppData == null) {
			xmppData = new XmppData();
		}
		return xmppData;
	}

}
